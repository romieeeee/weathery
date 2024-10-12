package com.example.weathery.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _locationText = MutableLiveData<String>()
    val locationText: LiveData<String> = _locationText

    // 위치 주소 update
    fun updateLocationText(location: String) {
        _locationText.value = location
    }
}