package com.example.talkeasy.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

// 入力方法を表すEnum（テキストまたは音声）
enum class InputType {
    TEXT, VOICE
}

// "messages"テーブルを表すエンティティ
@Entity(
    tableName = "messages",
    // 外部キー制約: talkIdはtalksテーブルのidを参照する
    foreignKeys = [
        ForeignKey(
            entity = Talks::class,
            parentColumns = ["id"],
            childColumns = ["talkId"],
            // 親となるTalksが削除された場合、関連するMessagesも削除する
            onDelete = ForeignKey.CASCADE
        )
    ],
    // talkIdにインデックスを張り、クエリを高速化する
    indices = [Index(value = ["talkId"])]
)
data class Messages(
    // 主キー。自動生成される
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // 関連するトークのID
    val talkId: Int,
    // メッセージのテキスト内容
    val text: String,
    // 作成日時
    val createdAt: LocalDateTime,
    // 入力方法（テキストか音声か）
    val inputType: InputType
)
