package com.example.wasim

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.core.ext.viewBinding
import com.example.wasim.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

@AndroidEntryPoint
class FlowActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)

    private val locationRequest = LocationRequest.Builder(0L)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setIntervalMillis(0L)
        .setMinUpdateIntervalMillis(0L)
        .setMaxUpdates(100)
        .build()

    @SuppressLint("MissingPermission")
    fun FusedLocationProviderClient.locationFlow() = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                try {
                    result.lastLocation?.let { it1 -> trySend(it1).isSuccess }
                } catch (e: Exception) {
                    Timber.e(e.toString())
                }
            }
        }
        if (isPermissionsGranted()) {
            requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
                .addOnFailureListener { e ->
                    close(e) // in case of exception, close the Flow
                }
        } else {
            requestLocationPermission()
        }
        // clean up when Flow collection ends
        awaitClose {
            removeLocationUpdates(callback)
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.entries.first().value && permissions.entries.last().value) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.locationFlow().lifecycleAwareCollect(this) {
                    Timber.e(it.toString())
                    binding.greetings.text = it.toString()
                }
            }
        }

    private fun isPermissionsGranted(): Boolean {
        return (
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
               )
    }

    private fun requestLocationPermission() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestLocationPermission()
    }
}
