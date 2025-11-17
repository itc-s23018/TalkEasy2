package com.example.talkeasy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.talkeasy.data.dao.MessagesDao
import com.example.talkeasy.data.dao.TalksDao
import com.example.talkeasy.data.entity.Messages
import com.example.talkeasy.data.entity.Talks
import jakarta.inject.Inject
import java.time.LocalDateTime

class TalksRepository @Inject constructor(
    private val talkdao: TalksDao,
    private val messagesDao: MessagesDao
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createTalk(): Int {
        val now = LocalDateTime.now()
        val talk = Talks(title = "新しいトーク", createdAt = now, updatedAt = now)
        return talkdao.insertTalk(talk).toInt()
    }

    suspend fun updateTalkTitle(talkId: Int, newTitle: String) {
        talkdao.updateTitle(talkId, newTitle)
    }

    suspend fun insertMessage(message: Messages) {
        messagesDao.insertMessage(message)
    }

    suspend fun getMessagesForTalk(talkId: Int): List<Messages> {
        return messagesDao.getMessagesForTalk(talkId)
    }

    suspend fun deleteTalk(talk: Talks) {
        talkdao.deleteTalk(talk)
    }

    suspend fun deleteTalkById(talkId: Int) {
        talkdao.deleteTalkById(talkId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteTalksOlderThanAWeek() {
        val threshold = LocalDateTime.now().minusWeeks(1)
        talkdao.deleteOldTalks(threshold)
    }

    suspend fun getTalk(id: Int): Talks? = talkdao.getTalkById(id)

    suspend fun getAllTalks(): List<Talks> = talkdao.getAllTalks()
}