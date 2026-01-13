package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "user",
    indices = [Index(value = ["lastName", "firstName"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val user_Id: Int = 1, // 自動採番されるユーザーID（主キー）

    val lastName: String,        // 名
    val lastNameRuby: String,    // 名（ルビ）
    val firstName: String,       // 性
    val firstNameRuby: String,   // 性（ルビ）

    val aiAssist: Boolean = false // AIアシスタント機能の使用可否フラグ
)