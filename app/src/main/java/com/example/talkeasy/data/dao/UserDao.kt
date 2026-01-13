package com.example.talkeasy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.talkeasy.data.entity.User

@Dao
interface UserDao {

    //ユーザーを新規登録する。
    @Insert
    suspend fun insert(user: User)

    //AIアシスタント使用可否フラグを更新
    @Query("UPDATE user SET aiAssist = :enabled WHERE user_Id = :userId")
    suspend fun updateAiAssist(userId: Int, enabled: Boolean)

    //ユーザー情報を更新
    @Update
    suspend fun update(user: User)

    //登録されているユーザーを取得
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User
}