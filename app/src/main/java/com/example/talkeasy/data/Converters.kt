package com.example.talkeasy.data

import androidx.room.TypeConverter
import com.example.talkeasy.data.entity.InputType
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromInputType(value: InputType): String = value.name

    @TypeConverter
    fun toInputType(value: String): InputType = InputType.valueOf(value)

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String = dateTime.toString()

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime = LocalDateTime.parse(value)
}
