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

    @Query("SELECT COUNT(*) FROM user")
    suspend fun getUserCount(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM user LIMIT 1)")
    suspend fun hasUser(): Boolean

    // 既存のメソッド
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM user WHERE user_Id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM user ORDER BY user_Id ASC")
    suspend fun getAllUsers(): List<User>
}