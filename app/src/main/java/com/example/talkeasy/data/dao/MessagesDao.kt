package com.example.talkeasy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.talkeasy.data.entity.Messages

@Dao
interface MessagesDao {
    // 新しいメッセージを挿入する。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Messages): Long

    // 指定されたトークIDのすべてのメッセージを作成日時の昇順で取得する
    @Query("SELECT * FROM messages WHERE talkId = :talkId ORDER BY createdAt ASC")
    suspend fun getMessagesForTalk(talkId: Int): List<Messages>

    // IDを指定してメッセージを取得する
    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: Int): Messages?

    // メッセージを削除する
    @Delete
    suspend fun deleteMessage(message: Messages)

    // 指定されたトークIDのすべてのメッセージを削除する
    @Query("DELETE FROM messages WHERE talkId = :talkId")
    suspend fun deleteMessagesByTalkId(talkId: Int)
}