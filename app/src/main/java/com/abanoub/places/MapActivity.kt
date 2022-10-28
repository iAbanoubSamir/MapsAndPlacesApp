package com.abanoub.places

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.abanoub.places.databinding.ActivityMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private var isLocationPermissionGranted = false
    private val LOCATION_PERMISSION_REQUEST_CODE = 400
    private lateinit var mGoogleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLocationPermission()
    }

    private fun getLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (ContextCompat.checkSelfPermission(applicationContext, FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            Toast.makeText(applicationContext, "Map is Ready", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isLocationPermissionGranted = false

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                LOCATION_PERMISSION_REQUEST_CODE -> {
                    isLocationPermissionGranted = true
                    initMap()
                }
            }
        }
    }
}