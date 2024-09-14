package com.example.weathery

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 title 숨기기

        val titleView = toolbar?.findViewById<TextView>(R.id.toolbar_title)
        titleView?.text = "Berlin, Germany" // 지역명 받아와서 설정

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

        // 메뉴 아이템의 색상 변경
        val drawerToggle = toolbar.navigationIcon
        drawerToggle?.setColorFilter(
            ContextCompat.getColor(this, R.color.dark_blue),
            PorterDuff.Mode.SRC_IN
        )
    }

    // 뒤로가기 버트 동작 처리
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}