package com.example.weathery

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        val menuButton: ImageView = findViewById(R.id.menu)
        val searchButton: ImageView = findViewById(R.id.search)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // 드로어 열기
        }

        searchButton.setOnClickListener {
            // 검색 버튼 클릭 처리
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())  // HomeFragment로 교체
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
