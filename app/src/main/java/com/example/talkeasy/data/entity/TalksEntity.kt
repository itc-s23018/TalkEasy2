package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

// "talks"テーブルを表すエンティティ
@Entity(tableName = "talks")
data class Talks(
    // 主キー。自動生成される
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // トークのタイトル
    val title: String,
    // 作成日時
    val createdAt: LocalDateTime,
    // 更新日時
    val updatedAt: LocalDateTime,
)