package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    // 主キー。
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // カテゴリ名
    val name: String
)
