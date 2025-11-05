package com.example.talkeasy

import android.app.Application
import com.example.talkeasy.data.AppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TalkEasyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}