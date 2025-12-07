package com.example.talkeasy.gemini

import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApiService {

    @POST("api/geminiVoice")
    suspend fun correctVoice(@Body request: GeminiRequest): GeminiResponse

    @POST("api/geminiText")
    suspend fun suggestReplies(@Body request: GeminiRequest): GeminiResponse

    @POST("api/geminiWord")
    suspend fun extractWords(@Body request: GeminiRequest): GeminiResponse
}
