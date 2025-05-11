package com.example.hw2_android

import android.app.Application
import com.example.hw2_android.logic.ScoreManager
import com.example.hw2_android.utilities.BackgroundMusicPlayer

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        BackgroundMusicPlayer.init(this)
        BackgroundMusicPlayer.getInstance().setResourceId(R.raw.background_music)
        ScoreManager.getInstance(this)
        ScoreManager.getInstance(this).loadScores()
    }

    override fun onTerminate() {
        super.onTerminate()
        BackgroundMusicPlayer.getInstance().stopMusic()

    }
}