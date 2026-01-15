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

    /** アプリ内ユーザー情報 */
    var user by mutableStateOf<User?>(null)
        private set

    /** Firebase Authenticationのユーザー情報 */
    var firebaseUser by mutableStateOf<FirebaseUser?>(auth.currentUser)
        private set

    /** ログイン状態を簡単にチェックするためのプロパティ */
    val isLoggedIn: Boolean
        get() = firebaseUser != null

    // 最後に取得したidToken（メモリキャッシュ）
    private var lastIdToken: String? = null

    // IDトークンをメモリとデータベースに保存
    fun saveIdToken(token: String) {
        lastIdToken = token
        firebaseUser?.uid?.let { uid ->
            viewModelScope.launch {
                authTokenRepository.saveToken(uid, token) // DBにも保存
            }
        }
    }

    //データベースからIDトークンを読み込む
    suspend fun loadIdToken(): String? {
        val uid = firebaseUser?.uid ?: return null
        return authTokenRepository.getToken(uid)
    }

    // --- ダイアログ表示状態 --- //
    var showUserInputDialog by mutableStateOf(false)
        private set
    var showUserEditDialog by mutableStateOf(false)
        private set
    var showAiAssistDialog by mutableStateOf(false)
        private set

    init {
        // FirebaseAuthの認証状態の変更を監視
        auth.addAuthStateListener { firebaseAuth ->
            firebaseUser = firebaseAuth.currentUser
        }

        // ユーザー情報をDBからロード。なければ入力ダイアログを表示
        viewModelScope.launch {
            try {
                val result = userRepository.getUser()
                if (result != null) {
                    user = result
                } else {
                    showUserInputDialog = true // ユーザー情報がない場合
                }
            } catch (e: Exception) {
                showUserInputDialog = true // エラー時
            }
        }
    }

    //ユーザー情報をデータベースに登録
    fun registerUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.insertUser(user)
                this@TopViewModel.user = user
                showUserInputDialog = false
                showAiAssistDialog = true // 登録後にAI支援ダイアログを表示
            } catch (e: Exception) {
                Log.e("TopViewModel", "Error inserting user", e)
            }
        }
    }

    //ユーザー情報を更新
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

    // --- ダイアログ制御 --- //
    fun showEditDialog() { showUserEditDialog = true }
    fun dismissEditDialog() { showUserEditDialog = false }
    fun dismissDialog() { showUserInputDialog = false }
    fun openAiAssistDialog() { showAiAssistDialog = true }
    fun dismissAiAssistDialog() { showAiAssistDialog = false }

    // GoogleのIDトークンを使用してFirebaseにログイン/
    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Log.d("TopViewModel", "Google login success")

                // ログイン成功後、Firebaseから最新のIDトークンを強制的に再取得
                auth.currentUser?.getIdToken(true)
                    ?.addOnSuccessListener { result ->
                        val freshToken = result.token
                        if (freshToken != null) {
                            saveIdToken(freshToken) // 最新トークンを保存
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


    // ログアウト処理。Firebaseからのサインアウトと、DBに保存したIDトークンの削除/
    fun logout(onSuccess: () -> Unit) {
        val uid = firebaseUser?.uid
        auth.signOut()
        firebaseUser = null
        user = null // アプリ内ユーザー情報もクリア
        viewModelScope.launch {
            uid?.let { authTokenRepository.deleteToken(it) } // DBからトークンを削除
        }
        onSuccess()
    }

    // 新しいトークを作成する
    fun createNewTalk(title: String, onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val talkId = talksRepository.createTalk()
            onCreated(talkId)
        }
    }
}
