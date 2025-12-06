package com.example.talkeasy.data.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.repository.TalksRepository
import com.example.talkeasy.data.repository.UserRepository
import com.example.talkeasy.data.repository.AuthTokenRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val talksRepository: TalksRepository,
    private val auth: FirebaseAuth,
    private val authTokenRepository: AuthTokenRepository
) : ViewModel() {

    // アプリ内ユーザー情報
    var user by mutableStateOf<User?>(null)
        private set

    // Firebaseログインユーザー情報
    var firebaseUser by mutableStateOf<FirebaseUser?>(auth.currentUser)
        private set

    // ログイン状態を判定する便利プロパティ
    val isLoggedIn: Boolean
        get() = firebaseUser != null

    // 最後に取得した idToken（メモリ保持）
    private var lastIdToken: String? = null

    fun saveIdToken(token: String) {
        lastIdToken = token
        firebaseUser?.uid?.let { uid ->
            viewModelScope.launch {
                authTokenRepository.saveToken(uid, token) // DBに保存
            }
        }
    }

    suspend fun loadIdToken(): String? {
        val uid = firebaseUser?.uid ?: return null
        return authTokenRepository.getToken(uid)
    }

    // ダイアログ表示フラグ
    var showUserInputDialog by mutableStateOf(false)
        private set
    var showUserEditDialog by mutableStateOf(false)
        private set
    var showAiAssistDialog by mutableStateOf(false)
        private set

    init {
        // FirebaseAuth の状態変化を監視
        auth.addAuthStateListener { firebaseAuth ->
            firebaseUser = firebaseAuth.currentUser
        }

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

    // ユーザー登録
    fun registerUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
            this@TopViewModel.user = user
            showUserInputDialog = false
            showAiAssistDialog = true
        }
    }

    // ユーザー更新
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

    // ダイアログ制御
    fun showEditDialog() { showUserEditDialog = true }
    fun dismissEditDialog() { showUserEditDialog = false }
    fun dismissDialog() { showUserInputDialog = false }
    fun openAiAssistDialog() { showAiAssistDialog = true }
    fun dismissAiAssistDialog() { showAiAssistDialog = false }

    // Googleログイン処理
    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Log.d("TopViewModel", "Google login success")

                // FirebaseAuth から最新の ID トークンを取得
                auth.currentUser?.getIdToken(true)
                    ?.addOnSuccessListener { result ->
                        val freshToken = result.token
                        if (freshToken != null) {
                            saveIdToken(freshToken) // 最新トークンを保存（DBにも）
                            Log.d("TopViewModel", "Saved latest idToken")
                        }
                        onSuccess()
                    }
                    ?.addOnFailureListener { e ->
                        Log.w("TopViewModel", "Failed to get latest idToken", e)
                        onError(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("TopViewModel", "Google login failed", e)
                onError(e)
            }
    }


    // ログアウト処理（DBからも削除）
    fun logout(onSuccess: () -> Unit) {
        val uid = firebaseUser?.uid
        auth.signOut()
        firebaseUser = null
        user = null
        viewModelScope.launch {
            uid?.let { authTokenRepository.deleteToken(it) } // 👈 ログアウト時に削除
        }
        onSuccess()
    }

    // 新しいトーク作成
    fun createNewTalk(title: String, onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val talkId = talksRepository.createTalk()
            onCreated(talkId)
        }
    }
}
