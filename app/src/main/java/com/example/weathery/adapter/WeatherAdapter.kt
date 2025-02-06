package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weathery.model.WeatherResponse
import com.example.weathery.utils.WeatherDataProcessor
import com.example.weathery.view.WeatherFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "HomeAdapter"

/**
 * ViewPager2를 위한 어댑터
 * - 각 도시별로 new fragment 생성
 * - API에서 받아온 날씨 데이터를 각 fragment로 전달
 */
class WeatherAdapter(
    fragmentActivity: FragmentActivity,
    private var weatherDataList: MutableList<WeatherResponse> = mutableListOf(),
    private var cityNames: List<String>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    // fragment 생성하면서 data 전달
    override fun createFragment(position: Int): Fragment {
        Log.d(TAG, "createFragment :: called [$position]")

        val weatherResponse = weatherDataList[position]
        val cityName = cityNames[position]

        val weatherData = WeatherDataProcessor(weatherResponse)
        val hourlyWeatherList = weatherData.getHourlyWeatherList() // 시간대별 날씨 리스트 가져오기


        // weatherFragment에 newInstance로 데이터 던지기
        return WeatherFragment.newInstance(
            cityName,
            getCurrentDate(),
            weatherData.getCurrentTemperature(),
            weatherData.getSkyCondition(),
            weatherData.getRainfall(),
            weatherData.getWindSpeed(),
            weatherData.getHumidity(),
            weatherData.getPrecipitationType(),
            hourlyWeatherList
        )
    }

    // 어댑터 데이터 갱신 메소드
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<WeatherResponse>, newCityNames: List<String>) {
        Log.d(TAG, "updateData :: called")

        this.weatherDataList = newDataList.toMutableList()
        this.cityNames = newCityNames
        notifyItemRangeChanged(0, weatherDataList.size)
    }

    // 현재 날짜를 반환하는 헬퍼 메소드
    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}