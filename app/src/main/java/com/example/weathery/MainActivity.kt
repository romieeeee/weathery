package com.example.weathery

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // DrawerLayout 설정
        drawerLayout = findViewById(R.id.drawer_layout)
        val menuButton: ImageView = findViewById(R.id.menu)
        val searchButton: ImageView = findViewById(R.id.search)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // 드로어 열기
        }

        searchButton.setOnClickListener {
            // TODO: 검색 기능 구현
        }

        // navigation 설정
        navController = findNavController(R.id.nav_host_fragment)

    }

}
