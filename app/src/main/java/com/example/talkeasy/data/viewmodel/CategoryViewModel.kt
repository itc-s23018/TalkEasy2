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
    private val categoryDao: CategoryDao, // CategoryDaoのインスタンス
    private val wordsDao: WordsDao       // WordsDaoのインスタンス
) : ViewModel() {

    /** 全てのカテゴリのリストを監視するStateFlow ("All"カテゴリを先頭に追加) */
    val categories: StateFlow<List<Category>> =
        categoryDao.getAllCategories()
            .map { dbCategories ->
                // 「すべて」表示用の仮想的なカテゴリを追加
                listOf(Category(id = -1, name = "All")) + dbCategories
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /** 各カテゴリに登録されている単語数を含んだカテゴリリストを監視するStateFlow */
    val categoriesWithCount: StateFlow<List<CategoryWithCount>> =
        categoryDao.getAllCategories()
            .flatMapLatest { dbCategories ->
                // 各カテゴリの単語数を並行して取得し、リストにまとめる
                combine(
                    dbCategories.map { category ->
                        wordsDao.getWordCount(category.id).map { count ->
                            CategoryWithCount(category, count)
                        }
                    }
                ) { it.toList() }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /** 新しいカテゴリを追加し、そのIDを返す */
    suspend fun addCategory(name: String): Int {
        return categoryDao.insertCategory(Category(name = name)).toInt()
    }

    /** カテゴリを削除*/
    fun deleteCategory(category: Category) {
        viewModelScope.launch { categoryDao.deleteCategory(category) }
    }

    /** 指定されたカテゴリIDの単語リストを取得*/
    fun getWordsByCategory(categoryId: Int): Flow<List<com.example.talkeasy.data.entity.Words>> {
        return wordsDao.getWordsByCategory(categoryId)
    }
}
