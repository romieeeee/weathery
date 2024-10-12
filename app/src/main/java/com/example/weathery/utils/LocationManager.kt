package com.example.weathery.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices

class LocationManager(private val context: Context) {

    // 위치 권한을 확인하는 함수
    fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 위치 권한을 요청하는 함수
    fun requestLocationPermission(callback: (Boolean) -> Unit) {
        val launcher = (context as FragmentActivity).activityResultRegistry.register(
            "key",
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            callback(isGranted)
        }
        launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // 마지막 위치를 가져오는 함수
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(onSuccess: (Location?) -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("Location", "getLastKnownLocation :: called")

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }
}
