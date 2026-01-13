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
    private val modelName: String = "gemini-1.5-flash"
) {
    private val TAG = "GeminiClient"

    // APIのエンドポイントURL
    private val ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

    // タイムアウト設定済みのOkHttpクライアントインスタンス
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    //指定されたプロンプトを使用して、Gemini APIにテキスト生成をリクエスト
    fun generateText(
        prompt: String,
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        // Gemini APIのリクエストボディ(JSON)を作成
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

        // HTTPリクエストを作成
        val request = Request.Builder()
            .url(ENDPOINT)
            .addHeader("x-goog-api-key", apiKey) // APIキーをヘッダーに追加
            .post(requestBody)
            .build()

        // リクエストを非同期で実行
        client.newCall(request).enqueue(object : Callback {
            //リクエストが失敗した（ネットワークエラーなど）場合に呼ばれる
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "API呼び出し失敗: ${e.message}")
                onError("API呼び出し失敗: ${e.message}")
            }

            //サーバーからのレスポンスを受け取った場合に呼ばれる
            override fun onResponse(call: Call, response: Response) {
                val rawBody = response.body?.string()
                // レスポンスが不成功、またはボディが空の場合はエラー処理
                if (!response.isSuccessful || rawBody == null) {
                    onError("APIエラー: ${response.code}\n$rawBody")
                    return
                }

                try {
                    Log.d(TAG, "Raw Gemini Response: $rawBody")

                    // レスポンスのJSONをパース
                    val json = JSONObject(rawBody)
                    val candidates = json.optJSONArray("candidates") // 候補の配列を取得

                    if (candidates != null && candidates.length() > 0) {
                        // 最初の候補を取得
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        val parts = content?.optJSONArray("parts")

                        // 候補からテキスト部分を抽出
                        val text = if (parts != null && parts.length() > 0) {
                            parts.getJSONObject(0).optString("text", "")
                        } else {
                            content?.optString("text", "") ?: ""
                        }

                        // 結果を改行で分割し、空白を除去してリスト化
                        val suggestions = text
                            .split("\n")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }

                        Log.d(TAG, "Gemini応答: $suggestions")
                        onResult(suggestions)
                    } else {
                        // "candidates"フィールドが存在しないか空の場合はエラー
                        onError("candidates が空: $rawBody")
                    }
                } catch (e: Exception) {
                    // JSONパース中にエラーが発生した場合
                    Log.e(TAG, "レスポンス解析失敗: ${e.message}")
                    onError("レスポンス解析失敗: ${e.message}")
                }
            }
        })
    }
}
