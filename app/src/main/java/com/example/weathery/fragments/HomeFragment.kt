package com.example.weathery.fragments

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.R
import com.example.weathery.adapter.ViewPagerAdapter
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.data.WeatherResponse
import com.example.weathery.database.AppDatabase
import com.example.weathery.database.City
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter

    private lateinit var locationManager: LocationManager

    // Room 데이터베이스 초기화
    private val db by lazy {
        Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "weather"
        ).build()
    }

    private val cityDao by lazy { db.cityDao() } // CityDao 초기화
    private val weatherDao by lazy { db.weatherDao() } // WeatherDao 초기화

    private val weatherRepository by lazy {
        WeatherRepository(cityDao, weatherDao) // cityDao와 weatherDao를 전달
    }

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
        adapter = ViewPagerAdapter(requireActivity(), mutableListOf())
        viewPager.adapter = adapter

        // indicator를 viewpager와 연결
        dotsIndicator = view.findViewById(R.id.dots_indicator)
        dotsIndicator.attachTo(viewPager)

        // 현재 위치를 가져와서 cities에 추가
        getCurrentLocationAndUpdateCities()
    }

    /**
     * 마지막 위치를 가져와 DB에 업데이트 하는 함수
     * 위치 권한 확인 후 마지막 위치 가져
     */
    private fun getCurrentLocationAndUpdateCities() {
        if (locationManager.checkLocationPermission()) {
            locationManager.getLastKnownLocation(
                onSuccess = { location ->
                    location?.let {
                        // 현재 위치를 기준으로 도시 데이터 가져오기
                        fetchWeatherDataForCity(it.latitude, it.longitude) // 위도, 경도로 날씨 데이터 요청
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

    private fun fetchWeatherDataForCity(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.Main).launch {
            val cityName = getCityNameFromCoordinates(lat, lon)
            val cityEntity = City(cityName = cityName, latitude = lat, longitude = lon)

            // 리포지토리에서 캐싱된 데이터 또는 새로운 날씨 데이터 가져오기
            val weather = weatherRepository.fetchWeatherForCity(cityEntity)

            weather?.let {
                // WeatherResponse를 사용해야 하므로 Weather 객체의 정보를 WeatherResponse로 변환해야 함
                val response = WeatherResponse(it) // 가정: WeatherResponse의 생성자가 Weather를 받도록 변경
                val processor = WeatherDataProcessor(response) // WeatherResponse를 인자로 전달

                // 어댑터에 데이터를 추가합니다.
                adapter.updateData(listOf(processor), listOf(cityName), listOf(getCurrentDate())) // 새로운 데이터와 함께 도시명, 날짜 전달
            }
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

    // 현재 날짜를 반환하는 헬퍼 메소드
    private fun getCurrentDate(): String {
        // 현재 날짜를 포맷팅하는 코드 구현
        // 예: "yyyy-MM-dd" 형식으로 반환
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}