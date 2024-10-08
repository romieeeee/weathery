package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.weathery.R
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.network.RetrofitClient
import com.example.weathery.network.WeatherApi
import com.example.weathery.utils.ApiKey
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var tv_location: TextView
    private lateinit var tv_today_date: TextView
    private lateinit var tv_now_temp: TextView
    private lateinit var tv_now_weather: TextView

    private lateinit var tv_rainfall: TextView
    private lateinit var tv_wind: TextView
    private lateinit var tv_humidity: TextView

    // 위치 정보 권환을 위한 ActivityResultLauncher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) { // 권한이 부여 시 위치 요청
            requestLocation()
        } else {
            Log.e("Location", "Permission denied")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        tv_location = view.findViewById(R.id.location)
        tv_today_date = view.findViewById(R.id.today_date)
        tv_now_temp = view.findViewById(R.id.now_temperature)
        tv_now_weather = view.findViewById(R.id.now_weather)

        tv_rainfall = view.findViewById(R.id.rainfall)
        tv_wind = view.findViewById(R.id.wind)
        tv_humidity = view.findViewById(R.id.humidity)

        // 권한 확인 및 요청
        if (checkLocationPermission()) {
            // 권한이 있으면 위치 요청
            requestLocation()
        } else {
            // 권한이 없으면 권한 요청
            requestLocationPermission()
        }

        setTodayDate(tv_today_date)

        setLocationText(tv_location)
    }

    /**
     * 위치 권한을 확인하는 함수
     */
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 위치 권한을 요청하는 함수
     */
    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION) // 권한 요청
    }

    /**
     * 위치 정보를 요청하는 함수
     * (권한이 부여된 경우에만 호출)
     */
    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val lat = it.latitude
                val lon = it.longitude
                Log.d("Location", "latitude: $lat, longitude: $lon")
                fetchWeatherData(lat, lon)
            } ?: run {
                Log.e("Location", "No location found")
            }
        }
    }

    /**
     * 날씨 데이터를 받아오는 함수
     * 위치 정보를 바탕으로 API 호출
     */
    private fun fetchWeatherData(lat: Double, lon: Double) {
        val baseDate = getFormattedDate()
        val baseTime = getFormattedTime()

        val apiKey = ApiKey.API_KEY
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // API 요청
                val response = RetrofitClient.getInstance().create(WeatherApi::class.java)
                    .getWeatherData(
                        apiKey,
                        1,
                        1000,
                        "JSON",
                        baseDate,
                        baseTime,
                        lat.toInt(),
                        lon.toInt()
                    )
                Log.d("API Request", "${lat.toInt()}, ${lon.toInt()}") // 요청 URL

                withContext(Dispatchers.Main) {
                    if (response.response.header.resultCode == "00") {
                        val processor = WeatherDataProcessor(response)

                        // UI 업데이트
                        updateWeatherUI(
                            processor.getCurrentTemperature(),
                            processor.getSkyCondition(),
                            processor.getRainfall(),
                            processor.getWindSpeed(),
                            processor.getHumidity()
                        )
                    } else {
                        // API 호출이 실패한 경우 UI에 에러 메시지 표시
                        Log.e("API", "Error: ${response.response.header.resultMsg}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API", "API Call Failed: ${e.message}")
                }
            }
        }
    }

    /**
     * UI를 업데이트하는 함수
     */
    @SuppressLint("SetTextI18n")
    private fun updateWeatherUI(
        temperature: String?,
        skyCondition: String?,
        rainfall: String?,
        windSpeed: String?,
        humidity: String?
    ) {
        tv_now_temp.text = temperature ?: "정보 없음"
        tv_now_weather.text = skyCondition ?: "정보 없음"
        tv_rainfall.text = "${rainfall ?: "정보 없음"}%"
        tv_wind.text = "${windSpeed ?: "정보 없음"}m/s"
        tv_humidity.text = "${humidity ?: "정보 없음"}%"
    }

    /**
     * 현재 날짜를 yyyyMMdd 형식으로 반환하는 함수
     */
    private fun getFormattedDate(): String {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return currentTime.format(formatter)
    }

    private fun setTodayDate(textView: TextView){
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREA)

        textView.text = currentTime.format(formatter)
    }

    /**
     * 현재 시간을 HHmm 형식으로 반환하는 함수
     */
    private fun getFormattedTime(): String {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        return currentTime.format(formatter)
    }

    /**
     * latitude, longitude로 주소를 받아오는 함수
     */
    private fun getAddress(lat: Double, lng: Double): List<Address>? {
        lateinit var address: List<Address>

        return try {
            val geocoder = Geocoder(requireContext(), Locale.KOREA)
            address = geocoder.getFromLocation(lat, lng, 1) as List<Address>
            address
        } catch (e: IOException) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun setLocationText(textView: TextView) {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    val address = getAddress(location.latitude, location.longitude)?.get(0)
                    val locationText = address?.let {
                        "${it.adminArea}\n${it.thoroughfare}"
                    } ?: "주소를 찾을 수 없음"

                    textView.text = locationText

                    // ViewModel에 주소 업데이트
//                    sharedViewModel.updateLocationText(locationText)
                }
            }
            .addOnFailureListener { fail ->
                textView.text = fail.localizedMessage
            }
    }
}
