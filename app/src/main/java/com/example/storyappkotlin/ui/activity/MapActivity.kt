package com.example.storyappkotlin.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyappkotlin.R
import com.example.storyappkotlin.data.remote.Result
import com.example.storyappkotlin.data.remote.dto.StoryDto
import com.example.storyappkotlin.databinding.ActivityMapBinding
import com.example.storyappkotlin.ui.viewmodel.StoryViewModel
import com.example.storyappkotlin.ui.viewmodel.ViewModelFactory
import com.example.storyappkotlin.utils.SharedPreferenceUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val tag = MapActivity::class.java.simpleName
    private lateinit var binding: ActivityMapBinding
    private lateinit var pref: SharedPreferenceUtil
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private var stories: List<StoryDto>? = null
    private var isMapReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate MapActivity")

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.appBar.toolbarTitleAppBar
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.map)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        pref = SharedPreferenceUtil(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val factory = ViewModelFactory.getInstance(this)
        val storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        val token = "Bearer ${pref.getToken()}"
        val page = null
        val size = null
        val location = 1

        storyViewModel.getStories(this, token, page, size, location)
        storyViewModel.getStoryResult().observe(this) { result ->
            if ( result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.pbLoading.visibility = View.GONE
                        stories = result.data.listStory.orEmpty()
                        addStoryMarkers()
                        Log.d(tag,"stories size = " + stories?.size)
                    }
                    is Result.Error -> {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(
                            this,
                            getString(R.string.failed) + ": ${result.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        isMapReady = true
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isIndoorLevelPickerEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        enableMyLocation()
        addStoryMarkers()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
            addStoryMarkers()
        }
    }

    private fun addStoryMarkers() {
        if (!isMapReady || stories == null) return

        stories?.forEach { story ->
            val lat = story.lat
            val lon = story.lon
            if (lat != null && lon != null) {
                val location = LatLng(lat, lon)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(story.name)
                        .snippet(story.description)
                )
            }
        }

        // Center map on user's current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 6f))
                } else {
                    // fallback to first story
                    centerToFirstStory()
                }
            }
        } else {
            centerToFirstStory()
        }
    }

    private fun centerToFirstStory() {
        stories?.firstOrNull()?.let { first ->
            if (first.lat != null && first.lon != null) {
                val firstLocation = LatLng(first.lat, first.lon)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 6f))
            }
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1001
            )
        }
    }
}