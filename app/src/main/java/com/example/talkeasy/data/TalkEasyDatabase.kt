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
        Category::class,
        AuthToken::class   // 👈 追加
    ],
    version = 6, // 👈 バージョンを更新
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun talksDao(): TalksDao
    abstract fun messagesDao(): MessagesDao
    abstract fun wordsDao(): WordsDao
    abstract fun categoryDao(): CategoryDao
    abstract fun authTokenDao(): AuthTokenDao   // 👈 追加

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration 1→2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Messages ADD COLUMN inputType TEXT NOT NULL DEFAULT 'TEXT'"
                )
            }
        }

        // Migration 2→3
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

        // Migration 3→4 (words テーブルを categoryId に変更)
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS words_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        word TEXT NOT NULL,
                        wordRuby TEXT NOT NULL,
                        updatedAt TEXT NOT NULL,
                        categoryId INTEGER NOT NULL,
                        FOREIGN KEY(categoryId) REFERENCES categories(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    INSERT INTO words_new (id, word, wordRuby, updatedAt, categoryId)
                    SELECT w.id, w.word, w.wordRuby, w.updatedAt,
                           COALESCE(c.id, -1)
                    FROM words w
                    LEFT JOIN categories c ON w.category = c.name
                    """.trimIndent()
                )

                database.execSQL("DROP TABLE words")
                database.execSQL("ALTER TABLE words_new RENAME TO words")
            }
        }

        // Migration 4→5 (User テーブルに aiAssistEnabled を追加)
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE user ADD COLUMN aiAssistEnabled INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        // Migration 5→6 (auth_tokens テーブル追加)
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS auth_tokens (
                        uid TEXT PRIMARY KEY NOT NULL,
                        idToken TEXT NOT NULL,
                        createdAt TEXT NOT NULL
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
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
