package com.example.talkeasy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.talkeasy.data.dao.TalksDao
import com.example.talkeasy.data.entity.Talks
import jakarta.inject.Inject
import java.time.LocalDateTime

class TalksRepository @Inject constructor(private val dao: TalksDao) {
    suspend fun createTalk(title: String): Int {
        val now = LocalDateTime.now()
        val talk = Talks(title = title, createdAt = now, updatedAt = now)
        return dao.insertTalk(talk).toInt()
    }

    suspend fun getTalk(id: Int): Talks? = dao.getTalkById(id)

    suspend fun getAllTalks(): List<Talks> = dao.getAllTalks()
}
