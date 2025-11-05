package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "message",
    foreignKeys = [
        ForeignKey(
            entity = TalksEntity::class,
            parentColumns = ["id"],
            childColumns = ["talkId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["talkId"])]
)
data class MessagesEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val talkId: Int,
    val text: String,
    val createdAt: LocalDateTime,
)
