package com.example.talkeasy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.talkeasy.data.entity.AuthToken

@Dao
interface AuthTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: AuthToken)

    @Query("SELECT * FROM auth_tokens LIMIT 1")
    suspend fun getToken(): AuthToken?

    @Query("DELETE FROM auth_tokens")
    suspend fun clearAll()
}
