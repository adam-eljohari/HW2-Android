package com.example.hw2_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hw2_android.logic.GameManager
import com.example.hw2_android.interfaces.TiltCallback
import com.example.hw2_android.utilities.Constants
import com.example.hw2_android.utilities.SingleSoundPlayer
import com.example.hw2_android.utilities.TiltDetector
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>

    private lateinit var main_LBL_odometer:MaterialTextView

    private lateinit var main_LBL_score: MaterialTextView

    private lateinit var main_FAB_right: ExtendedFloatingActionButton

    private lateinit var main_FAB_left: ExtendedFloatingActionButton

    private lateinit var main_IMG_players: Array<AppCompatImageView>

    private lateinit var main_IMG_obstacles: Array<Array<AppCompatImageView>>

    private lateinit var main_IMG_coins: Array<Array<AppCompatImageView>>

    private lateinit var gameManager: GameManager

    private lateinit var tiltDetector: TiltDetector

    private var isUsingSensors: Boolean = false
    private var selectedSpeed: String = "Normal"
    private val speed: Long
        get() = speedToDelay(selectedSpeed)

    val handler = Handler(Looper.getMainLooper())

    private var isRunning = false

    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                gameManager.moveObstaclesDown()
                gameManager.moveCoinsDown()
                refreshUI()
            }
            handler.postDelayed(this, speed)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        isUsingSensors = intent.getBooleanExtra("IS_USING_SENSORS", false)
        selectedSpeed = intent.getStringExtra("SELECTED_SPEED") ?: "Normal"
        findViews()
        gameManager = GameManager(main_IMG_hearts.size)
        initViews()
        if (isUsingSensors) {
            initTiltDetector()
            main_FAB_left.visibility = View.INVISIBLE
            main_FAB_right.visibility = View.INVISIBLE
        }
    }


    private fun findViews() {

        main_LBL_score = findViewById(R.id.main_LBL_score)

        main_LBL_odometer = findViewById(R.id.main_LBL_odometer)

        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
            findViewById(R.id.main_IMG_heart3)
        )

        main_IMG_players = arrayOf(
            findViewById(R.id.main_IMG_player1),
            findViewById(R.id.main_IMG_player2),
            findViewById(R.id.main_IMG_player3),
            findViewById(R.id.main_IMG_player4),
            findViewById(R.id.main_IMG_player5)
        )

        main_FAB_right = findViewById(R.id.main_FAB_right)
        main_FAB_left = findViewById(R.id.main_FAB_left)

        main_IMG_obstacles = arrayOf(
            arrayOf(
                findViewById(R.id.main_IMG_obstacle1),
                findViewById(R.id.main_IMG_obstacle2),
                findViewById(R.id.main_IMG_obstacle3),
                findViewById(R.id.main_IMG_obstacle4),
                findViewById(R.id.main_IMG_obstacle5)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle6),
                findViewById(R.id.main_IMG_obstacle7),
                findViewById(R.id.main_IMG_obstacle8),
                findViewById(R.id.main_IMG_obstacle9),
                findViewById(R.id.main_IMG_obstacle10)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle11),
                findViewById(R.id.main_IMG_obstacle12),
                findViewById(R.id.main_IMG_obstacle13),
                findViewById(R.id.main_IMG_obstacle14),
                findViewById(R.id.main_IMG_obstacle15)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle16),
                findViewById(R.id.main_IMG_obstacle17),
                findViewById(R.id.main_IMG_obstacle18),
                findViewById(R.id.main_IMG_obstacle19),
                findViewById(R.id.main_IMG_obstacle20)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle21),
                findViewById(R.id.main_IMG_obstacle22),
                findViewById(R.id.main_IMG_obstacle23),
                findViewById(R.id.main_IMG_obstacle24),
                findViewById(R.id.main_IMG_obstacle25)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle26),
                findViewById(R.id.main_IMG_obstacle27),
                findViewById(R.id.main_IMG_obstacle28),
                findViewById(R.id.main_IMG_obstacle29),
                findViewById(R.id.main_IMG_obstacle30)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle31),
                findViewById(R.id.main_IMG_obstacle32),
                findViewById(R.id.main_IMG_obstacle33),
                findViewById(R.id.main_IMG_obstacle34),
                findViewById(R.id.main_IMG_obstacle35)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle36),
                findViewById(R.id.main_IMG_obstacle37),
                findViewById(R.id.main_IMG_obstacle38),
                findViewById(R.id.main_IMG_obstacle39),
                findViewById(R.id.main_IMG_obstacle40)
            )
        )

        main_IMG_coins = arrayOf(
            arrayOf(
                findViewById(R.id.main_IMG_coin1),
                findViewById(R.id.main_IMG_coin2),
                findViewById(R.id.main_IMG_coin3),
                findViewById(R.id.main_IMG_coin4),
                findViewById(R.id.main_IMG_coin5)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin6),
                findViewById(R.id.main_IMG_coin7),
                findViewById(R.id.main_IMG_coin8),
                findViewById(R.id.main_IMG_coin9),
                findViewById(R.id.main_IMG_coin10)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin11),
                findViewById(R.id.main_IMG_coin12),
                findViewById(R.id.main_IMG_coin13),
                findViewById(R.id.main_IMG_coin14),
                findViewById(R.id.main_IMG_coin15)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin16),
                findViewById(R.id.main_IMG_coin17),
                findViewById(R.id.main_IMG_coin18),
                findViewById(R.id.main_IMG_coin19),
                findViewById(R.id.main_IMG_coin20)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin21),
                findViewById(R.id.main_IMG_coin22),
                findViewById(R.id.main_IMG_coin23),
                findViewById(R.id.main_IMG_coin24),
                findViewById(R.id.main_IMG_coin25)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin26),
                findViewById(R.id.main_IMG_coin27),
                findViewById(R.id.main_IMG_coin28),
                findViewById(R.id.main_IMG_coin29),
                findViewById(R.id.main_IMG_coin30)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin31),
                findViewById(R.id.main_IMG_coin32),
                findViewById(R.id.main_IMG_coin33),
                findViewById(R.id.main_IMG_coin34),
                findViewById(R.id.main_IMG_coin35)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin36),
                findViewById(R.id.main_IMG_coin37),
                findViewById(R.id.main_IMG_coin38),
                findViewById(R.id.main_IMG_coin39),
                findViewById(R.id.main_IMG_coin40)
            )
        )
    }

    private fun initViews() {
        if (!isUsingSensors) {
            main_FAB_right.setOnClickListener {
                movePlayerRight()
            }
            main_FAB_left.setOnClickListener {
                movePlayerLeft()
            }
        }
        main_LBL_score.text = gameManager.score.toString()
        main_LBL_odometer.text = gameManager.odometer.toString()

        refreshUI()
        isRunning = true
        handler.postDelayed(runnable, Constants.GameLogic.DELAY_MILLIS)
    }

    private fun speedToDelay(speed: String): Long {
        return when (speed) {
            "Slow" -> Constants.GameLogic.DELAY_MILLIS * 2
            "Normal" -> Constants.GameLogic.DELAY_MILLIS
            "Fast" -> Constants.GameLogic.DELAY_MILLIS / 2
            else -> Constants.GameLogic.DELAY_MILLIS
        }
    }

    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback {
                override fun tiltX() {
                    if (tiltDetector.tiltCounterX > 0) {
                        movePlayerRight()
                    } else {
                        movePlayerLeft()
                    }
                }
            }
        )
        tiltDetector.start()
    }

    override fun onResume() {
        super.onResume()
        if (isUsingSensors) {
            tiltDetector.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isUsingSensors)
            tiltDetector.stop()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
        if (isUsingSensors) {
            tiltDetector.stop()
        }
    }


    private fun movePlayerLeft() {
        if (gameManager.canMovePlayerLeft()) {
            gameManager.movePlayerLeft()
            refreshUI()
        }
    }

    private fun movePlayerRight() {
        if (gameManager.canMovePlayerRight()) {
            gameManager.movePlayerRight()
            refreshUI()
        }
    }

    fun showMessage(txt: String) {
        Toast.makeText(applicationContext,txt, Toast.LENGTH_SHORT).show()
        vibrateOnce(this)
    }


    @SuppressLint("ObsoleteSdkInt")
    @Suppress("DEPRECATION")
    private fun vibrateOnce(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator?.hasVibrator() == true) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(Constants.GameLogic.DURATION, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(Constants.GameLogic.DURATION)
            }
        }
    }

    private fun updatePlayers() {
        for (i in main_IMG_players.indices) {
            if (i == gameManager.playerPosition) {
                main_IMG_players[i].visibility = View.VISIBLE
            } else {
                main_IMG_players[i].visibility = View.INVISIBLE
            }
        }
    }

    private fun updateObstacles() {
        for (row in main_IMG_obstacles.indices) {
            for (col in main_IMG_obstacles[row].indices) {
                if (gameManager.obstacleMatrix[row][col]) {
                    main_IMG_obstacles[row][col].visibility = View.VISIBLE
                } else {
                    main_IMG_obstacles[row][col].visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun updateCoins() {

        for (row in main_IMG_coins.indices) {
            for (col in main_IMG_coins[row].indices) {
                if (gameManager.coinMatrix[row][col]) {
                    main_IMG_coins[row][col].visibility = View.VISIBLE
                } else {
                    main_IMG_coins[row][col].visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun updateHearts() {
        if (gameManager.hitPosition != 0 && gameManager.hitPosition <= main_IMG_hearts.size) {
            main_IMG_hearts[main_IMG_hearts.size - gameManager.hitPosition].visibility =
                View.INVISIBLE
        }
    }

    private fun refreshUI() {
        if (gameManager.isGameOver) {
            showMessage("Game Over! 😭 ")
            isRunning = false
            changeActivity("Game Over! 😭 ", gameManager.score)
        } else {
            updatePlayers()
            updateObstacles()
            updateCoins()
            if (gameManager.checkCollisionObstacle()) {
                showMessage("You hit the moon , it's not good!")
                SingleSoundPlayer(this).playSound(R.raw.boom_sound)
            }
            if (gameManager.checkCollisionCoin()) {
                showMessage("Nice")
                SingleSoundPlayer(this).playSound(R.raw.coin_collect)
            }else{
                gameManager.updateScore()
                gameManager.updateOdometer()
            }
            main_LBL_odometer.text = gameManager.odometer.toString()
            main_LBL_score.text = gameManager.score.toString()
            updateHearts()
        }
    }

    private fun changeActivity(message: String, score: Int) {
        handler.removeCallbacks(runnable)
        val intent = Intent(this, HighScoreActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(Constants.BundleKeys.SCORE_KEY, score)
        bundle.putString(Constants.BundleKeys.STATUS_KEY, message)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }






}