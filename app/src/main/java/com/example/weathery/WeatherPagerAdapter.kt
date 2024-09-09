package com.example.weathery

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WeatherPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val fragments: ArrayList<Fragment> = ArrayList()

    // 페이지 갯수 설정
    override fun getItemCount(): Int = fragments.size

    // 각 위치에 맞는 프래그먼트 반환
    override fun createFragment(position: Int): Fragment = fragments[position]

    // 프래그먼트 추가 메서드
    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
    }
}
