package com.example.talkeasy.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.talkeasy.data.repository.TalksRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking

@HiltWorker
class DeleteTalkWorker @AssistedInject constructor(
    @Assisted context: Context, // WorkManagerから提供されるコンテキスト
    @Assisted params: WorkerParameters, // WorkManagerから提供されるパラメータ
    private val repository: TalksRepository // Hiltから注入されるリポジトリ
) : Worker(context, params) {

    /**
     * ワーカーのメインの処理を定義するメソッド。
     * @return 処理の結果（成功、失敗、リトライ）
     */
    override fun doWork(): Result {
        // WorkManagerから渡された入力データ("talkId")を取得
        val talkId = inputData.getInt("talkId", -1)
        if (talkId == -1) {
            // talkIdが不正な場合は、処理を失敗させる
            return Result.failure()
        }

        return try {
            // WorkerのdoWorkは同期的だが、リポジトリのメソッドはsuspend関数のため、
            // runBlockingを使用してコルーチンを同期的（ブロッキング）に実行する。
            runBlocking {
                repository.deleteTalkById(talkId)
            }
            // 処理が成功したことをWorkManagerに通知
            Result.success()
        } catch (e: Exception) {
            // 例外が発生した場合は、処理をリトライするようにWorkManagerに通知
            Result.retry()
        }
    }
}
