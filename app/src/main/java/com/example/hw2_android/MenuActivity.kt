package com.example.hw2_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MenuActivity : AppCompatActivity() {

    private lateinit var menu_BTN_start: MaterialButton
    private lateinit var menu_BTN_settings: MaterialButton
    private lateinit var menu_BTN_exit: MaterialButton
    private lateinit var menu_BTN_records: MaterialButton


    private var isUsingSensors: Boolean = false
    private var selectedSpeed: String = "Normal"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViews()
        initViews()
    }


    private fun findViews() {
        menu_BTN_start = findViewById(R.id.menu_BTN_start)
        menu_BTN_settings = findViewById(R.id.menu_BTN_settings)
        menu_BTN_records = findViewById(R.id.menu_BTN_records)
        menu_BTN_exit = findViewById(R.id.menu_BTN_exit)
    }

    private fun initViews() {
        menu_BTN_start.setOnClickListener {
            startGame()
        }

        menu_BTN_settings.setOnClickListener {
            openSettings()
        }

        menu_BTN_records.setOnClickListener {
            val intent = Intent(this, HighScoreActivity::class.java)
            startActivity(intent)

        }

        menu_BTN_exit.setOnClickListener {
            exitGame()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val isUsingSensors = data.getBooleanExtra("IS_USING_SENSORS", false)
            val selectedSpeed = data.getStringExtra("SELECTED_SPEED") ?: "Normal"
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("IS_USING_SENSORS", isUsingSensors)
            intent.putExtra("SELECTED_SPEED", selectedSpeed)
            startActivity(intent)
        }
    }

    private fun startGame() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("IS_USING_SENSORS", isUsingSensors)
            putExtra("SELECTED_SPEED", selectedSpeed)
        }
        startActivity(intent)
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        @Suppress("DEPRECATION")
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

    private fun exitGame() {
        finish()
    }

    companion object {
        private const val SETTINGS_REQUEST_CODE = 1
    }
}
