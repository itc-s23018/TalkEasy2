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

    @Insert
    suspend fun insert(user: User)

    @Query("UPDATE user SET aiAssist = :enabled WHERE user_Id = :userId")
    suspend fun updateAiAssist(userId: Int, enabled: Boolean)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User
}