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
        最後のメッセージに対して、会話の流れに沿った自然な返答を3つ提案してください。
        返答はチャット風で、短すぎず長すぎない一文程度にしてください。
        保存済み用語を活用できる場合は取り入れてください。

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
                            val sentence = line.substring(start + 1, end).trim()
                            if (sentence.length in 10..60) sentence else null
                        } else null
                    }
                    .take(3)
                onResult(suggestions)
            },
            onError = onError
        )
    }
}
