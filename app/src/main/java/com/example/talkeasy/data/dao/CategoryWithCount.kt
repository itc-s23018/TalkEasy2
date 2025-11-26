package com.example.talkeasy.data.dao

import com.example.talkeasy.data.entity.Category

data class CategoryWithCount(
    val category: Category,
    val wordCount: Int
)
