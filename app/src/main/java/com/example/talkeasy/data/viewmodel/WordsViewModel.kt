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

    // ✅ 抽出候補を追加 (未分類 categoryId = -1 を許容)
    fun addExtractedWords(talkId: Int, newWords: List<Words>, allWords: List<Words>) {
        val current = _extractedWordsMap.value[talkId]?.toMutableList() ?: mutableListOf()

        newWords.forEach { newWord ->
            val alreadyInList = current.any { it.word == newWord.word }
            val alreadyInDb = allWords.any { it.word == newWord.word }

            // ✅ DBにも候補リストにも存在しない場合のみ追加
            if (!alreadyInList && !alreadyInDb) {
                current.add(newWord.copy(categoryId = -1)) // 仮カテゴリで追加
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

    fun getWords(categoryId: Int?): Flow<List<Words>> {
        return if (categoryId == null) {
            // null を「All」扱いにする
            dao.getAllWords()
        } else {
            dao.getWordsByCategory(categoryId)
        }
    }


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

    fun deleteWord(word: Words) {
        viewModelScope.launch {
            dao.deleteWord(word)
        }
    }
}
