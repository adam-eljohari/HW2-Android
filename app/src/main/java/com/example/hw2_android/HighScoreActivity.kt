package com.example.hw2_android

import android.Manifest
import android.annotation.SuppressLint
import android.location.LocationManager
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.example.hw2_android.fragments.HighScoreFragment
import com.example.hw2_android.interfaces.CallbackHighScoreClicked
import com.example.hw2_android.utilities.Constants
import com.example.hw2_android.logic.ScoreManager
import com.example.hw2_android.item_model.Score
import pub.devrel.easypermissions.EasyPermissions


class HighScoreActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {



    private lateinit var mainActivity:MainActivity

    private lateinit var main_FRAME_list: FrameLayout
    private lateinit var main_FRAME_map: FrameLayout
//    private lateinit var mapFragment: SupportMapFragment
//    private var googleMap: GoogleMap? = null
    private lateinit var highScoreFragment: HighScoreFragment
    private var currentScore: Int = 0
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)
        findViews()
        initViews()
    }

    private fun findViews() {
        main_FRAME_list = findViewById(R.id.main_FRAME_list)
        main_FRAME_map = findViewById(R.id.main_FRAME_map)
    }

    @SuppressLint("CommitTransaction")
    private fun initViews() {
        highScoreFragment = HighScoreFragment()
//        mapFragment = SupportMapFragment.newInstance()
        setupHighScoreClickListener()
        supportFragmentManager.beginTransaction().replace(R.id.main_FRAME_list, highScoreFragment)
//            .replace(R.id.main_FRAME_map, mapFragment)
//            .commit()
//        mapFragment.getMapAsync { map ->
//            googleMap = map
//            googleMap?.uiSettings?.isZoomControlsEnabled = true
//            updateMapWithScores()
//        }
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        initLocationListener()
    }

    private fun setupHighScoreClickListener() {
        highScoreFragment.highScoreClicked = object : CallbackHighScoreClicked {
            override fun highScoreClicked(lat: Double, lon: Double) {
//                googleMap?.animateCamera(
//                    CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15f)
//                )
                mainActivity.showMessage("Zooming to: Lat=$lat, Lon=$lon")
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
//            fetchAndAddScore()
            updateMapWithScores()
            highScoreFragment.updateHighScore()
            if (ScoreManager.getInstance(this).scores.isEmpty() && currentScore == 0) {
                highScoreFragment.highScore_LBL_title.visibility = View.VISIBLE
            } else {
                highScoreFragment.highScore_LBL_title.visibility = View.GONE
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
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000L, 10f, locationListener)
        } catch (e: SecurityException) {
            mainActivity.showMessage("Location permissions are required to save high scores.")
        }
    }

    private fun stopLocationListener() {
        locationManager.removeUpdates(locationListener)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchAndAddScore() {
//        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) {
//                val latitude = location.latitude
//                val longitude = location.longitude
//                saveScoreIfEligible(latitude, longitude)
//            } else {
//                mainActivity.showMessage("Unable to fetch location")
//            }
//        }.addOnFailureListener {
//            mainActivity.showMessage("Failed to fetch location")
//        }
    }

    private fun saveScoreIfEligible(latitude: Double, longitude: Double) {
        if (currentScore == 0) return
        val scoreManager = ScoreManager.getInstance(this)
        val scores = scoreManager.scores
        if (scores.size < 10 || currentScore > scores.last().scoreValue) {
            val newScore = Score(currentScore, latitude, longitude)
            scoreManager.updateScore(newScore)
            highScoreFragment.updateHighScore()
//            googleMap?.addMarker(
//                MarkerOptions()
//                    .position(LatLng(latitude, longitude))
//                    .title("Score: $currentScore")
//            )
//            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15f))
        }
        currentScore = 0
    }

    private fun updateMapWithScores() {
        val scores = ScoreManager.getInstance(this).scores
        if (scores.isNotEmpty()) {
//            googleMap?.clear()
//            val boundsBuilder = LatLngBounds.Builder()
//            scores.forEach { score ->
//                val location = LatLng(score.latitude, score.longitude)
//                googleMap?.addMarker(
//                    MarkerOptions()
//                        .position(location)
//                        .title("Score: ${score.scoreValue}")
//                        .snippet("Lat: ${score.latitude}, Lon: ${score.longitude}")
//                )
//                boundsBuilder.include(location)
//            }
//            val bounds = boundsBuilder.build()
//            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
//            googleMap?.setOnMarkerClickListener { marker ->
//                marker.showInfoWindow()
//                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
//                mainActivity.showMessage("Clicked on: ${marker.title}")
//                true
//            }
        } else {
            mainActivity.showMessage("No scores to display on the map.")
        }
    }

    private fun debugScores() {
        val scores = ScoreManager.getInstance(this).scores
        Log.d("HighScoreActivity", "Scores loaded: $scores")
        if (scores.isEmpty()) {
            mainActivity.showMessage("No scores found in ScoreManager")
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        mainActivity.showMessage("Permissions denied: $perms")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        mainActivity.showMessage("Permissions granted: $perms")
    }






}