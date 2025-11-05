package com.example.talkeasy.data

import android.content.Context
import com.example.talkeasy.data.repository.UserRepository
import com.example.talkeasy.data.repository.impl.UserRepositoryImpl

class AppContainer(context: Context) {

    // Room データベースのインスタンス
    private val database: AppDatabase = AppDatabase.getInstance(context)

    // Repository のインスタンス
    val userRepository: UserRepository = UserRepositoryImpl(database.userDao())

}