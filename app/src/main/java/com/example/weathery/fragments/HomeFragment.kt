package com.example.weathery.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.R
import com.example.weathery.adapter.ViewPagerAdapter
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private val weatherRepository by lazy { WeatherRepository() }
    private var weatherDataList = mutableListOf<WeatherDataProcessor>() // 도시별 날씨 데이터를 담는 리스트

    // 현재 위치를 가져오는 LocationManager
    private lateinit var locationManager: LocationManager

    // 도시 좌표 목록
    private val cities = mutableListOf(
        Pair(37.5665, 126.9780), // 서울
        Pair(35.1796, 129.0756), // 부산
        Pair(33.4996, 126.5312)  // 제주
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewPager)

        // 현재 위치 가져오기
        locationManager = LocationManager(requireContext())

        locationManager.getLastKnownLocation(
            onSuccess = { location ->
                location?.let {
                    // 현재 위치 추가
                    cities.add(0, Pair(it.latitude, it.longitude))
                    fetchWeatherDataForCities()
                }
            },
            onFailure = {
                // 현재 위치 가져오기 실패 시 기본 도시에 대한 날씨 정보 요청
                fetchWeatherDataForCities()
            }
        )
    }

    // 날씨 데이터를 도시별로 받아오기
    private fun fetchWeatherDataForCities() {
        CoroutineScope(Dispatchers.Main).launch {
            for (city in cities) {
                val weatherData = fetchWeatherForCity(city.first, city.second)
                weatherData?.let { weatherDataList.add(it) }
            }
            setupViewPager()
        }
    }

    // 비동기적으로 도시별 날씨 데이터를 받아오는 함수
    private suspend fun fetchWeatherForCity(lat: Double, lon: Double): WeatherDataProcessor? {
        return try {
            val result = weatherRepository.fetchWeatherData(lat, lon)
            result.getOrNull() // 성공 시 WeatherDataProcessor 반환
        } catch (e: Exception) {
            e.printStackTrace()
            null // 실패 시 null 반환
        }
    }

    // ViewPager 설정
    private fun setupViewPager() {
        adapter = ViewPagerAdapter(requireActivity(), weatherDataList)
        viewPager.adapter = adapter
    }
}
