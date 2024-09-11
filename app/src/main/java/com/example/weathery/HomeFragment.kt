package com.example.weathery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)
        val adapter = WeatherPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        // 기본 날씨 (서울)
        adapter.addFragment(DefaultLocFragment())

        // 지역 추가 페이지
        adapter.addFragment(AddLocFragment { newCity ->
            adapter.addFragment(DefaultLocFragment.newInstance(newCity))
            viewPager.currentItem = adapter.itemCount - 1
        })
    }
}
