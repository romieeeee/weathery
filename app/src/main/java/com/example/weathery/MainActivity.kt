package com.example.weathery

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
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
        val toolbar: Toolbar = findViewById(R.id.custom_toolbar)
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
//        val navView: NavigationView = findViewById(R.id.navigation_view)
//        navView.setupWithNavController(navController)
    }

    // 메뉴를 툴바에 표시
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        // SearchView 설정
        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색어 제출 시 처리할 작업
                Toast.makeText(this@MainActivity, "Search: $query", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색어가 변경될 때 처리할 작업
                return false
            }
        })

        return true
    }

    // 메뉴 아이템 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_list -> {
                // 리스트 아이콘 클릭 시 처리할 작업
                Toast.makeText(this, "List clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 뒤로가기 버트 동작 처리
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}