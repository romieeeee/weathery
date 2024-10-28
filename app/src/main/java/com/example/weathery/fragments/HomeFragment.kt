package com.example.weathery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.MainActivity
import com.example.weathery.R
import com.example.weathery.adapter.HomeAdapter
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
import com.example.weathery.utils.WeatheryManager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

private const val TAG = "main function"

/**
 * 메인 화면
 * - 사용자의 현재 위치 가져오기
 * - 위치를 토대로 도시명 가져오기
 * - 날씨 api 호출하기
 */
class HomeFragment : Fragment() {

    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: HomeAdapter

    private lateinit var locationManager: LocationManager
    private lateinit var weatheryManager: WeatheryManager

    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }

    private val cityDao by lazy { db.cityDao() } // CityDao 초기화
    private val weatherDao by lazy { db.weatherDao() } // WeatherDao 초기화
    private val weatherRepository by lazy {
        WeatherRepository(
            cityDao,
            weatherDao
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity

        locationManager = LocationManager(requireContext())
        weatheryManager = WeatheryManager(cityDao, weatherDao, weatherRepository)

        viewPager = view.findViewById(R.id.viewPager)

        // 어댑터 초기화
        adapter = HomeAdapter(requireActivity(), mainActivity.weatherDataList, mainActivity.cityNames)
        viewPager.adapter = adapter

        // indicator를 viewpager와 연결
        dotsIndicator = view.findViewById(R.id.dots_indicator)
        dotsIndicator.attachTo(viewPager)

        // 초기 데이터 설정
        adapter.updateData(mainActivity.weatherDataList, mainActivity.cityNames)
    }

    // MainActivity에서 호출하여 데이터를 업데이트하는 함수
    fun updateViewPager() {
        val mainActivity = activity as MainActivity
        adapter.updateData(mainActivity.weatherDataList, mainActivity.cityNames)
    }
}