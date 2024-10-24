package com.example.weathery.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.R
import com.example.weathery.adapter.HomeAdapter
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.database.WeatherEntity
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
import com.example.weathery.utils.WeatheryManager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val weatherDataList = mutableListOf<WeatherEntity>() // 날씨 데이터 리스트
    private val cityNames = mutableListOf<String>() // 도시 이름 리스트

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
        adapter = HomeAdapter(requireActivity(), mutableListOf(), cityNames)
        viewPager.adapter = adapter

        // indicator를 viewpager와 연결
        dotsIndicator = view.findViewById(R.id.dots_indicator)
        dotsIndicator.attachTo(viewPager)

        weatheryManager = WeatheryManager(cityDao, weatherDao, weatherRepository)

        // 위치 정보를 가져오고 그 이후 날씨 데이터를 가져오는 함수 호출
        getCurrentLocation()
    }

    /**
     * 현재 위치를 가져오는 함수
     * 위치를 성공적으로 가져오면, fetchWeather을 호출하여 날씨 데이터 업데이트
     */
    private fun getCurrentLocation() {
        if (locationManager.checkLocationPermission()) {
            locationManager.getLastKnownLocation(
                onSuccess = { location ->
                    location?.let {
                        // 위치 정보를 가져온 후 날씨 데이터 요청 함수 호출
                        fetchWeather(it.latitude, it.longitude)
                    }
                },
                onFailure = { exception -> Log.e("Location", "위치 가져오기 실패: ${exception.message}") }
            )
        } else {
            Log.d("Location", "위치 권한이 거부되었습니다.")
        }
    }

    /**
     * 주어진 위도와 경도를 기반으로 날씨 데이터를 가져오고 UI 업데이트
     */
    private fun fetchWeather(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 도시명 가져오기
                val cityName = locationManager.getCityNameFromCoord(latitude, longitude)

                // 도시 정보 저장 (cityId 반환)
                val cityId = weatheryManager.saveCity(cityName, latitude, longitude)

                // 날씨 데이터 가져오기
                val weatherData = weatheryManager.fetchWeatherData(cityId, latitude, longitude)

                weatherData?.let { data ->
                    weatherDataList.add(data)
                    cityNames.add(cityName)

                    // UI 업데이트
                    withContext(Dispatchers.Main) {
                        adapter.updateData(weatherDataList, cityNames)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching weather data: ${e.message}")
            }
        }
    }
}