package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "word")
data class WordsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val word: String,
    val wordRubi: String,
    val updatedAt: LocalDateTime,
    val category: String,
)