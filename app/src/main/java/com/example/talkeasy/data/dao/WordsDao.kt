package com.example.talkeasy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.talkeasy.data.entity.Words

@Dao
interface WordsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Words): Long

    @Update
    suspend fun updateWord(word: Words)

    @Delete
    suspend fun deleteWord(word: Words)

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Int): Words?

    @Query("SELECT * FROM words WHERE userId = :userId ORDER BY updatedAt DESC")
    suspend fun getWordsByUser(userId: String): List<Words>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY updatedAt DESC")
    suspend fun getWordsByCategory(category: String): List<Words>
}