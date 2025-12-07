package com.example.talkeasy.gemini

import com.example.talkeasy.data.entity.Words

data class GeminiResponse(
    val uid: String,
    val data: Map<String, Any?>
) {
    fun extractCorrectedText(): String? {
        val candidates = data["candidates"] as? List<*> ?: return null
        val firstCandidate = candidates.firstOrNull() as? Map<*, *> ?: return null
        val content = firstCandidate["content"] as? Map<*, *> ?: return null
        val parts = content["parts"] as? List<*> ?: return null
        val firstPart = parts.firstOrNull() as? Map<*, *> ?: return null
        return firstPart["text"] as? String
    }

    fun extractReplySuggestions(): List<String> {
        val candidates = data["candidates"] as? List<*> ?: return emptyList()
        val texts = candidates.mapNotNull { candidate ->
            val content = (candidate as? Map<*, *>)?.get("content") as? Map<*, *>
            val parts = content?.get("parts") as? List<*>
            val firstPart = parts?.firstOrNull() as? Map<*, *>
            firstPart?.get("text") as? String
        }
        if (texts.isEmpty()) return emptyList()

        return texts.joinToString("\n")
            .replace("```", " ")
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { line ->
                line.replace(Regex("^((返答\\s*\\d+\\s*:)|(-|•|\\*|\\d+\\.|#+)\\s*)"), "")
                    .trim()
            }
            .filterNot { it.contains("返答の提案") || it.contains("出力仕様") }
            .filter { it.any { ch -> ch.code >= 0x3040 } }
            .take(3)
    }

    fun extractWords(): List<Words> {
        val candidates = data["candidates"] as? List<*> ?: return emptyList()
        val firstCandidate = candidates.firstOrNull() as? Map<*, *> ?: return emptyList()
        val content = firstCandidate["content"] as? Map<*, *> ?: return emptyList()
        val parts = content["parts"] as? List<*> ?: return emptyList()
        val firstPart = parts.firstOrNull() as? Map<*, *> ?: return emptyList()
        val text = firstPart["text"] as? String ?: return emptyList()

        return try {
            val jsonArray = org.json.JSONArray(text)
            val now = java.time.LocalDateTime.now()
            (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                Words(
                    word = obj.optString("word", ""),
                    wordRuby = obj.optString("wordRuby", ""),
                    updatedAt = now,
                    categoryId = -1
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
