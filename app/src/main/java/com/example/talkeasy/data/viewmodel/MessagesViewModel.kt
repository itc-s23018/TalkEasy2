package com.example.talkeasy.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.dao.MessagesDao
import com.example.talkeasy.data.entity.Messages
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val messagesDao: MessagesDao
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages: StateFlow<List<Messages>> = _messages

    fun loadMessages(talkId: Int) {
        viewModelScope.launch {
            _messages.value = messagesDao.getMessagesForTalk(talkId)
        }
    }

    fun sendMessage(talkId: Int, text: String) {
        viewModelScope.launch {
            val message = Messages(
                talkId = talkId,
                text = text,
                createdAt = LocalDateTime.now() // ← Date → LocalDateTime に戻す
            )
            messagesDao.insertMessage(message)
            loadMessages(talkId)
        }
    }

    fun deleteMessage(message: Messages) {
        viewModelScope.launch {
            messagesDao.deleteMessage(message)
            loadMessages(message.talkId)
        }
    }
}

