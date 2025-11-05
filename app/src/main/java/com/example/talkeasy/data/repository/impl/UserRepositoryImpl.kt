package com.example.talkeasy.data.repository.impl

import com.example.talkeasy.data.dao.UserDao
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao
): UserRepository  {

    override suspend fun insertUser(user: User) = dao.insert(user)

    override suspend fun update(user: User) = dao.update(user)

    override suspend fun getUser(): User = dao.getUser()
}