package com.example.talkeasy.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.dao.CategoryDao
import com.example.talkeasy.data.entity.Category
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(private val dao: CategoryDao) : ViewModel() {

    val categories: StateFlow<List<Category>> =
        dao.getAllCategories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun addCategory(name: String) {
        viewModelScope.launch {
            dao.insertCategory(Category(name = name))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { dao.deleteCategory(category) }
    }
}
