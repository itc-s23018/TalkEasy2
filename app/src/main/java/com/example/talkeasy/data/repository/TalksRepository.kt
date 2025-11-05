package com.example.talkeasy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.talkeasy.data.dao.TalksDao
import com.example.talkeasy.data.entity.Talks
import jakarta.inject.Inject
import java.time.LocalDateTime

class TalksRepository @Inject constructor(private val dao: TalksDao) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createTalk(): Int {
        val now = LocalDateTime.now()
        val talk = Talks(title = "新しいトーク", createdAt = now, updatedAt = now)
        return dao.insertTalk(talk).toInt()
    }

    suspend fun updateTalkTitle(talkId: Int, newTitle: String) {
        dao.updateTitle(talkId, newTitle)
    }

    suspend fun deleteTalk(talk: Talks) {
        dao.deleteTalk(talk)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteTalksOlderThanAWeek() {
        val threshold = LocalDateTime.now().minusWeeks(1)
        dao.deleteOldTalks(threshold)
    }

    suspend fun getTalk(id: Int): Talks? = dao.getTalkById(id)

    suspend fun getAllTalks(): List<Talks> = dao.getAllTalks()
}