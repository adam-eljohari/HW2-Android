package com.example.hw2_android.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.hw2_android.interfaces.TiltCallback

class TiltDetector (context: Context, private val tiltCallback: TiltCallback?) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
    private lateinit var sensorEventListener: SensorEventListener

    var tiltCounterX: Int = 0
        private set

    private var timestamp: Long = 0L

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                calculateTilt(x)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {


            }
        }
    }


    private fun calculateTilt(c: Float) {
        if (System.currentTimeMillis() - timestamp >= Constants.GameLogic.DELAY_MILLIS) {
            timestamp = System.currentTimeMillis()
            if (c >= 3){
                tiltCounterX--
                tiltCallback?.tiltX()
            }
            if (c <= -3) {
                tiltCounterX++
                tiltCallback?.tiltX()
            }
        }
    }

    fun start() {
        sensorManager
            .registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
    }

    fun stop() {
        sensorManager.unregisterListener(sensorEventListener, sensor)
    }
}