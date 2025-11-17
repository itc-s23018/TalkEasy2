package com.example.talkeasy

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.talkeasy.data.AppContainer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TalkEasyApplication : Application(), Configuration.Provider {

    // HiltWorkerFactory を注入
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    // ✅ 抽象プロパティを override する
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
