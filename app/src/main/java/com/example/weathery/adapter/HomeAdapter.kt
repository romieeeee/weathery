package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weathery.database.WeatherEntity
import com.example.weathery.fragments.WeatherFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "HomeAdapter"

/**
 * ViewPager2를 위한 어댑터
 * - 각 도시별로 new fragment 생성
 * - API에서 받아온 날씨 데이터를 각 fragment로 전달
 */
class HomeAdapter(
    fragmentActivity: FragmentActivity,
    private var weatherDataList: MutableList<WeatherEntity> = mutableListOf(), // 날씨 데이터 리스트
    private var cityNames: List<String> // 도시 이름 리스트
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    // fragment 생성하면서 data 전달
    override fun createFragment(position: Int): Fragment {
        Log.d(TAG, "createFragment :: called [$position]")

        val weatherData = weatherDataList[position]
        val cityName = cityNames[position] // 도시 이름을 리스트에서 가져옴

        // weatherFragment에 newInstance로 데이터 던지기
        return WeatherFragment.newInstance(
            cityName, // 도시명 전달
            getCurrentDate(), // 현재 날짜 전달
            weatherData.temperature ?: "온도 없음",
            weatherData.weatherCondition ?: "날씨 없음",
            weatherData.rainfall ?: "강수 없음",
            weatherData.windSpeed ?: "풍속 없음",
            weatherData.humidity ?: "습도 없음"
        )
    }

    // 어댑터 데이터 갱신 메소드
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<WeatherEntity>, newCityNames: List<String>) {
        Log.d(TAG, "updateData :: called")

        this.weatherDataList = newDataList.toMutableList() // 새 데이터로 업데이트
        this.cityNames = newCityNames // 도시 이름 리스트도 업데이트
        notifyItemRangeChanged(0, weatherDataList.size) // 변경된 데이터만 갱신
    }

    // 현재 날짜를 반환하는 헬퍼 메소드
    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}