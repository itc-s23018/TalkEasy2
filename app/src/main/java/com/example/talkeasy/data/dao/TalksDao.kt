package com.example.talkeasy.data.dao

import androidx.room.*
import com.example.talkeasy.data.entity.Talks
import java.time.LocalDateTime

// TalksテーブルにアクセスするためのDAO(Data Access Object)
@Dao
interface TalksDao {
    // 新しいトークを挿入する。競合した場合は置き換える
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalk(talk: Talks): Long

    // トークのタイトルを更新する
    @Query("UPDATE talks SET title = :newTitle WHERE id = :talkId")
    suspend fun updateTitle(talkId: Int, newTitle: String)

    // トークを削除する
    @Delete
    suspend fun deleteTalk(talk: Talks)

    // IDを指定してトークを削除する
    @Query("DELETE FROM talks WHERE id = :talkId")
    suspend fun deleteTalkById(talkId: Int)

    // 指定した日時より古いトークを削除する
    @Query("DELETE FROM talks WHERE createdAt < :threshold")
    suspend fun deleteOldTalks(threshold: LocalDateTime)

    // IDを指定してトークを取得する
    @Query("SELECT * FROM talks WHERE id = :id")
    suspend fun getTalkById(id: Int): Talks?

    // すべてのトークを作成日時の降順で取得する
    @Query("SELECT * FROM talks ORDER BY createdAt DESC")
    suspend fun getAllTalks(): List<Talks>
}
