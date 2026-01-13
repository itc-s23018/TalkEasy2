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
        AuthToken::class
    ],
    version = 6, // スキーマのバージョン。変更時にマイグレーションが必要。
    exportSchema = false // スキーマ情報をファイルに書き出さない
)
@TypeConverters(Converters::class) // カスタム型（LocalDateTimeなど）の変換クラスを指定
abstract class AppDatabase : RoomDatabase() {

    // --- 各テーブルに対応するDAOの抽象メソッド --- //
    abstract fun userDao(): UserDao
    abstract fun talksDao(): TalksDao
    abstract fun messagesDao(): MessagesDao
    abstract fun wordsDao(): WordsDao
    abstract fun categoryDao(): CategoryDao
    abstract fun authTokenDao(): AuthTokenDao

    companion object {
        @Volatile // INSTANCEへの変更が即座に他のスレッドから見えるようにする
        private var INSTANCE: AppDatabase? = null

        // --- データベースマイグレーションの定義 --- //

        // Migration 1→2: MessagesテーブルにinputTypeカラムを追加
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Messages ADD COLUMN inputType TEXT NOT NULL DEFAULT 'TEXT'"
                )
            }
        }

        // Migration 2→3: categoriesテーブルを新規作成
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

        // Migration 3→4: wordsテーブルの`category`を`categoryId`に変更（外部キー制約付き）
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. 新しいスキーマで一時テーブルを作成
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

                // 2. 古いテーブルから新しいテーブルにデータを移行
                database.execSQL(
                    """
                    INSERT INTO words_new (id, word, wordRuby, updatedAt, categoryId)
                    SELECT w.id, w.word, w.wordRuby, w.updatedAt,
                           COALESCE(c.id, -1) -- カテゴリ名からIDを検索して設定
                    FROM words w
                    LEFT JOIN categories c ON w.category = c.name
                    """.trimIndent()
                )

                // 3. 古いテーブルを削除
                database.execSQL("DROP TABLE words")
                // 4. 一時テーブルを正式なテーブル名に変更
                database.execSQL("ALTER TABLE words_new RENAME TO words")
            }
        }

        // Migration 4→5: UserテーブルにaiAssistEnabledカラムを追加
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE user ADD COLUMN aiAssistEnabled INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        // Migration 5→6: auth_tokensテーブルを新規作成
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
            // INSTANCEがnullでなければそれを返す。nullならsynchronizedブロックで初期化する。
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // データベースファイル名
                )
                    // 定義したマイグレーションをビルダーに追加
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .build()
                    .also { INSTANCE = it } // 作成したインスタンスをINSTANCEに設定
            }
        }
    }
}
