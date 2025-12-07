package com.example.talkeasy.gemini

import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.entity.Words

data class GeminiRequest(
    val idToken: String,
    val prompt: String,
    val mode: String = "voice",
    val history: List<String> = emptyList(),
    val dbWords: List<Words> = emptyList(),
    val user: User? = null
)
