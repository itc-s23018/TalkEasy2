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
    // 保存（ログイン時）
    suspend fun saveToken(uid: String, idToken: String) {
        withContext(Dispatchers.IO) {
            val token = AuthToken(uid = uid, idToken = idToken, createdAt = System.currentTimeMillis().toString())
            authTokenDao.insert(token)
        }
    }

    // 取得（サーバー呼び出し時など）
    suspend fun getToken(uid: String): String? {
        return withContext(Dispatchers.IO) {
            authTokenDao.getToken()?.idToken
        }
    }

    // 削除（ログアウト時）
    suspend fun deleteToken(uid: String) {
        withContext(Dispatchers.IO) {
            authTokenDao.clearAll() // 1ユーザー前提なので全部削除でOK
        }
    }

    // 全削除（アカウント削除時など）
    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            authTokenDao.clearAll()
        }
    }
}
