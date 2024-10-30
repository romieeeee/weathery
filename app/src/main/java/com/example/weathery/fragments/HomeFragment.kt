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
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.utils.LocationManager
import com.example.weathery.utils.WeatherManager
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

    private lateinit var locationManager: LocationManager
    private lateinit var weatherManager: WeatherManager

    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }
    private val cityDao by lazy { db.cityDao() } // CityDao 초기화

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationManager = LocationManager(requireContext())
        weatherManager = WeatherManager(cityDao)

        viewPager = view.findViewById(R.id.viewPager)
        dotsIndicator = view.findViewById(R.id.dots_indicator)

        // 빈 데이터로 초기화하고, 데이터를 동적으로 로드
        adapter = HomeAdapter(requireActivity(), mutableListOf(), listOf())
        viewPager.adapter = adapter

        dotsIndicator.attachTo(viewPager)

        // 현재 위치 가져오기
        getNowWeather()

        // 데이터 로드하여 ViewPager 업데이트
        loadWeatherData()
    }

    // 현재 위치와 날씨 데이터 가져오기
    private fun getNowWeather() {
        Log.d(TAG, "setDefault :: called")
        locationManager.getLastKnownLocation(
            onSuccess = { location ->
                location?.let { getWeather(it.latitude, it.longitude) }
            },
            onFailure = { Log.e(TAG, "위치 가져오기 실패") }
        )
    }

    // api 응답값을 토대로 현재 지역의 날씨를 list에 추가?????
    fun loadWeatherData() {
        Log.d(TAG, "loadWeatherData :: called")
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val cities = cityDao.getAllCities()
            val weatherDataList = mutableListOf<WeatherDataProcessor>()
            val cityNames = mutableListOf<String>()

            for (city in cities) {
                // API를 통해 날씨 데이터 가져오기
                val weatherData = weatherManager.fetchWeatherData(city.latitude, city.longitude)
                if (weatherData != null) {
                    // 여기에서 WeatherDataProcessor 객체를 생성하는 로직 추가 필요
                    val processedData = WeatherDataProcessor(weatherData) // 예시로 처리
                    weatherDataList.add(processedData)
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



    /**
     * 현재 위치의 날씨 정보를 가져오고 도시 정보를 DB에 저장하는 메서드
     */
    private fun getWeather(latitude: Double, longitude: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            val cityName = locationManager.getCityNameFromCoord(latitude, longitude)
            weatherManager.saveCity(cityName, latitude, longitude)

            val weatherData = weatherManager.fetchWeatherData(latitude, longitude)
            weatherData?.let { data ->
                withContext(Dispatchers.Main) {

                    // 날씨 정보 화면에 표시
                }
            }
            Log.d(TAG, "getWeather :: cityName = $cityName, weatherData = ($weatherData)")
        }
    }
}
