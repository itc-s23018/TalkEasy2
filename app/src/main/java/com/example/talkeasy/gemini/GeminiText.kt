package com.example.talkeasy.gemini

import com.example.talkeasy.BuildConfig

object GeminiText {
    private val client = GeminiClient(BuildConfig.API_KEY_TEXT)

    fun generateText(
        prompt: String,
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) = client.generateText(prompt, onResult, onError)

    fun suggestReplyToLatestMessage(
        messages: List<String>,
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        if (messages.isEmpty()) {
            onError("メッセージ履歴が空です")
            return
        }

        val historyText = messages.dropLast(1).joinToString("\n")
        val latestMessage = messages.last()

        val prompt = """
        以下は会話の履歴です。最後のメッセージに対して、自然で適切な返答を3つ提案してください。

        会話履歴:
        $historyText

        最後のメッセージ: 「$latestMessage」

        返答の提案:
    """.trimIndent()

        GeminiVoice.generateText(prompt, onResult, onError)
    }
}
