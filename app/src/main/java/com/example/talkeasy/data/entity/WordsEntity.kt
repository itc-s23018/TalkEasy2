package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Words(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val wordRuby: String,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val categoryId: Int
)
