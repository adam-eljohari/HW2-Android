package com.example.hw2_android


import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hw2_android.interfaces.CallbackHighScoreClicked
import com.example.hw2_android.ui.HighScoresFragment
import com.example.hw2_android.ui.MapFragment
import com.google.android.material.button.MaterialButton


class HighScoreActivity : AppCompatActivity() {


    private lateinit var main_FRAME_highScores: FrameLayout

    private lateinit var main_FRAME_map: FrameLayout

    private lateinit var mapFragment: MapFragment
    private lateinit var highScoresFragment: HighScoresFragment

    private lateinit var highScores_BTN_back: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_high_score)

        findViews()
        initViews()
    }

    private fun findViews() {
        main_FRAME_map = findViewById(R.id.main_FRAME_map)
        main_FRAME_highScores = findViewById(R.id.main_FRAME_highScores)
        highScores_BTN_back = findViewById(R.id.highScores_BTN_back)
    }

    private fun initViews() {

        highScores_BTN_back.setOnClickListener {
            backToMenu()
        }

        mapFragment = MapFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_FRAME_map, mapFragment)
            .commit()

        highScoresFragment = HighScoresFragment()
        highScoresFragment.highScoreItemClicked =
            object : CallbackHighScoreClicked {
                override fun highScoreItemClicked(lat: Double, lon: Double) {
                    mapFragment.zoom(lat, lon)
                }
            }
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_FRAME_highScores, highScoresFragment)
            .commit()
    }

    private fun backToMenu() {
        finish()
    }
}


