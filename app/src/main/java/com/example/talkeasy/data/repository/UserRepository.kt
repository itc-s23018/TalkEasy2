package com.example.talkeasy.data.repository

import com.example.talkeasy.data.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: User) // ユーザー登録

    suspend fun updateAiAssist(userId: Int, enabled: Boolean) // AIアシスタント使用可否更新

    suspend fun update(user: User) // ユーザー情報更新
    suspend fun getUser(): User? // ユーザー情報取得（null の可能性あり）
}