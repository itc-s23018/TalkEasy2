package com.example.talkeasy.data.repository

import com.example.talkeasy.data.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user:User)
    suspend fun update(user: User)
    suspend fun getUser(): User
}