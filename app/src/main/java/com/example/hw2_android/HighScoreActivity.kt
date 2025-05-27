package com.example.hw2_android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.example.hw2_android.interfaces.CallbackHighScoreClicked
import com.example.hw2_android.item_model.Score
import com.example.hw2_android.logic.ScoreManager
import com.example.hw2_android.ui.HighScoresFragment
import com.example.hw2_android.utilities.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import pub.devrel.easypermissions.EasyPermissions



class HighScoreActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var main_FRAME_highScores: FrameLayout
    private lateinit var main_FRAME_map: FrameLayout
    private lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null
    private lateinit var highScoresFragment: HighScoresFragment
    private var currentScore: Int = 0
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var locationPermissionGranted = false

    private lateinit var highScores_BTN_back: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)
        findViews()
        initViews()
    }

    private fun findViews() {
        main_FRAME_highScores = findViewById(R.id.main_FRAME_highScores)
        main_FRAME_map = findViewById(R.id.main_FRAME_map)
        highScores_BTN_back = findViewById(R.id.highScores_BTN_back)
    }

    private fun initViews() {
        highScoresFragment = HighScoresFragment()
        mapFragment = SupportMapFragment.newInstance()
        setupHighScoreClickListener()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_FRAME_highScores, highScoresFragment)
            .replace(R.id.main_FRAME_map, mapFragment)
            .commit()
        mapFragment.getMapAsync { map ->
            googleMap = map
            googleMap?.uiSettings?.isZoomControlsEnabled = true
            updateMapWithScores()
        }
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        initLocationListener()

        highScores_BTN_back.setOnClickListener {
            backToMenu()
        }
    }

    private fun setupHighScoreClickListener() {
        highScoresFragment.highScoreItemClicked = object : CallbackHighScoreClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lat, lon),
                        15f
                    )
                )
                showMessage("Zooming to: Lat=$lat, Lon=$lon")
            }
        }
    }

    private fun initLocationListener() {
        locationListener = LocationListener { location ->
            saveScoreIfEligible(location.latitude, location.longitude)
        }
    }

    override fun onResume() {
        super.onResume()
        loadScoreFromIntent()
        checkLocationPermission()
        if (locationPermissionGranted) {
            debugScores()
            startLocationListener()
            fetchAndAddScore()
            updateMapWithScores()
            highScoresFragment.updateHighScore()
            if (ScoreManager.getInstance(this).scores.isEmpty() && currentScore == 0) {
                highScoresFragment.highScore_LBL_title.visibility = View.VISIBLE
            } else {
                highScoresFragment.highScore_LBL_title.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationListener()
    }

    private fun loadScoreFromIntent() {
        val bundle = intent.extras ?: return
        currentScore = bundle.getInt(Constants.BundleKeys.SCORE_KEY, 0)
    }

    private fun checkLocationPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationPermissionGranted = true
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Location permissions are needed to save high scores.",
                101,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun startLocationListener() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                30000L,
                10f,
                locationListener
            )
        } catch (e: SecurityException) {
            showMessage("Location permissions are required to save high scores.")
        }
    }

    private fun stopLocationListener() {
        locationManager.removeUpdates(locationListener)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchAndAddScore() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                saveScoreIfEligible(latitude, longitude)
            } else {
                showMessage("Unable to fetch location")
            }
        }.addOnFailureListener {
            showMessage("Failed to fetch location")
        }
    }

    private fun saveScoreIfEligible(latitude: Double, longitude: Double) {
        if (currentScore == 0) return
        val scoreManager = ScoreManager.getInstance(this)
        val scores = scoreManager.scores
        if (scores.size < 10 || currentScore > scores.last().scoreValue) {
            val newScore = Score(currentScore, latitude, longitude)
            scoreManager.updateScore(newScore)
            highScoresFragment.updateHighScore()
            googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(latitude, longitude))
                    .title("Score: $currentScore")
            )
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude, longitude),
                    15f
                )
            )
        }
        currentScore = 0
    }

    private fun updateMapWithScores() {
        val scores = ScoreManager.getInstance(this).scores
        if (scores.isNotEmpty()) {
            googleMap?.clear()
            val boundsBuilder = LatLngBounds.Builder()
            scores.forEach { score ->
                val location = LatLng(score.latitude, score.longitude)
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("Score: ${score.scoreValue}")
                        .snippet("Lat: ${score.latitude}, Lon: ${score.longitude}")
                )
                boundsBuilder.include(location)
            }
            val bounds = boundsBuilder.build()
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            googleMap?.setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
                showMessage("Clicked on: ${marker.title}")
                true
            }
        } else {
            showMessage("No scores to display on the map.")
        }
    }

    private fun debugScores() {
        val scores = ScoreManager.getInstance(this).scores
        Log.d("HighScoreActivity", "Scores loaded: $scores")
        if (scores.isEmpty()) {
            showMessage("No scores found in ScoreManager")
        }
    }

    private fun backToMenu() {
        finish()
    }

    fun showMessage(txt: String) {
        Toast.makeText(applicationContext,txt, Toast.LENGTH_SHORT).show()
        vibrateOnce(this)
    }
    @Suppress("DEPRECATION")
    @SuppressLint("ObsoleteSdkInt")
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

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        showMessage("Permissions denied: $perms")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        showMessage("Permissions granted: $perms")
    }
}


