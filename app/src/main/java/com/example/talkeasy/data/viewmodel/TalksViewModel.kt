package com.example.talkeasy.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.repository.TalksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TalksViewModel @Inject constructor(
    private val repository: TalksRepository
) : ViewModel() {

    private val _currentTalk = MutableStateFlow<Talks?>(null)
    val currentTalk: StateFlow<Talks?> = _currentTalk

    fun loadTalk(id: Int) {
        viewModelScope.launch {
            _currentTalk.value = repository.getTalk(id)
        }
    }
}