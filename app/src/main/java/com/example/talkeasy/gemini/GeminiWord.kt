package com.example.talkeasy.gemini

import android.util.Log
import com.example.talkeasy.BuildConfig
import com.example.talkeasy.data.entity.Words
import org.json.JSONArray
import java.time.LocalDateTime

object GeminiWord {
    private val client = GeminiClient(BuildConfig.API_KEY_TEXT)

    fun extractTermsFromHistory(
        history: List<String>,
        onResult: (List<Words>) -> Unit,
        onError: (String) -> Unit
    ) {
        val historyText = history.joinToString("\n")

        val prompt = """
        以下は会話履歴です。この中から専門用語を抽出してください。
        出力は必ず JSON 配列形式のみで返してください。
        各要素は {"word": "用語", "wordRuby": "読み仮名"} の形にしてください。
        他の文字列や説明は返さないでください。

        会話履歴:
        $historyText

        専門用語リスト(JSON):
        """.trimIndent()

        client.generateText(
            prompt = prompt,
            onResult = { results ->
                try {
                    val rawText = results.joinToString("\n").trim()
                    Log.d("GeminiWord", "Gemini応答(JSON): $rawText")

                    // 余計なコードブロックマーカーを除去
                    val cleaned = rawText
                        .removePrefix("```json")
                        .removePrefix("```")
                        .removeSuffix("```")
                        .trim()

                    if (!cleaned.startsWith("[")) {
                        onError("JSON配列ではない応答: $cleaned")
                        return@generateText
                    }

                    val jsonArray = JSONArray(cleaned)
                    val now = LocalDateTime.now()

                    val words = (0 until jsonArray.length()).map { i ->
                        val obj = jsonArray.getJSONObject(i)
                        Words(
                            word = obj.getString("word"),
                            wordRuby = obj.optString("wordRuby", ""),
                            updatedAt = now,
                            categoryId = -1
                        )
                    }

                    Log.d("GeminiWord", "抽出結果: $words")
                    onResult(words) // ✅ 保存済みチェックは ViewModel 側で行う
                } catch (e: Exception) {
                    Log.e("GeminiWord", "JSON解析失敗: ${e.message}")
                    onError("JSON解析失敗: ${e.message}")
                }
            },
            onError = { error ->
                Log.e("GeminiWord", "Gemini呼び出し失敗: $error")
                onError(error)
            }
        )
    }
}

