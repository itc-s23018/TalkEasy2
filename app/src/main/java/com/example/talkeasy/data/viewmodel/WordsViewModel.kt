package com.example.talkeasy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.dao.WordsDao
import com.example.talkeasy.data.entity.Words
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class WordsViewModel @Inject constructor(
    private val dao: WordsDao
) : ViewModel() {

    val allWords: StateFlow<List<Words>> =
        dao.getAllWords()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // ✅ トークごとの抽出候補をメモリ上で保持
    private val _extractedWordsMap = MutableStateFlow<Map<Int, List<Words>>>(emptyMap())
    val extractedWordsMap: StateFlow<Map<Int, List<Words>>> = _extractedWordsMap.asStateFlow()

    fun addWord(word: String, ruby: String, category: String) {
        viewModelScope.launch {
            dao.insertWord(
                Words(
                    word = word,
                    wordRuby = ruby,
                    category = category,
                    updatedAt = LocalDateTime.now()
                )
            )
        }
    }

    // ✅ 抽出候補を追加
    fun addExtractedWords(talkId: Int, newWords: List<Words>, allWords: List<Words>) {
        val current = _extractedWordsMap.value[talkId]?.toMutableList() ?: mutableListOf()
        newWords.forEach { newWord ->
            val alreadyInList = current.any { it.word == newWord.word }
            val alreadyInDb = allWords.any { it.word == newWord.word }
            if (!alreadyInList && !alreadyInDb) {
                current.add(newWord)
            }
        }
        _extractedWordsMap.value = _extractedWordsMap.value.toMutableMap().apply {
            put(talkId, current)
        }
    }


    // ✅ 保存後に候補リストから削除
    fun removeExtractedWord(talkId: Int, word: Words) {
        val current = _extractedWordsMap.value[talkId]?.toMutableList() ?: mutableListOf()
        current.remove(word)
        _extractedWordsMap.value = _extractedWordsMap.value.toMutableMap().apply {
            put(talkId, current)
        }
    }

    fun getWords(category: String): Flow<List<Words>> {
        return if (category == "All") {
            dao.getAllWords()
        } else {
            dao.getWordsByCategory(category)
        }
    }

    fun updateWord(id: Int, newWord: String, newRuby: String, newCategory: String) {
        viewModelScope.launch {
            val updated = Words(
                id = id,
                word = newWord,
                wordRuby = newRuby,
                category = newCategory,
                updatedAt = LocalDateTime.now()
            )
            dao.updateWord(updated)
        }
    }

    fun deleteWord(word: Words) {
        viewModelScope.launch {
            dao.deleteWord(word)
        }
    }
}
