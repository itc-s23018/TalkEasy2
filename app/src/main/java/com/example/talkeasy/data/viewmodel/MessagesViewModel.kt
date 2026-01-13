package com.example.talkeasy.data.viewmodel

import android.R.attr.inputType
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.dao.MessagesDao
import com.example.talkeasy.data.entity.InputType
import com.example.talkeasy.data.entity.Messages
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date

// HiltによるViewModel注入のためのアノテーション
@HiltViewModel
// MessagesViewModel: メッセージの管理を行う
class MessagesViewModel @Inject constructor(
    private val messagesDao: MessagesDao // MessagesDaoのインスタンス
) : ViewModel() {

    // メッセージのリストを保持するMutableStateFlow
    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    // 外部に公開する読み取り専用のStateFlow
    val messages: StateFlow<List<Messages>> = _messages

    // 指定されたトークIDのメッセージをロードする
    fun loadMessages(talkId: Int) {
        viewModelScope.launch {
            _messages.value = messagesDao.getMessagesForTalk(talkId)
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
            messagesDao.insertMessage(message)
            loadMessages(talkId) // メッセージ送信後、リストを再読み込み
        }
    }

    // メッセージを削除する
    fun deleteMessage(message: Messages) {
        viewModelScope.launch {
            messagesDao.deleteMessage(message)
            loadMessages(message.talkId) // メッセージ削除後、リストを再読み込み
        }
    }
}

