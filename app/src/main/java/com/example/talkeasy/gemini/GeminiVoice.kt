package com.example.talkeasy.gemini

import android.util.Log
import com.example.talkeasy.BuildConfig
import com.example.talkeasy.data.entity.Words
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiVoice {
    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-2.5-flash"
    private val apiKey_voice = BuildConfig.API_KEY_VOICE

    private val ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

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
            .addHeader("x-goog-api-key", apiKey_voice)
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

    fun correctSpeechTextWithContext(
        rawText: String,
        history: List<String>,
        dbWords: List<Words>,   // ← List<String> → List<Words> に修正
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val historyText = history.joinToString("\n")

        // word と wordRuby を両方渡す
        val dictionaryText = dbWords.joinToString(", ") { "${it.word}(${it.wordRuby})" }

        val prompt = """
        あなたのタスクは「最新の入力の文」を自然な日本語に補正することです。
        これまでの会話履歴とマイ辞書を参考にして、
        会話の流れに沿った自然な文になるようにしてください。
        補正対象は必ず最新の入力のみです。

        会話履歴:
        $historyText

        マイ辞書:
        $dictionaryText

        最新の入力: 「$rawText」

        補正後の文:
    """.trimIndent()

        generateText(
            prompt = prompt,
            onResult = { results: List<String> ->
                onResult(results.firstOrNull() ?: rawText)
            },
            onError = onError
        )
    }

}
