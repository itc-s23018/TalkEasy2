package com.example.talkeasy.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.entity.Messages
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.repository.TalksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Duration

@HiltViewModel
class TalksViewModel @Inject constructor(
    private val repository: TalksRepository
) : ViewModel() {

    private val _talkTitle = MutableStateFlow("新しいトーク")
    val talkTitle: StateFlow<String> = _talkTitle

    private val _talks = MutableStateFlow<List<Talks>>(emptyList())
    val talks: StateFlow<List<Talks>> = _talks

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

    fun sendMessage(talkId: Int, text: String) {
        viewModelScope.launch {
            val message = Messages(
                talkId = talkId,
                text = text,
                createdAt = LocalDateTime.now()
            )
            repository.insertMessage(message)

            // 既存リストに即時追加（DB再取得を待たずに表示）
            _messages.update { current -> current + message }
        }
    }


    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages: StateFlow<List<Messages>> = _messages

    fun loadMessages(talkId: Int) {
        viewModelScope.launch {
            _messages.value = repository.getMessagesForTalk(talkId)
        }
    }
}
