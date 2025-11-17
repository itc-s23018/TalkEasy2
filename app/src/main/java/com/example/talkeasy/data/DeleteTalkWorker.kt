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
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TalksRepository
) : Worker(context, params) {

    override fun doWork(): Result {
        // WorkManagerから渡されたtalkIdを取得
        val talkId = inputData.getInt("talkId", -1)
        if (talkId == -1) {
            return Result.failure()
        }

        return try {
            // suspend関数を呼ぶためrunBlockingを使用
            runBlocking {
                repository.deleteTalkById(talkId)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
