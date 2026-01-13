package com.example.talkeasy.gemini

import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApiService {

    //音声認識結果のテキストを、文脈を考慮して修正するAPIを呼び出す。

    @POST("api/geminiVoice")
    suspend fun correctVoice(@Body request: GeminiRequest): GeminiResponse

    //会話履歴に基づいて、返信候補を生成するAPIを呼び出す
    @POST("api/geminiText")
    suspend fun suggestReplies(@Body request: GeminiRequest): GeminiResponse

    //会話履歴から、重要な単語や専門用語を抽出するAPIを呼び出す
    @POST("api/geminiWord")
    suspend fun extractWords(@Body request: GeminiRequest): GeminiResponse
}
