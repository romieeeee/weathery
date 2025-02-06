package com.example.weathery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathery.data.local.CityEntity
import com.example.weathery.data.repository.WeatherRepository
import com.example.weathery.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * UI와 Repository 간의 데이터 로직을 처리
 * - Room DB와 통신
 * - Repository에 날씨 데이터 요청
 * -> 결과를 LiveData로 View에 전달!
 */
class HomeViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    // 날씨 데이터 변수
    private val _weatherData = MutableLiveData<List<WeatherResponse>>()
    val weatherData: LiveData<List<WeatherResponse>> get() = _weatherData

    // 도시 목록 변수
    private val _cityList = MutableLiveData<List<CityEntity>>()
    val cityList: LiveData<List<CityEntity>> get() = _cityList

    init{
        loadSavedCities()
    }

    // Room DB에서 저장된 도시 목록 가져오기
    private fun loadSavedCities() {
        viewModelScope.launch {
            weatherRepository.getCities().collect { cities ->
                _cityList.postValue(cities)
            }
        }
    }

    // 특정 좌표의 날씨 데이터 가져오기
    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = weatherRepository.fetchWeatherData(lat, lon)
            response?.let {
                val updatedList = (_weatherData.value ?: listOf()) + it // 리스트로 유지
                _weatherData.postValue(updatedList)
            }
        }
    }

    // 여러 개의 도시 날씨 한꺼번에 가져오기
    fun fetchWeatherForCities(cities: List<CityEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherList = cities.mapNotNull { city ->
                weatherRepository.fetchWeatherData(city.latitude, city.longitude)
            }
            _weatherData.postValue(weatherList)
        }
    }

    // 새로운 도시 추가
    fun addCity(cityEntity: CityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.addCity(cityEntity)
            loadSavedCities() // 업데이트
        }
    }
}