package com.example.weathery.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.R
import com.example.weathery.adapter.HomeAdapter
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.database.WeatherEntity
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "HomeFragment"

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

    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }
    private val cityDao by lazy { db.cityDao() } // CityDao 초기화
    private val weatherDao by lazy { db.weatherDao() } // WeatherDao 초기화

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        dotsIndicator = view.findViewById(R.id.dots_indicator)

        // 빈 데이터로 초기화하고, 데이터를 동적으로 로드
        adapter = HomeAdapter(requireActivity(), mutableListOf(), listOf())
        viewPager.adapter = adapter

        dotsIndicator.attachTo(viewPager)

        // 데이터 로드하여 ViewPager 업데이트
        loadWeatherData()
    }

    // db 조회해서 데이터 가져오기
    fun loadWeatherData() {
        Log.d(TAG, "loadWeatherData :: called")
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val cities = cityDao.getAllCities()
            val weatherDataList = mutableListOf<WeatherEntity>()
            val cityNames = mutableListOf<String>()

            for (city in cities) {
                val weatherData = weatherDao.getLatestWeatherByCityId(city.cityId)
                if (weatherData != null) {
                    weatherDataList.add(weatherData)
                    cityNames.add(city.cityName)
                }
            }
            Log.d(TAG, "loadWeatherData :: cityNames = $cityNames")

            // UI 업데이트
            withContext(Dispatchers.Main) {
                adapter.updateData(weatherDataList, cityNames)
            }
        }
    }
}
