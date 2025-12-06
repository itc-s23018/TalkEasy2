package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "auth_tokens")
data class AuthToken(
    @PrimaryKey val uid: String,   // 1アプリ1ユーザーなら uid をキーにする
    val idToken: String,
    val createdAt: String = LocalDateTime.now().toString()
)
