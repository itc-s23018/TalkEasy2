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
    private val dao: WordsDao) : ViewModel() {

    val allWords: StateFlow<List<Words>> =
        dao.getAllWords()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun addWord(word: String, ruby: String, category: String) {
        viewModelScope.launch {
            dao.insertWord(
                Words(
                    word = word,
                    wordRubi = ruby,
                    category = category,
                    updatedAt = LocalDateTime.now()
                )
            )
        }
    }

    fun updateWord(word: Words) {
        viewModelScope.launch { dao.updateWord(word) }
    }

    fun deleteWord(word: Words) {
        viewModelScope.launch { dao.deleteWord(word) }
    }

    fun getWordsByCategory(category: String): Flow<List<Words>> =
        dao.getWordsByCategory(category)
}
