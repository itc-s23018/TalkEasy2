package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "words",
    // 外部キー制約: categoryIdはcategoriesテーブルのidを参照する
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            // 親となるCategoryが削除された場合、関連するWordsも削除する
            onDelete = ForeignKey.CASCADE
        )
    ],
    // categoryIdにインデックスを張り、クエリを高速化する
    indices = [Index("categoryId")]
)
data class Words(
    // 主キー。自動生成される
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // 単語
    val word: String,
    // 単語のふりがな
    val wordRuby: String,
    // 更新日時
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    // 関連するカテゴリのID
    val categoryId: Int
)
