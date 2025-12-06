package com.example.talkeasy.data

import android.content.Context
import com.example.talkeasy.data.dao.AuthTokenDao
import com.example.talkeasy.data.dao.CategoryDao
import com.example.talkeasy.data.dao.MessagesDao
import com.example.talkeasy.data.dao.TalksDao
import com.example.talkeasy.data.dao.UserDao
import com.example.talkeasy.data.dao.WordsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideTalksDao(db: AppDatabase): TalksDao = db.talksDao()
    @Provides fun provideMessagesDao(db: AppDatabase): MessagesDao = db.messagesDao()
    @Provides fun provideWordsDao(db: AppDatabase): WordsDao = db.wordsDao()
    @Provides fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides fun provideAuthTokenDao(db: AppDatabase): AuthTokenDao = db.authTokenDao() // 👈 追加
}
