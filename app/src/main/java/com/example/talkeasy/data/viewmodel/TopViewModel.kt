package com.example.talkeasy.data.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel()  {

    var user by mutableStateOf<User?>(null)
        private set

    var showUserInputDialog by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            try {
                val result = userRepository.getUser()
                if (result != null) {
                    user = result
                } else {
                    showUserInputDialog = true
                }
            } catch (e: Exception) {
                showUserInputDialog = true
            }
        }
    }

    fun registerUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
            this@TopViewModel.user = user
            showUserInputDialog= false
        }
    }

    fun dismissDialog() {
        showUserInputDialog = false
    }
}