package com.example.weathery

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.adapter.WeatherPagerAdapter

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar 세부 설정
        val toolbar = (activity as? AppCompatActivity)?.findViewById<Toolbar>(R.id.toolbar)
        val titleView = toolbar?.findViewById<TextView>(R.id.toolbar_title)

        titleView?.text = "Berlin, Germany" // 지역명 받아와서 설정
        titleView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_blue))

        // 메뉴 버튼 색상 변경
        toolbar?.navigationIcon?.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.dark_blue),
            PorterDuff.Mode.SRC_IN
        )

        // ViewPager 설정
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
