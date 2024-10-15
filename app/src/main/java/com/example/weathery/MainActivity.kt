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
import com.example.weathery.utils.LocationManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var locationManager: LocationManager

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

        // 위치 권한 확인 및 요청
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        Log.d("weathery", "requestLocationPermissionl :: called")
        if (locationManager.checkLocationPermission()) {
            Log.d("Location", "위치 권한이 이미 허용되었습니다.")
        } else {
            locationManager.requestLocationPermission { isGranted ->
                if (isGranted) {
                    Log.d("Location", "위치 권한이 허용되었습니다.")
                } else {
                    Log.d("Location", "위치 권한이 거부되었습니다.")
                }
            }
        }
    }
}
