package com.example.talkeasy.data.repository

import com.example.talkeasy.data.dao.AuthTokenDao
import com.example.talkeasy.data.entity.AuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenRepository @Inject constructor(
    private val authTokenDao: AuthTokenDao
) {
    //Google ログイン成功時に ID トークンを保存
    suspend fun saveToken(uid: String, idToken: String) {
        withContext(Dispatchers.IO) {
            val token = AuthToken(uid = uid, idToken = idToken, createdAt = System.currentTimeMillis().toString())
            authTokenDao.insert(token)
        }
    }

    //Gemini API 呼び出し時などに ID トークンを取得
    suspend fun getToken(uid: String): String? {
        return withContext(Dispatchers.IO) {
            authTokenDao.getToken()?.idToken
        }
    }

    //ログアウト時にトークンを削除
    suspend fun deleteToken(uid: String) {
        withContext(Dispatchers.IO) {
            authTokenDao.clearAll() // 1ユーザー前提なので全部削除でOK
        }
    }

    //アカウント削除時など、完全にトークンを消したい場合に使用。
    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            authTokenDao.clearAll()
        }
    }
}
