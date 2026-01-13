package com.example.talkeasy.data.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.talkeasy.data.DeleteTalkWorker
import com.example.talkeasy.data.entity.Category
import com.example.talkeasy.data.entity.InputType
import com.example.talkeasy.data.entity.Messages
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.repository.TalksRepository
import com.example.talkeasy.gemini.GeminiApiService
import com.example.talkeasy.gemini.GeminiRequest
import com.example.talkeasy.gemini.GeminiResponse
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

// HiltによるViewModel注入のためのアノテーション
@HiltViewModel
// TalksViewModel: トークとメッセージの管理、およびGemini APIとの連携を行う
class TalksViewModel @Inject constructor(
    private val repository: TalksRepository, // TalksRepositoryのインスタンス
    private val geminiApi: GeminiApiService, // GeminiApiServiceのインスタンス
    @ApplicationContext private val context: Context // アプリケーションコンテキスト
) : ViewModel() {

    // 現在のトークのタイトルを保持するStateFlow
    private val _talkTitle = MutableStateFlow("新しいトーク")
    val talkTitle: StateFlow<String> = _talkTitle

    // すべてのトークのリストを保持するStateFlow
    private val _talks = MutableStateFlow<List<Talks>>(emptyList())
    val talks: StateFlow<List<Talks>> = _talks

    // 現在のトークのメッセージリストを保持するStateFlow
    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages: StateFlow<List<Messages>> = _messages

    // AIによる修正中の一次的なメッセージを保持するStateFlow
    private val _tempMessage = MutableStateFlow<Messages?>(null)
    val tempMessage: StateFlow<Messages?> = _tempMessage

    // AIによる返信提案のリストを保持するStateFlow
    private val _aiSuggestions = MutableStateFlow<List<String>>(emptyList())
    val aiSuggestions: StateFlow<List<String>> = _aiSuggestions

    // 返信提案を生成中かどうかを示すStateFlow
    private val _isGeneratingSuggestions = MutableStateFlow(false)
    val isGeneratingSuggestions: StateFlow<Boolean> = _isGeneratingSuggestions

    // 初期化時に古いトークをクリーンアップし、すべてのトークをロードする
    init {
        cleanUpOldTalks()
        loadAllTalks()
    }

    // 指定されたIDのトークをロードし、タイトルを更新する
    fun loadTalk(talkId: Int) {
        viewModelScope.launch {
            val talk = repository.getTalk(talkId)
            _talkTitle.value = talk?.title?.takeIf { it.isNotBlank() } ?: "新しいトーク"
        }
    }

    // トークのタイトルを更新する
    fun updateTalkTitle(talkId: Int, newTitle: String) {
        viewModelScope.launch {
            repository.updateTalkTitle(talkId, newTitle)
            _talkTitle.value = newTitle
            loadAllTalks() // タイトル更新後、全トークリストを再読み込み
        }
    }

    // 新しいトークを作成し、7日後に自動削除するWorkerをスケジュールする
    fun createNewTalk(onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val newId = repository.createTalk()

            val workRequest = OneTimeWorkRequestBuilder<DeleteTalkWorker>()
                .setInitialDelay(7, TimeUnit.DAYS)
                .setInputData(workDataOf("talkId" to newId))
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)

            onCreated(newId)
            loadAllTalks()
        }
    }

    // トークを削除する
    fun deleteTalk(talk: Talks) {
        viewModelScope.launch {
            repository.deleteTalk(talk)
            loadAllTalks()
        }
    }

    // 1週間以上前の古いトークを削除する
    fun cleanUpOldTalks() {
        viewModelScope.launch {
            repository.deleteTalksOlderThanAWeek()
        }
    }

    // すべてのトークをロードする
    fun loadAllTalks() {
        viewModelScope.launch {
            _talks.value = repository.getAllTalks()
        }
    }

    // 指定されたトークIDのメッセージをロードする
    fun loadMessages(talkId: Int) {
        viewModelScope.launch {
            _messages.value = repository.getMessagesForTalk(talkId)
        }
    }

    // メッセージを送信（DBに保存し、UIを更新）する
    fun sendMessage(talkId: Int, text: String, inputType: InputType) {
        viewModelScope.launch {
            val message = Messages(
                talkId = talkId,
                text = text,
                createdAt = LocalDateTime.now(),
                inputType = inputType
            )
            repository.insertMessage(message)
            _messages.update { current -> current + message }
        }
    }

    /** Firebase ID Token を取得 */
    private suspend fun getIdToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return user.getIdToken(false).await().token
    }

    /** ✅ 音声入力補正 (サーバー呼び出し版) */
    fun correctWithFullHistory(
        talkId: Int,
        rawText: String,
        dbWords: List<Words>,
        user: User?
    ) {
        viewModelScope.launch {
            // 補正中のテキストを一時的に表示
            val temp = Messages(
                talkId = talkId,
                text = rawText,
                createdAt = LocalDateTime.now(),
                inputType = InputType.VOICE
            )
            _tempMessage.value = temp

            val historyTexts = _messages.value.map { it.text }
            val idToken = getIdToken() ?: run {
                // IDトークンが取得できない場合は、補正せずにそのままメッセージを送信
                sendMessage(talkId, rawText, InputType.VOICE)
                _tempMessage.value = null
                return@launch
            }

            val request = GeminiRequest(
                idToken = idToken,
                prompt = rawText,
                mode = "voice",
                history = historyTexts,
                dbWords = dbWords,
                user = user
            )

            try {
                val response: GeminiResponse = geminiApi.correctVoice(request)
                Log.d("TalksViewModel", "GeminiVoice Response: $response")
                val correctedText = response.extractCorrectedText() ?: rawText
                sendMessage(talkId, correctedText, InputType.VOICE)
            } catch (e: Exception) {
                Log.e("TalksViewModel", "GeminiVoice Error: ${e.message}", e)
                sendMessage(talkId, rawText, InputType.VOICE) // エラー時も元のテキストを送信
            } finally {
                _tempMessage.value = null // 一時メッセージをクリア
            }
        }
    }

    /** ✅ 返答提案 (サーバー呼び出し版) */
    fun generateReplySuggestions(allWords: List<Words>, categories: List<Category>) {
        val historyTexts = _messages.value.map { it.text }
        if (historyTexts.isEmpty()) return

        _isGeneratingSuggestions.value = true

        viewModelScope.launch {
            val idToken = getIdToken() ?: run {
                _aiSuggestions.value = listOf("ログインが必要です")
                _isGeneratingSuggestions.value = false
                return@launch
            }

            val request = GeminiRequest(
                idToken = idToken,
                prompt = "", // 返信提案なのでプロンプトは空
                mode = "text",
                history = historyTexts,
                dbWords = allWords
            )

            try {
                val response: GeminiResponse = geminiApi.suggestReplies(request)
                Log.d("TalksViewModel", "GeminiText Response: $response")
                _aiSuggestions.value = response.extractReplySuggestions()
            } catch (e: Exception) {
                Log.e("TalksViewModel", "GeminiText Error: ${e.message}", e)
                _aiSuggestions.value = listOf("エラー: ${e.message}")
            } finally {
                _isGeneratingSuggestions.value = false
            }
        }
    }
}
