package com.example.talkeasy.gemini

import com.example.talkeasy.BuildConfig
import com.example.talkeasy.data.entity.Words

object GeminiVoice {
    private val client = GeminiClient(BuildConfig.API_KEY_VOICE)

    fun generateText(
        prompt: String,
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) = client.generateText(prompt, onResult, onError)

    fun correctSpeechTextWithContext(
        rawText: String,
        history: List<String>,
        dbWords: List<Words>,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val historyText = history.joinToString("\n")
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
            onResult = { results -> onResult(results.firstOrNull() ?: rawText) },
            onError = onError
        )
    }
}
