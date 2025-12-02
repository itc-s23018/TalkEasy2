package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "user",
    indices = [Index(value = ["lastName", "firstName"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val user_Id: Int = 1,
    val lastName: String,
    val lastNameRuby: String,
    val firstName: String,
    val firstNameRuby: String,
    val aiAssist: Boolean = false
)