package com.abanoub.places

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.abanoub.places.databinding.ActivityMainBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityTag"
    private val ERROR_DIALOG_REQUEST = 9001
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isGoogleServiceInstalled()) {
            init()
        }
    }

    private fun init() {
        binding.showMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }

    fun isGoogleServiceInstalled(): Boolean {
        val isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        if (isAvailable == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isGoogleServiceInstalled: Play Services Available")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)) {
            Log.d(TAG, "isGoogleServiceInstalled: UserResolvableError")
            val dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this, isAvailable, ERROR_DIALOG_REQUEST)
            dialog?.show()
            return false
        } else {
            Log.d(TAG, "isGoogleServiceInstalled: Error")
        }
        return false
    }

}