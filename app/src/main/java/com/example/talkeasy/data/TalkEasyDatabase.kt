package com.example.talkeasy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.talkeasy.data.dao.MessagesDao
import com.example.talkeasy.data.dao.TalksDao
import com.example.talkeasy.data.dao.UserDao
import com.example.talkeasy.data.dao.WordsDao
import com.example.talkeasy.data.entity.Messages
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.entity.Words

@Database(
    entities = [
        User::class,
        Talks::class,
        Messages::class,
        Words::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun talksDao(): TalksDao
    abstract fun messagesDao(): MessagesDao
    abstract fun wordsDao(): WordsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}