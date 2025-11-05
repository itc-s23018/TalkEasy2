package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "talks")
data class Talks(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)