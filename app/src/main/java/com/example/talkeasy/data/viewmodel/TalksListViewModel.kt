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

@HiltViewModel
class TalksListViewModel @Inject constructor(
    private val repository: TalksRepository
) : ViewModel() {

    private val _talks = MutableStateFlow<List<Talks>>(emptyList())
    val talks: StateFlow<List<Talks>> = _talks

    init {
        viewModelScope.launch {
            _talks.value = repository.getAllTalks()
        }
    }
}