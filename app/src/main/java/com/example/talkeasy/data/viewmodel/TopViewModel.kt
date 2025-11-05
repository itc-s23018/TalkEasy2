package com.example.talkeasy.data.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.repository.TalksRepository
import com.example.talkeasy.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val talksRepository: TalksRepository
) : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set

    var showUserInputDialog by mutableStateOf(false)
        private set

    var showUserEditDialog by mutableStateOf(false)
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
            showUserInputDialog = false
        }
    }

    fun updateUser(lastName: String, firstName: String, lastNameRuby: String, firstNameRuby: String) {
        val current = user ?: return
        val updated = current.copy(
            lastName = lastName,
            firstName = firstName,
            lastNameRuby = lastNameRuby,
            firstNameRuby = firstNameRuby
        )
        viewModelScope.launch {
            userRepository.update(updated)
            user = updated
            showUserEditDialog = false
        }
    }

    fun showEditDialog() {
        showUserEditDialog = true
    }

    fun dismissEditDialog() {
        showUserEditDialog = false
    }

    fun dismissDialog() {
        showUserInputDialog = false
    }

    fun createNewTalk(title: String, onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val talkId = talksRepository.createTalk()
            onCreated(talkId)
        }
    }

}