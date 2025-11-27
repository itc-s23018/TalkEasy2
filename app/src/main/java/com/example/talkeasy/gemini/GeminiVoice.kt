package com.example.talkeasy.gemini

import com.example.talkeasy.BuildConfig
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.entity.User   // ← ユーザー情報を追加

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
        user: User?,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val historyText = history.joinToString("\n")
        val dictionaryText = dbWords.joinToString(", ") { "${it.word}(${it.wordRuby})" }

        val userText = user?.let {
            "ユーザー名: ${it.lastName}${it.firstName} (${it.lastNameRuby} ${it.firstNameRuby})"
        } ?: ""

        val userTextSection = if (userText.isNotEmpty()) "ユーザー情報:\n$userText\n" else ""

        val prompt = """
            あなたのタスクは「最新の入力の文」を自然な日本語に補正することです。
            これまでの会話履歴、マイ辞書、ユーザーの名前情報を参考にして、
            会話の流れに沿った自然な文になるようにしてください。
            補正対象は必ず最新の入力のみです。
            
            会話履歴:
            $historyText
            
            マイ辞書:
            $dictionaryText
            
            $userTextSection
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
