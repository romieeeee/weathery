package com.example.weathery.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import java.util.Locale

/**
 * 위치 권한 확인 및 요청
 */
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
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    // 좌표로 도시명 가져오는 함수
    fun getCityNameFromCoord(lat: Double, lon: Double): String {
        val geocoder = Geocoder(context, Locale.KOREA)

        return try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                address?.let {
                    if (it.locality.isNullOrEmpty()) {
                        "${it.adminArea}\n${it.thoroughfare}"
                    } else if (it.thoroughfare.isNullOrEmpty()) {
                        "${it.adminArea}\n${it.locality}"
                    } else {
                        "${it.locality}\n${it.thoroughfare}"
                    }
                } ?: "주소를 찾을 수 없음"

            } else {
                "알 수 없는 위치"
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "도시명 가져오기 실패: ${e.message}")
            "알 수 없는 위치"
        }
    }
}
