package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "auth_tokens")
data class AuthToken(
    @PrimaryKey
    val uid: String,   // Firebase Authentication の UID（ユーザー識別子）

    val idToken: String, // Google Sign-In で取得した ID トークン（Gemini API 呼び出しに使用）

    val createdAt: String = LocalDateTime.now().toString() // 保存時刻
)
