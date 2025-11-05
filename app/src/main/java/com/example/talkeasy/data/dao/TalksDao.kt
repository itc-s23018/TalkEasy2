package com.example.talkeasy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.talkeasy.data.entity.Talks

@Dao
interface TalksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalk(talk: Talks): Long

    @Query("UPDATE talks SET title = :newTitle WHERE id = :talkId")
    suspend fun updateTitle(talkId: Int, newTitle: String)

    @Delete
    suspend fun deleteTalk(talk: Talks)

    @Query("SELECT * FROM talks WHERE id = :id")
    suspend fun getTalkById(id: Int): Talks?

    @Query("SELECT * FROM talks ORDER BY createdAt DESC")
    suspend fun getAllTalks(): List<Talks>
}