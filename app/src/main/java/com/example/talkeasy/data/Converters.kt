package com.example.talkeasy.data

import androidx.room.TypeConverter
import com.example.talkeasy.data.entity.InputType
import java.time.LocalDateTime

class Converters {
    // InputType(Enum)をStringに変換
    @TypeConverter
    fun fromInputType(value: InputType): String = value.name

    // StringをInputType(Enum)に変換
    @TypeConverter
    fun toInputType(value: String): InputType = InputType.valueOf(value)

    //LocalDateTimeをStringに変換
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String = dateTime.toString()

    // StringをLocalDateTimeに変換
    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime = LocalDateTime.parse(value)
}
