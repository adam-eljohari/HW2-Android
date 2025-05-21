package com.example.hw2_android

import android.app.Application
import com.example.hw2_android.logic.ScoreManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ScoreManager.getInstance(this)
        ScoreManager.getInstance(this).loadScores()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}