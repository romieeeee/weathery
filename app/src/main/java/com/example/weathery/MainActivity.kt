package com.example.weathery

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.database.WeatherEntity
import com.example.weathery.fragments.GMapFragment
import com.example.weathery.fragments.HomeFragment
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
import com.example.weathery.utils.WeatheryManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // database
    private val db by lazy { DatabaseProvider.getDatabase(this) }
    private val cityDao by lazy { db.cityDao() } // CityDao 초기화
    private val weatherDao by lazy { db.weatherDao() } // WeatherDao 초기화
    private val weatherRepository by lazy { WeatherRepository(cityDao, weatherDao) }

    val weatherDataList = mutableListOf<WeatherEntity>() // 날씨 데이터 리스트
    val cityNames = mutableListOf<String>() // 도시 이름 리스트

    private lateinit var weatheryManager: WeatheryManager
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

        // LocationManager 초기화
        locationManager = LocationManager(this)
        weatheryManager = WeatheryManager(cityDao, weatherDao, weatherRepository)

        // 위치 권한 확인 및 날씨 데이터 초기화
        if (weatherDataList.isEmpty()) {
            getCurrentLocationAndWeather()
        }
    }

    // 위치와 날씨 데이터 가져오기
    private fun getCurrentLocationAndWeather() {
        if (locationManager.checkLocationPermission()) {
            locationManager.getLastKnownLocation(
                onSuccess = { location ->
                    location?.let {
                        fetchWeather(it.latitude, it.longitude)
                    }
                },
                onFailure = { Log.e("Location", "위치 가져오기 실패") }
            )
        }
    }

    private fun fetchWeather(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val cityName = locationManager.getCityNameFromCoord(latitude, longitude)
            val cityId = weatheryManager.saveCity(cityName, latitude, longitude)
            val weatherData = weatheryManager.fetchWeatherData(cityId, latitude, longitude)
            weatherData?.let { data ->
                weatherDataList.add(data)
                cityNames.add(cityName)

                // Notify fragments to update UI
                withContext(Dispatchers.Main) {
                    updateFragments()
                }
            }
        }
    }

    // 두 Fragment에서 데이터를 업데이트하도록 호출하는 메서드
    private fun updateFragments() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            when (fragment) {
                is HomeFragment -> fragment.updateViewPager()
                is GMapFragment -> fragment.updateList()
            }
        }
    }
}
