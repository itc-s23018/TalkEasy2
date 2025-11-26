package com.example.talkeasy.data.dao

import androidx.room.*
import com.example.talkeasy.data.entity.Words
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM words ORDER BY updatedAt DESC")
    fun getAllWords(): Flow<List<Words>>

    @Query("SELECT * FROM words WHERE categoryId = :categoryId ORDER BY updatedAt DESC")
    fun getWordsByCategory(categoryId: Int): Flow<List<Words>>

}