package com.example.talkeasy.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.talkeasy.data.DeleteTalkWorker
import com.example.talkeasy.data.entity.InputType
import com.example.talkeasy.data.entity.Messages
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.repository.TalksRepository
import com.example.talkeasy.gemini.GeminiText
import com.example.talkeasy.gemini.GeminiVoice
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@HiltViewModel
class TalksViewModel @Inject constructor(
    private val repository: TalksRepository,
    @ApplicationContext private val context: Context   // ✅ 修正: Qualifierを追加
) : ViewModel() {

    private val _talkTitle = MutableStateFlow("新しいトーク")
    val talkTitle: StateFlow<String> = _talkTitle

    private val _talks = MutableStateFlow<List<Talks>>(emptyList())
    val talks: StateFlow<List<Talks>> = _talks

    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages: StateFlow<List<Messages>> = _messages

    private val _tempMessage = MutableStateFlow<Messages?>(null)
    val tempMessage: StateFlow<Messages?> = _tempMessage

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

            // ✅ WorkManagerで1週間後に削除タスクを登録
            val workRequest = OneTimeWorkRequestBuilder<DeleteTalkWorker>()
                .setInitialDelay(7, TimeUnit.DAYS)
                .setInputData(workDataOf("talkId" to newId))
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)

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

    fun correctWithFullHistory(
        talkId: Int,
        rawText: String,
        dbWords: List<Words>,
        user: User?
    ) {
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
                dbWords = dbWords,
                user = user,
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

    fun generateReplySuggestions(allWords: List<Words>) {
        val historyTexts = _messages.value.map { it.text }
        if (historyTexts.isEmpty()) return

        _isGeneratingSuggestions.value = true

        GeminiText.suggestReplyToLatestMessage(
            messages = historyTexts,
            savedWords = allWords,
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
