package com.example.talkeasy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.talkeasy.data.dao.*
import com.example.talkeasy.data.entity.*

@Database(
    entities = [
        User::class,
        Talks::class,
        Messages::class,
        Words::class,
        Category::class   // ✅ Category を追加
    ],
    version = 3,   // ✅ バージョンを上げる
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun talksDao(): TalksDao
    abstract fun messagesDao(): MessagesDao
    abstract fun wordsDao(): WordsDao
    abstract fun categoryDao(): CategoryDao   // ✅ 追加

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ✅ Migration 1→2 (Messages に inputType 追加)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Messages ADD COLUMN inputType TEXT NOT NULL DEFAULT 'TEXT'"
                )
            }
        }

        // ✅ Migration 2→3 (categories テーブル追加)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
            """.trimIndent()
                )
            }
        }


        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // ✅ Migration を追加
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
