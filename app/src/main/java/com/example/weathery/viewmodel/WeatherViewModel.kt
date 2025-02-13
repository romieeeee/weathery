package com.example.weathery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weathery.model.WeatherUiModel

class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherUiModel>()
    val weatherData: LiveData<WeatherUiModel> get() = _weatherData

    fun updateWeatherData(newData: WeatherUiModel) {
        _weatherData.value = newData
    }
}
