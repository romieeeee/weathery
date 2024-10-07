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

        val titleView = toolbar.findViewById<TextView>(R.id.toolbar_title)

        // Viewmodel로부터 위치 정보 받아서 toolbar에 set
        sharedViewModel.locationText.observe(this, Observer { location ->
            titleView.text = location
        })

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

    override fun onResume() {
        super.onResume()

        // 프래그먼트 전환 시에도 ViewModel을 통해 Toolbar 텍스트 업데이트
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val titleView = toolbar.findViewById<TextView>(R.id.toolbar_title)

        // ViewModel로부터 위치 정보를 다시 받아서 업데이트
        sharedViewModel.locationText.value?.let { location ->
            titleView.text = location
        }
    }
}