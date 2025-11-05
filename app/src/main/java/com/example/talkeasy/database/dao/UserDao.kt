package com.example.talkeasy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.talkeasy.data.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM user WHERE user_Id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM user ORDER BY user_Id ASC")
    suspend fun getAllUsers(): List<UserEntity>
}