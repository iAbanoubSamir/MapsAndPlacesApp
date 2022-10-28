package com.abanoub.places

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.abanoub.places.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private val TAG = "MapActivityTag"
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private var isLocationPermissionGranted = false
    private val LOCATION_PERMISSION_REQUEST_CODE = 400
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocation: FusedLocationProviderClient
    private val DEFAULT_ZOOM = 15f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLocationPermission()
    }

    private fun init() {
        binding.searchBox.editText?.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || keyEvent.action == KeyEvent.ACTION_DOWN
                || keyEvent.action == KeyEvent.KEYCODE_ENTER
            ) {
                geolocate()
            }
            return@setOnEditorActionListener false
        }

        binding.locateMe.setOnClickListener {
            getDeviceLocation()
        }

        hideSoftKeyboard()
    }

    private fun geolocate() {
        val searchQuery = binding.searchBox.editText?.text.toString()

        val geoCoder = Geocoder(this)

        var addressesList: List<Address> = ArrayList<Address>()

        try {
            addressesList = geoCoder.getFromLocationName(searchQuery, 1)
        } catch (e: IOException) {
            Log.d(TAG, "geolocate: ${e.message}")
        }

        if (addressesList.isNotEmpty()) {
            val address = addressesList[0]
//            Toast.makeText(this, address.toString(), Toast.LENGTH_LONG).show()

            moveMapCamera(
                LatLng(address.latitude, address.longitude),
                DEFAULT_ZOOM,
                address.getAddressLine(0)
            )
        }
    }

    private fun getDeviceLocation() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        try {
            if (isLocationPermissionGranted) {
                val location = fusedLocation.lastLocation
                location.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "getDeviceLocation: Found Location")
                        val lastLocation = task.result
                        moveMapCamera(
                            LatLng(lastLocation.latitude, lastLocation.longitude),
                            DEFAULT_ZOOM,
                            "Current Location"
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "getDeviceLocation: ${e.message}")
        }
    }

    private fun moveMapCamera(lng: LatLng, zoom: Float, title: String) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, zoom))

        if (!title.equals("Current Location")) {
            val markerOptions = MarkerOptions()
                .position(lng)
                .title(title)
            mGoogleMap.addMarker(markerOptions)
        }

        hideSoftKeyboard()
    }

    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            Toast.makeText(applicationContext, "Map is Ready", Toast.LENGTH_SHORT).show()

            if (isLocationPermissionGranted) {
                getDeviceLocation()
                mGoogleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = false

                    init()
                }
            }
        }

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
            initMap()
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
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