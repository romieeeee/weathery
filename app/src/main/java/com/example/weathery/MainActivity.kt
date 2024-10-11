package com.example.weathery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weathery.utils.LocationManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 title 숨기기

        // drawer layout 설정
        drawerLayout = findViewById(R.id.drawer_layout)

        // navigation 설정
        val navController = findNavController(R.id.nav_host_fragment)

        // AppBarConfiguration 설정
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment), // HomeFragment에서는 메뉴 아이콘
            drawerLayout // DrawerLayout을 함께 사용할 때 설정
        )

        // Toolbar와 NavController 연결
        setupActionBarWithNavController(navController, appBarConfiguration)

        // NavigationView와 NavController 연결
        val navView: NavigationView = findViewById(R.id.navigation_view)
        navView.setupWithNavController(navController)

        // 위치 권한 확인 및 요청 로직
        locationManager = LocationManager(this)
        if (!locationManager.checkLocationPermission()) {
            locationManager.requestLocationPermission { isGranted ->
                if (isGranted) {
                    // 권한 부여 시
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.homeFragment) // HomeFragment로 이동
                } else {
                    // 권한 거부 시 처리 (필요시 추가 가능)
                }
            }
        }
    }

    // 뒤로가기 버튼 동작 처리
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
