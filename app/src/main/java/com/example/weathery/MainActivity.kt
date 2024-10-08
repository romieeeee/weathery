package com.example.weathery

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.viewmodels.SharedViewModel
import com.google.android.material.navigation.NavigationView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    private val sharedViewModel: SharedViewModel by viewModels()

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
            setOf(R.id.mainFragment), // MainFragment에서는 메뉴 아이콘
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
            ContextCompat.getColor(this, R.color.black),
            PorterDuff.Mode.SRC_IN
        )
    }

    // 뒤로가기 버트 동작 처리
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}