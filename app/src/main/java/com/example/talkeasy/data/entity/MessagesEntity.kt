package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = Talks::class,
            parentColumns = ["id"],
            childColumns = ["talkId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["talkId"])]
)
data class Messages(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val talkId: Int,
    val text: String,
    val createdAt: LocalDateTime
)
