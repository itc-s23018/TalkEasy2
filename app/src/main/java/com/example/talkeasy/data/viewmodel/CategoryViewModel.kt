package com.example.talkeasy.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.dao.CategoryDao
import com.example.talkeasy.data.dao.WordsDao
import com.example.talkeasy.data.entity.Category
import com.example.talkeasy.data.entity.CategoryWithCount
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val wordsDao: WordsDao
) : ViewModel() {

    val categories: StateFlow<List<Category>> =
        categoryDao.getAllCategories()
            .map { dbCategories ->
                listOf(Category(id = -1, name = "All")) + dbCategories
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val categoriesWithCount: StateFlow<List<CategoryWithCount>> =
        categoryDao.getAllCategories()
            .flatMapLatest { dbCategories ->
                combine(
                    dbCategories.map { category ->
                        wordsDao.getWordCount(category.id).map { count ->
                            CategoryWithCount(category, count)
                        }
                    }
                ) { it.toList() }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    suspend fun addCategory(name: String): Int {
        return categoryDao.insertCategory(Category(name = name)).toInt()
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { categoryDao.deleteCategory(category) }
    }

    fun getWordsByCategory(categoryId: Int): Flow<List<com.example.talkeasy.data.entity.Words>> {
        return wordsDao.getWordsByCategory(categoryId)
    }
}
