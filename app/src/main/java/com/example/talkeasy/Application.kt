package com.example.talkeasy

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.talkeasy.data.AppContainer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * TalkEasyアプリケーションのカスタムApplicationクラス。
 * Hiltの初期化とWorkManagerのカスタム設定を行う、アプリ全体のエントリーポイント。
 */
@HiltAndroidApp
class TalkEasyApplication : Application(), Configuration.Provider {

    // HiltがWorkManagerのWorkerに依存性を注入するために使用するファクトリ
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // 手動DIコンテナ（Hiltと併用）
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // アプリケーションコンテナを初期化
        container = AppContainer(this)
    }

    /**
     * WorkManagerのカスタム設定を提供。
     * HiltWorkerFactoryを設定することで、Worker内での依存性注入を可能にする。
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
