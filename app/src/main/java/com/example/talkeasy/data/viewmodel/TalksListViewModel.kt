package com.example.talkeasy.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.repository.TalksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// HiltによってViewModelを注入するためのアノテーション
@HiltViewModel
// TalksListViewModel: TalksRepositoryを介してトーク履歴を取得し、UIに提供する
class TalksListViewModel @Inject constructor(
    private val repository: TalksRepository // TalksRepositoryのインスタンス
) : ViewModel() {

    // トーク履歴のリストを保持するMutableStateFlow
    private val _talks = MutableStateFlow<List<Talks>>(emptyList())
    // 外部に公開する読み取り専用のStateFlow
    val talks: StateFlow<List<Talks>> = _talks

    // 初期化時にすべてのトーク履歴をデータベースから取得する
    init {
        viewModelScope.launch {
            _talks.value = repository.getAllTalks()
        }
    }
}