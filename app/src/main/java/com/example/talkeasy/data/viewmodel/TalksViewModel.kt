package com.example.talkeasy.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.entity.InputType
import com.example.talkeasy.data.entity.Messages
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.entity.Words   // ← これを忘れずに import
import com.example.talkeasy.data.repository.TalksRepository
import com.example.talkeasy.gemini.GeminiText
import com.example.talkeasy.gemini.GeminiVoice
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class TalksViewModel @Inject constructor(
    private val repository: TalksRepository
) : ViewModel() {

    // タイトル・トーク一覧・メッセージ一覧
    private val _talkTitle = MutableStateFlow("新しいトーク")
    val talkTitle: StateFlow<String> = _talkTitle

    private val _talks = MutableStateFlow<List<Talks>>(emptyList())
    val talks: StateFlow<List<Talks>> = _talks

    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages: StateFlow<List<Messages>> = _messages

    // 補正中メッセージ（アニメーション表示用）
    private val _tempMessage = MutableStateFlow<Messages?>(null)
    val tempMessage: StateFlow<Messages?> = _tempMessage

    // AI返答候補とローディング状態
    private val _aiSuggestions = MutableStateFlow<List<String>>(emptyList())
    val aiSuggestions: StateFlow<List<String>> = _aiSuggestions

    private val _isGeneratingSuggestions = MutableStateFlow(false)
    val isGeneratingSuggestions: StateFlow<Boolean> = _isGeneratingSuggestions

    init {
        cleanUpOldTalks()
        loadAllTalks()
    }

    fun loadTalk(talkId: Int) {
        viewModelScope.launch {
            val talk = repository.getTalk(talkId)
            _talkTitle.value = talk?.title?.takeIf { it.isNotBlank() } ?: "新しいトーク"
        }
    }

    fun updateTalkTitle(talkId: Int, newTitle: String) {
        viewModelScope.launch {
            repository.updateTalkTitle(talkId, newTitle)
            _talkTitle.value = newTitle
            loadAllTalks()
        }
    }

    fun createNewTalk(onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val newId = repository.createTalk()
            cleanUpOldTalks()
            onCreated(newId)
            loadAllTalks()
        }
    }

    fun deleteTalk(talk: Talks) {
        viewModelScope.launch {
            repository.deleteTalk(talk)
            loadAllTalks()
        }
    }

    fun cleanUpOldTalks() {
        viewModelScope.launch {
            repository.deleteTalksOlderThanAWeek()
        }
    }

    fun loadAllTalks() {
        viewModelScope.launch {
            _talks.value = repository.getAllTalks()
        }
    }

    fun loadMessages(talkId: Int) {
        viewModelScope.launch {
            _messages.value = repository.getMessagesForTalk(talkId)
        }
    }

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

    // ✅ dbWords を List<Words> で受け取る
    fun correctWithFullHistory(talkId: Int, rawText: String, dbWords: List<Words>) {
        viewModelScope.launch {
            val temp = Messages(
                talkId = talkId,
                text = rawText,
                createdAt = LocalDateTime.now(),
                inputType = InputType.VOICE
            )
            _tempMessage.value = temp

            val historyTexts = _messages.value.map { it.text }

            GeminiVoice.correctSpeechTextWithContext(
                rawText = rawText,
                history = historyTexts,
                dbWords = dbWords,   // ← List<Words> を渡す
                onResult = { correctedText ->
                    sendMessage(talkId, correctedText, InputType.VOICE)
                    _tempMessage.value = null
                },
                onError = { error ->
                    sendMessage(talkId, rawText, InputType.VOICE)
                    _tempMessage.value = null
                }
            )
        }
    }

    // ✅ AIによる返答候補生成
    fun generateReplySuggestions() {
        val historyTexts = _messages.value.map { it.text }
        if (historyTexts.isEmpty()) return

        _isGeneratingSuggestions.value = true

        GeminiText.suggestReplyToLatestMessage(
            messages = historyTexts,
            onResult = { replies ->
                _aiSuggestions.value = replies
                _isGeneratingSuggestions.value = false
            },
            onError = { error ->
                _aiSuggestions.value = listOf("エラー: $error")
                _isGeneratingSuggestions.value = false
            }
        )
    }
}
