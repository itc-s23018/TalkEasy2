package com.example.talkeasy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.talkeasy.data.entity.MessagesEntity
import com.example.talkeasy.data.entity.TalksEntity
import com.example.talkeasy.data.entity.UserEntity
import com.example.talkeasy.data.entity.WordsEntity
import kotlinx.coroutines.InternalCoroutinesApi

@Database(entities = [UserEntity::class, TalksEntity::class, MessagesEntity::class, WordsEntity::class],
    version = 1,
    exportSchema = false)
annotation class TalkEasyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun talksDao(): TalksDao
    abstract fun messagesDao(): MessagesDao
    abstract fun wordsDao(): WordsDao

    companion object{
        @Volatile
        private var Instance: TalkEasyDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabese(context: Context): TalkEasyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, TalkEasyDatabase::class.java,
                    "talk_easy_database"
                ).build().also { Instance = it }
            }
        })
    }
}