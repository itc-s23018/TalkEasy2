package com.example.talkeasy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.talkeasy.data.dao.MessagesDao
import com.example.talkeasy.data.dao.TalksDao
import com.example.talkeasy.data.dao.UserDao
import com.example.talkeasy.data.dao.WordsDao
import com.example.talkeasy.data.entity.Messages
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.entity.Words

@Database(
    entities = [User::class, Talks::class, Messages::class, Words::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun talksDao(): TalksDao
    abstract fun messagesDao(): MessagesDao
    abstract fun wordsDao(): WordsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ✅ マイグレーション定義
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Messages ADD COLUMN inputType TEXT NOT NULL DEFAULT 'TEXT'")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2) // ✅ 本番対応
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
