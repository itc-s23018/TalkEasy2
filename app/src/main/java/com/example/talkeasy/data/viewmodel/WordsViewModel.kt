package com.example.talkeasy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.dao.WordsDao
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.gemini.GeminiApiService
import com.example.talkeasy.gemini.GeminiRequest
import com.example.talkeasy.gemini.GeminiResponse
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

@HiltViewModel
class WordsViewModel @Inject constructor(
    private val dao: WordsDao,
    private val geminiApi: GeminiApiService   // ✅ Retrofit経由でGemini呼び出し
) : ViewModel() {

    /** 全ての単語を監視 */
    val allWords: StateFlow<List<Words>> =
        dao.getAllWords()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /** トークごとの抽出候補を保持 */
    private val _extractedWordsMap = MutableStateFlow<Map<Int, List<Words>>>(emptyMap())
    val extractedWordsMap: StateFlow<Map<Int, List<Words>>> = _extractedWordsMap.asStateFlow()

    /** 単語をDBに追加 */
    fun addWord(word: String, ruby: String, categoryId: Int) {
        viewModelScope.launch {
            dao.insertWord(
                Words(
                    word = word,
                    wordRuby = ruby,
                    categoryId = categoryId,
                    updatedAt = LocalDateTime.now()
                )
            )
        }
    }

    /** 抽出候補を追加 (未分類 categoryId = -1) */
    fun addExtractedWords(talkId: Int, newWords: List<Words>, allWords: List<Words>) {
        val current = _extractedWordsMap.value[talkId]?.toMutableList() ?: mutableListOf()

        newWords.forEach { newWord ->
            val alreadyInList = current.any { it.word == newWord.word }
            val alreadyInDb = allWords.any { it.word == newWord.word }

            if (!alreadyInList && !alreadyInDb) {
                current.add(newWord.copy(categoryId = -1))
            }
        }

        _extractedWordsMap.value = _extractedWordsMap.value.toMutableMap().apply {
            put(talkId, current)
        }
    }

    /** 抽出候補を削除 */
    fun removeExtractedWord(talkId: Int, word: Words) {
        val current = _extractedWordsMap.value[talkId]?.toMutableList() ?: mutableListOf()
        current.remove(word)
        _extractedWordsMap.value = _extractedWordsMap.value.toMutableMap().apply {
            put(talkId, current)
        }
    }

    /** カテゴリ別に単語を取得 */
    fun getWords(categoryId: Int?): Flow<List<Words>> {
        return if (categoryId == null) {
            dao.getAllWords()
        } else {
            dao.getWordsByCategory(categoryId)
        }
    }

    /** 単語を更新 */
    fun updateWord(id: Int, newWord: String, newRuby: String, newCategoryId: Int) {
        viewModelScope.launch {
            val updated = Words(
                id = id,
                word = newWord,
                wordRuby = newRuby,
                categoryId = newCategoryId,
                updatedAt = LocalDateTime.now()
            )
            dao.updateWord(updated)
        }
    }

    /** 単語を削除 */
    fun deleteWord(word: Words) {
        viewModelScope.launch {
            dao.deleteWord(word)
        }
    }

    /** ✅ サーバー経由で用語抽出 */
    fun extractWordsFromServer(talkId: Int, history: List<String>, allWords: List<Words>) {
        viewModelScope.launch {
            val idToken = getIdToken() ?: return@launch
            val request = GeminiRequest(
                idToken = idToken,
                prompt = "",
                mode = "word",
                history = history
            )
            try {
                val response: GeminiResponse = geminiApi.extractWords(request)
                Log.d("WordsViewModel", "GeminiWord Response: $response")
                val newWords = response.extractWords()
                addExtractedWords(talkId, newWords, allWords)
            } catch (e: Exception) {
                Log.e("WordsViewModel", "GeminiWord Error: ${e.message}", e)
            }
        }
    }

    /** Firebase ID Token を取得 */
    private suspend fun getIdToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return user.getIdToken(false).await().token
    }
}