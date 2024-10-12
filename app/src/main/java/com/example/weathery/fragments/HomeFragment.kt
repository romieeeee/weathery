package com.example.weathery.fragments

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.R
import com.example.weathery.adapter.ViewPagerAdapter
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private val weatherRepository by lazy { WeatherRepository() }
    private val weatherDataList = mutableListOf<WeatherDataProcessor>()
    private val cityNames = mutableListOf<String>() // 도시명 리스트
    private val dates = mutableListOf<String>() // 날짜 리스트
//    private val cities = mutableListOf<Pair<Double, Double>>() // 도시 좌표 목록

    private lateinit var locationManager: LocationManager

    // 도시 좌표 목록 (위도, 경도만 가지고 있음)
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

        locationManager = LocationManager(requireContext())
        viewPager = view.findViewById(R.id.viewPager)

        // 어댑터 초기화
        adapter = ViewPagerAdapter(requireActivity(), weatherDataList, cityNames, dates)
        viewPager.adapter = adapter

        // indicator를 viewpager와 연결
        dotsIndicator = view.findViewById(R.id.dots_indicator)
        dotsIndicator.attachTo(viewPager)

        // 현재 위치를 가져와서 cities에 추가
        getCurrentLocationAndUpdateCities()
    }

    private fun getCurrentLocationAndUpdateCities() {
        if (locationManager.checkLocationPermission()) {
            locationManager.getLastKnownLocation(
                onSuccess = { location ->
                    location?.let {
                        // 현재 위치를 cities의 첫 번째 요소로 추가
                        cities.add(0, Pair(it.latitude, it.longitude))
                        fetchWeatherDataForCities() // 도시별 날씨 데이터 받아오기
                    }
                },
                onFailure = { exception ->
                    Log.e("Location", "위치 가져오기 실패: ${exception.message}")
                }
            )
        } else {
            // 권한이 없는 경우 처리
            Log.d("Location", "위치 권한이 거부되었습니다.")
        }
    }

    private fun fetchWeatherDataForCities() {
        CoroutineScope(Dispatchers.Main).launch {
            weatherDataList.clear() // 기존 데이터 삭제
            cityNames.clear() // 기존 도시명 삭제
            dates.clear() // 기존 날짜 삭제

            for (city in cities) {
                val weatherData = fetchWeatherForCity(city.first, city.second)
                weatherData?.let {
                    weatherDataList.add(it)
                    cityNames.add(getCityNameFromCoordinates(city.first, city.second)) // 도시명 추가
                    dates.add(getFormattedDate()) // 오늘 날짜 추가
                }
            }
            adapter.notifyDataSetChanged() // 어댑터 전체 갱신
        }
    }

    private suspend fun fetchWeatherForCity(lat: Double, lon: Double): WeatherDataProcessor? {
        return try {
            val result = weatherRepository.fetchWeatherData(lat, lon)
            result.getOrNull() // 성공 시 WeatherDataProcessor 반환
        } catch (e: Exception) {
            e.printStackTrace()
            null // 실패 시 null 반환
        }
    }

    private fun getCityNameFromCoordinates(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.KOREA)

        return try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                address?.let {
                    if (it.locality.isNullOrEmpty()) {
                        "${it.adminArea}\n${it.thoroughfare}"
                    } else if (it.thoroughfare.isNullOrEmpty()) {
                        "${it.adminArea}\n${it.locality}"
                    } else {
                        "${it.locality}\n${it.thoroughfare}"
                    }
                } ?: "주소를 찾을 수 없음"
            } else {
                "알 수 없는 위치"
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "도시명 가져오기 실패: ${e.message}")
            "알 수 없는 위치"
        }
    }

    private fun getFormattedDate(): String {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        return currentTime.format(formatter)
    }
}
