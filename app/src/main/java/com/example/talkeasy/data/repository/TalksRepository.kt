package com.example.talkeasy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.talkeasy.data.dao.MessagesDao
import com.example.talkeasy.data.dao.TalksDao
import com.example.talkeasy.data.entity.Messages
import com.example.talkeasy.data.entity.Talks
import jakarta.inject.Inject
import java.time.LocalDateTime

// TalksRepository: トークとメッセージに関するデータ操作を行うリポジトリ
class TalksRepository @Inject constructor(
    private val talkdao: TalksDao, // TalksDaoのインスタンス
    private val messagesDao: MessagesDao // MessagesDaoのインスタンス
) {

    // 新しいトークを作成し、そのIDを返す
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createTalk(): Int {
        val now = LocalDateTime.now()
        val talk = Talks(title = "新しいトーク", createdAt = now, updatedAt = now)
        return talkdao.insertTalk(talk).toInt()
    }

    // トークのタイトルを更新する
    suspend fun updateTalkTitle(talkId: Int, newTitle: String) {
        talkdao.updateTitle(talkId, newTitle)
    }

    // メッセージを挿入する
    suspend fun insertMessage(message: Messages) {
        messagesDao.insertMessage(message)
    }

    // 指定されたトークIDのすべてのメッセージを取得する
    suspend fun getMessagesForTalk(talkId: Int): List<Messages> {
        return messagesDao.getMessagesForTalk(talkId)
    }

    // トークを削除する
    suspend fun deleteTalk(talk: Talks) {
        talkdao.deleteTalk(talk)
    }

    // IDを指定してトークを削除する
    suspend fun deleteTalkById(talkId: Int) {
        talkdao.deleteTalkById(talkId)
    }

    // 1週間以上前のトークを削除する
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteTalksOlderThanAWeek() {
        val threshold = LocalDateTime.now().minusWeeks(1)
        talkdao.deleteOldTalks(threshold)
    }

    // IDを指定してトークを取得する
    suspend fun getTalk(id: Int): Talks? = talkdao.getTalkById(id)

    // すべてのトークを取得する
    suspend fun getAllTalks(): List<Talks> = talkdao.getAllTalks()
}
