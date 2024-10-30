package com.example.weathery

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
import com.example.weathery.utils.WeatherManager
import com.google.android.material.navigation.NavigationView

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    // database
    private val db by lazy { DatabaseProvider.getDatabase(this) }
    private val cityDao by lazy { db.cityDao() } // CityDao 초기화
    private val weatherRepository by lazy { WeatherRepository() }

    private lateinit var weatherManager: WeatherManager
    private lateinit var locationManager: LocationManager

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var listButton: ImageButton
    private lateinit var searchButton: ImageButton

    private lateinit var navController: NavController
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init
        toolbar = findViewById(R.id.toolbar)
        listButton = findViewById(R.id.action_list)
        searchButton = findViewById(R.id.action_search)

        drawerLayout = findViewById(R.id.drawer_layout)

        navController = findNavController(R.id.nav_host_fragment)
        navView = findViewById(R.id.navigation_view)

        // Toolbar 설정
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 title 숨기기

        listButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        searchButton.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_GMapFragment)
        }

        // NavigationView와 NavController 연결
        navView.setupWithNavController(navController)

        // Manager 초기화
        locationManager = LocationManager(this)
        weatherManager = WeatherManager(cityDao)

        checkPermission()
    }

    private fun checkPermission() {
        if (locationManager.checkLocationPermission()) {
            Log.d(TAG, "위치 권한이 허용되었습니다.")

        } else {
            locationManager.requestLocationPermission { isGranted ->
                if (isGranted) {
                    Log.d(TAG, "위치 권한이 허용되었습니다.")
                } else {
                    Log.e(TAG, "위치 권한이 거부되었습니다.")
                }
            }
        }
    }

//    // 현재 위치와 날씨 데이터 가져오기
//    private fun setDefault() {
//        Log.d(TAG, "setDefault :: called")
//        locationManager.getLastKnownLocation(
//            onSuccess = { location ->
//                location?.let { getWeather(it.latitude, it.longitude) }
//            },
//            onFailure = { Log.e(TAG, "위치 가져오기 실패") }
//        )
//    }
//
//    /**
//     * 현재 위치의 날씨 정보를 가져오고 도시 정보를 DB에 저장하는 메서드
//     */
//    private fun getWeather(latitude: Double, longitude: Double) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val cityName = locationManager.getCityNameFromCoord(latitude, longitude)
//            weatherManager.saveCity(cityName, latitude, longitude)
//
//            val weatherData = weatherManager.fetchWeatherData(latitude, longitude)
//            weatherData?.let { data ->
//                withContext(Dispatchers.Main) {
//                    notifyWeatherDataUpdated()
//                }
//            }
//            Log.d(TAG, "getWeather :: cityName = $cityName, weatherData = ($weatherData)")
//        }
//    }
//
//    // 데이터를 업데이트하라고 알리는 메서드
//    private fun notifyWeatherDataUpdated() {
//        Log.d(TAG, "notifyWeatherDataUpdated :: called")
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
//            if (fragment is HomeFragment) {
//                fragment.loadWeatherData() // 데이터 새로 고침
//            }
//        }
//    }
}
