package com.example.weathery

import android.os.Bundle
import android.view.View
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
    private lateinit var searchButton: android.widget.SearchView

    private lateinit var navController: NavController
    private lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init
        toolbar = findViewById(R.id.toolbar)
        listButton = findViewById(R.id.action_list)
        searchButton = findViewById(R.id.action_search)

        drawerLayout = findViewById(R.id.drawer_layout)

        navController = findNavController(R.id.nav_host_fragment)
        navView = findViewById(R.id.navigation_view)

        // toolbar 설정
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 title 숨기기

        listButton.setOnClickListener(View.OnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        })


        // NavigationView와 NavController 연결
        navView.setupWithNavController(navController)

        // 위치 권한 확인 및 요청 로직
        locationManager = LocationManager(this)
        if (!locationManager.checkLocationPermission()) {
            locationManager.requestLocationPermission { isGranted ->
                if (isGranted) {
                    // 권한 부여 시
                } else {
                    // 권한 거부 시 처리 (필요시 추가 가능)
                }
            }
        }
    }
}
