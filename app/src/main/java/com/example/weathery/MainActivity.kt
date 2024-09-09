package com.example.weathery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 앱이 처음 실행되었을 때 프래그먼트를 로드
        if (savedInstanceState == null) {
            // HomeFragment를 처음 화면으로 설정
            val homeFragment = HomeFragment()
            replaceFragment(homeFragment)
        }
    }

    // 프래그먼트를 교체하는 메서드
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment) // fragment_container는 activity_main.xml에 정의된 프래그먼트가 삽입될 위치
            .commit()
    }
}
