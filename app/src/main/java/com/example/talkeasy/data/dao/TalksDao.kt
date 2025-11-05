package com.example.talkeasy.data.dao

import androidx.room.*
import com.example.talkeasy.data.entity.Talks
import java.time.LocalDateTime

@Dao
interface TalksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalk(talk: Talks): Long

    @Query("UPDATE talks SET title = :newTitle WHERE id = :talkId")
    suspend fun updateTitle(talkId: Int, newTitle: String)

    @Delete
    suspend fun deleteTalk(talk: Talks)

    @Query("DELETE FROM talks WHERE createdAt < :threshold")
    suspend fun deleteOldTalks(threshold: LocalDateTime)

    @Query("SELECT * FROM talks WHERE id = :id")
    suspend fun getTalkById(id: Int): Talks?

    @Query("SELECT * FROM talks ORDER BY createdAt DESC")
    suspend fun getAllTalks(): List<Talks>
}