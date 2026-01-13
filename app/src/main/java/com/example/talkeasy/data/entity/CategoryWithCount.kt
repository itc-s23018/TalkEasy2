package com.example.talkeasy.data.entity

// カテゴリと、そのカテゴリに属する単語数を保持するためのデータクラス
data class CategoryWithCount(
    // カテゴリ情報
    val category: Category,
    // そのカテゴリに登録されている単語の数
    val wordCount: Int
)