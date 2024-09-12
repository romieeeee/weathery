package com.example.weathery

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity(), ToolbarUpdater {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navController: NavController

    lateinit var iv_menu: ImageView
    lateinit var iv_search: ImageView
    lateinit var tv_title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 title 숨기기

        // 초기화
        iv_menu = findViewById(R.id.menu)
        iv_search = findViewById(R.id.search)
        tv_title = findViewById(R.id.title)

        // drawer layout 설정
        drawerLayout = findViewById(R.id.drawer_layout)

        // navigation 설정
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun updateToolbar(menuResId: Int, title: String, tvColor: Int, searchResId: Int) {

        // 아이콘 및 타이틀 변경
        iv_menu.setImageResource(menuResId)
        tv_title.text = title
        iv_search.setImageResource(searchResId)

        // 타이틀 색상 변경
        val color = resources.getColor(tvColor, null)
        tv_title.setTextColor(color)

        // toolbar 기능 제어
        iv_menu.setOnClickListener {
            val currentFragment = navController.currentDestination?.id?.let {
                supportFragmentManager.findFragmentById(it)
            }

            when (currentFragment) {
                is HomeFragment -> {
                    // HomeFragment에서는 네비게이션 드로어 열기
                    openDrawer()
                }

                is DetailFragment -> {
                    // DetailFragment에서는 뒤로 가기 기능
                    onBackPressedDispatcher.onBackPressed()
                }

                else -> {
                    // ...
                }

            }
        }

        // search 기능 공통 설정
        iv_search.setOnClickListener {
            startSearch()
        }
    }

    private fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START) // 드로어 열기
    }

    private fun startSearch() {
        // 검색 기능 구현
    }
}