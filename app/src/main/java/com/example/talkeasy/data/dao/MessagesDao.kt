package com.example.talkeasy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.talkeasy.data.entity.Messages

@Dao
interface MessagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Messages): Long

    @Query("SELECT * FROM messages WHERE talkId = :talkId ORDER BY createdAt ASC")
    suspend fun getMessagesForTalk(talkId: Int): List<Messages>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: Int): Messages?

    @Delete
    suspend fun deleteMessage(message: Messages)

    @Query("DELETE FROM messages WHERE talkId = :talkId")
    suspend fun deleteMessagesByTalkId(talkId: Int)
}