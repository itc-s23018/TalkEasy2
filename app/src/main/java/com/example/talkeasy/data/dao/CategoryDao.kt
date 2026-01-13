package com.example.talkeasy.data.dao

import androidx.room.*
import com.example.talkeasy.data.entity.Category
import kotlinx.coroutines.flow.Flow

// "categories"テーブルにアクセスするためのDAO(Data Access Object)
@Dao
interface CategoryDao {
    // 新しいカテゴリを挿入
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    // カテゴリを更新
    @Update
    suspend fun updateCategory(category: Category)

    // カテゴリを削除
    @Delete
    suspend fun deleteCategory(category: Category)

    // すべてのカテゴリを名前の昇順で取得
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>
}
