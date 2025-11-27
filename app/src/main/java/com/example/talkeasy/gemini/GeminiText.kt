package com.example.talkeasy.gemini

import com.example.talkeasy.BuildConfig
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.entity.Category

object GeminiText {
    private val client = GeminiClient(BuildConfig.API_KEY_TEXT)

    fun generateText(
        prompt: String,
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) = client.generateText(prompt, onResult, onError)

    fun suggestReplyToLatestMessage(
        messages: List<String>,
        savedWords: List<Words>,
        categories: List<Category>,
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        if (messages.isEmpty()) {
            onError("メッセージ履歴が空です")
            return
        }

        val historyText = messages.dropLast(1).joinToString("\n")
        val latestMessage = messages.last()

        val categoryMap = categories.associateBy({ it.id }, { it.name })
        val wordsText = savedWords.joinToString("\n") { w ->
            val categoryName = categoryMap[w.categoryId] ?: "Unknown"
            "- ${w.word} (${w.wordRuby}) [$categoryName]"
        }

        val prompt = """
        以下は会話の履歴と保存済みの専門用語です。
        最後のメッセージに対して、自然で適切な返答を3つ提案してください。
        可能であれば保存済み用語を活用してください。

        会話履歴:
        $historyText

        保存済み用語:
        $wordsText

        最後のメッセージ: 「$latestMessage」

        返答の提案:
    """.trimIndent()

        GeminiVoice.generateText(
            prompt = prompt,
            onResult = { results ->
                val rawText = results.joinToString("\n")
                val suggestions = rawText
                    .split("\n")
                    .map { it.trim() }
                    .filter { it.contains("「") && it.contains("」") }
                    .mapNotNull { line ->
                        val start = line.indexOf("「")
                        val end = line.indexOf("」")
                        if (start != -1 && end != -1 && end > start) {
                            line.substring(start + 1, end)
                        } else null
                    }
                    .take(3)
                onResult(suggestions)
            },
            onError = onError
        )
    }
}