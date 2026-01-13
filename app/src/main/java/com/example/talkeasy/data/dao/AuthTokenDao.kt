package com.example.talkeasy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.talkeasy.data.entity.AuthToken

@Dao
interface AuthTokenDao {

    //トークンを保存する。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: AuthToken)

    //保存されているトークンを取得
    @Query("SELECT * FROM auth_tokens LIMIT 1")
    suspend fun getToken(): AuthToken?

    //ログアウト時・アカウント削除時に、トークンを全削除する。
    @Query("DELETE FROM auth_tokens")
    suspend fun clearAll()
}
