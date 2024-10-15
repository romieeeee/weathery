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
import com.example.weathery.database.AppDatabase
import com.example.weathery.database.City
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 메인 화면
 * - 사용자의 현재 위치 가져오기
 * - 위치를 토대로 도시명 가져오기
 *  - 날씨 api 호출하기?
 */
class HomeFragment : Fragment() {

    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter

    private lateinit var locationManager: LocationManager

    // Room 데이터베이스 초기화
    private val db by lazy {
        Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "weather_table"
        ).build()
    }

    private val cityDao by lazy { db.cityDao() } // CityDao 초기화
    private val weatherDao by lazy { db.weatherDao() } // WeatherDao 초기화
    private val weatherRepository by lazy {  WeatherRepository(cityDao, weatherDao) }  // cityDao와 weatherDao를 전달

    private val weatherDataList = mutableListOf<WeatherDataProcessor>() // 날씨 데이터 리스트
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
        adapter = ViewPagerAdapter(requireActivity(), mutableListOf(), cityNames)
        viewPager.adapter = adapter

        // indicator를 viewpager와 연결
        dotsIndicator = view.findViewById(R.id.dots_indicator)
        dotsIndicator.attachTo(viewPager)

        // 현재 위치를 가져와서 cities에 추가
        getCurrentLocationAndUpdateCities()
    }

    /**
     * 마지막 위치를 가져와 DB에 업데이트 하는 함수
     * 위치 권한 확인 후 마지막 위치 가져옴
     */
    private fun getCurrentLocationAndUpdateCities() {
        if (locationManager.checkLocationPermission()) {
            locationManager.getLastKnownLocation(
                onSuccess = { location ->
                    location?.let {
                        // 도시명 가져오기 및 저장
                        val cityName = getCityNameFromCoordinates(it.latitude, it.longitude)
                        val cityEntity = City(cityName = cityName, latitude = it.latitude, longitude = it.longitude)

                        CoroutineScope(Dispatchers.IO).launch {
                            cityDao.insertCity(cityEntity) // DB에 도시 정보 저장
                            fetchWeatherDataForCity(it.latitude, it.longitude) // 날씨 데이터 가져오기
                        }
                    }
                },
                onFailure = { exception ->
                    Log.e("Location", "위치 가져오기 실패: ${exception.message}")
                }
            )
        } else {
            Log.d("Location", "위치 권한이 거부되었습니다.")
        }
    }

    // 날씨 데이터 가져오는 함수 (비동기로 호출)
    private suspend fun fetchWeatherDataForCity(lat: Double, lon: Double) {
        val weatherData = fetchWeatherForCity(lat, lon)
        weatherData?.let {
            weatherDataList.add(it)
            cityNames.add(getCityNameFromCoordinates(lat, lon))

            // UI 작업은 Main 스레드에서 수행
            withContext(Dispatchers.Main) {
                adapter.updateData(weatherDataList, cityNames) // 어댑터 업데이트
            }
        }
    }

    private suspend fun fetchWeatherForCity(lat: Double, lon: Double): WeatherDataProcessor? {
        return try {
            // API에서 WeatherResponse 데이터를 가져옴
            val weatherResponse = weatherRepository.fetchWeatherResponseForCity(lat, lon)
            weatherResponse?.let { WeatherDataProcessor(it) } // WeatherResponse를 WeatherDataProcessor로 전달
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 위도와 경도를 기반으로 도시명을 가져오는 함수
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
//                    "${it.adminArea} ${it.locality} ${it.thoroughfare}" // 시 구 동 정보를 가져옴
                } ?: "주소를 찾을 수 없음"

            } else {
                "알 수 없는 위치"
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Failed to get city name: ${e.message}")
            "알 수 없는 위치"
        }
    }
}