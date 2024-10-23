package com.example.weathery.fragments

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
import com.example.weathery.database.CityEntity
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.database.WeatherEntity
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.LocationManager
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
    private lateinit var adapter: ViewPagerAdapter

    private lateinit var locationManager: LocationManager

    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }

    private val cityDao by lazy { db.cityDao() } // CityDao 초기화
    private val weatherDao by lazy { db.weatherDao() } // WeatherDao 초기화
    private val weatherRepository by lazy {
        WeatherRepository(
            cityDao,
            weatherDao
        )
    }

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
        Log.d("", "getCurrentLocationAndUpdateCities() called")
        if (locationManager.checkLocationPermission()) {
            Log.d(TAG, "checkLocationPermission")
            locationManager.getLastKnownLocation(
                onSuccess = { location ->
                    location?.let {
                        val cityName = locationManager.getCityNameFromCoord(it.latitude, it.longitude)
                        val cityEntity = CityEntity(
                            cityName = cityName,
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                        Log.d(TAG, "city info: ${cityEntity.latitude}, ${cityEntity.longitude}, ${cityEntity.cityName}")

                        CoroutineScope(Dispatchers.IO).launch {
                            // 도시 데이터 삽입 전 동일 도시 존재 여부 확인
                            val existingCity = cityDao.getCityByName(cityName)

                            if (existingCity == null) { // 새로운 도시인 경우
                                // 도시 데이터를 삽입하고, 자동 생성된 cityId를 가져옴
                                val cityId = cityDao.insertCity(cityEntity) // 삽입 후 cityId 반환
                                fetchWeatherDataForCity(cityName, it.latitude, it.longitude, cityId )
                            } else{
                                fetchWeatherDataForCity(cityName, it.latitude, it.longitude, existingCity.cityId.toLong())
                            }
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
    private suspend fun fetchWeatherDataForCity(cityName:String, lat: Double, lon: Double, cityId: Long) {
        Log.d(TAG, "fetchWeatherDataForCity() called")
        val weatherData = fetchWeatherForCity(lat, lon)
        weatherData?.let {
            // 날씨 데이터 -> weather_table 에 저장
            val weatherEntity = WeatherEntity(
                cityId = cityId.toInt(),
                temperature = it.getCurrentTemperature(),
                weatherCondition = it.getSkyCondition(),
                rainfall = it.getRainfall(),
                windSpeed = it.getWindSpeed(),
                humidity = it.getHumidity(),
                timestamp = System.currentTimeMillis()
            )
            weatherDao.insertWeather(weatherEntity)
            Log.d(TAG, "${weatherEntity.cityId}, ${weatherEntity.temperature}," +
                    " ${weatherEntity.weatherCondition}, ${weatherEntity.rainfall}, ${weatherEntity.windSpeed}, ${weatherEntity.humidity}, ${weatherEntity.timestamp}")

            // 날씨 데이터를 리스트에 추가
            weatherDataList.add(it)
            cityNames.add(cityName)

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

}