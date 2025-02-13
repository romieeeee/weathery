package com.example.weathery.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathery.data.local.CityEntity
import com.example.weathery.data.repository.WeatherRepository
import com.example.weathery.model.WeatherUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val TAG = "HomeViewModel"

/**
 * 날씨 데이터와 관련된 모든 로직을 관리
 * - Room DB와 통신
 * - Repository에 날씨 데이터 요청
 * -> 결과를 LiveData로 View에 전달!
 */
class HomeViewModel(
    private val weatherRepository: WeatherRepository,
) : ViewModel() {
    // 현재 위치 날씨 데이터
    private val _currentWeather = MutableLiveData<WeatherUiModel?>()
    val currentWeather: LiveData<WeatherUiModel?> get() = _currentWeather

    // Room에서 가져온 도시 리스트
    private val _cityList = MutableLiveData<List<CityEntity>>(emptyList())
    val cityList: LiveData<List<CityEntity>> get() = _cityList

    // 저장된 도시들의 날씨 데이터 리스트
    private val _weatherList = MutableLiveData<List<WeatherUiModel>>(emptyList())
    val weatherList: LiveData<List<WeatherUiModel>> get() = _weatherList

    init {
        loadSavedCities()
    }

    /**
     * 현재 위치 기반 날씨 데이터 가져오기
     */
    fun fetchCurrentLocationWeather(lat: Double, lon: Double) {
        Log.d(TAG, "fetchCurrentLocationWeather:: called")
        viewModelScope.launch(Dispatchers.IO) {
            val response = weatherRepository.fetchWeatherData(lat, lon)
            response?.let {
                _currentWeather.postValue(it.copy()) // 새로운 객체로 변경
                Log.d(TAG, "Updated LiveData: ${_currentWeather.value}")
            } ?: Log.e(TAG, "Failed to fetch current location weather")
        }
    }

    /**
     * Room에서 저장된 도시 목록을 가져오고 해당 도시들의 날씨 데이터도 로드
     */
    private fun loadSavedCities() {
        viewModelScope.launch {
            weatherRepository.getCities()
                .collect { cities ->
                    _cityList.value = cities // UI 스레드에서 안전하게 업데이트
                    fetchWeatherForCities(cities)
                }
        }
    }

    /**
     * Room에 저장된 도시들의 날씨 데이터를 가져와서 업데이트
     */
    private fun fetchWeatherForCities(cities: List<CityEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherList = cities.mapNotNull { city ->
                weatherRepository.fetchWeatherData(city.latitude, city.longitude)
            }
            _weatherList.postValue(weatherList) // 저장된 도시들의 날씨 데이터만 저장
        }
    }

    /**
     * 새로운 도시를 추가하고, 해당 도시의 날씨 데이터도 바로 요청
     */
    fun addCity(cityEntity: CityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.addCity(cityEntity)
            loadSavedCities() // 새로 추가한 후 전체 도시 목록 갱신
        }
    }
}
