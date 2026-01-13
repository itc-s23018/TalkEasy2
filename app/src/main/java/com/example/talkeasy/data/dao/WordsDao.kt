package com.example.talkeasy.data.dao

import androidx.room.*
import com.example.talkeasy.data.entity.Words
import kotlinx.coroutines.flow.Flow

@Dao
interface WordsDao {
    // 新しい単語を挿入
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Words): Long

    // 単語を更新
    @Update
    suspend fun updateWord(word: Words)

    // 単語を削除
    @Delete
    suspend fun deleteWord(word: Words)

    // IDを指定して単語を取得
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Int): Words?

    // すべての単語を更新日時の降順で取得
    @Query("SELECT * FROM words ORDER BY updatedAt DESC")
    fun getAllWords(): Flow<List<Words>>

    // 指定されたカテゴリIDのすべての単語を更新日時の降順で取得
    @Query("SELECT * FROM words WHERE categoryId = :categoryId ORDER BY updatedAt DESC")
    fun getWordsByCategory(categoryId: Int): Flow<List<Words>>

    // ✅ カテゴリごとの件数を取得
    @Query("SELECT COUNT(*) FROM words WHERE categoryId = :categoryId")
    fun getWordCount(categoryId: Int): Flow<Int>
}
