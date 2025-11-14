package com.example.talkeasy.gemini

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiClient(
    private val apiKey: String,
    private val modelName: String = "gemini-2.5-flash"
) {
    private val TAG = "GeminiClient"

    private val ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun generateText(
        prompt: String,
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        val payload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val mediaType = "application/json".toMediaType()
        val requestBody = payload.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(ENDPOINT)
            .addHeader("x-goog-api-key", apiKey)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "API呼び出し失敗: ${e.message}")
                onError("API呼び出し失敗: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val rawBody = response.body?.string()
                if (!response.isSuccessful || rawBody == null) {
                    onError("APIエラー: ${response.code}\n$rawBody")
                    return
                }

                try {
                    val json = JSONObject(rawBody)
                    val text = json
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    val suggestions = text
                        .split("\n")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    Log.d(TAG, "Gemini応答: $suggestions")
                    onResult(suggestions)
                } catch (e: Exception) {
                    Log.e(TAG, "レスポンス解析失敗: ${e.message}")
                    onError("レスポンス解析失敗: ${e.message}")
                }
            }
        })
    }
}
