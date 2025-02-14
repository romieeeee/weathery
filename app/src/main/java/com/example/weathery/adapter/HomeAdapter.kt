package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weathery.model.WeatherUiModel
import com.example.weathery.view.WeatherFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "HomeAdapter"

/**
 * ViewPager2 어댑터 (날씨 프래그먼트 관리)
 */
class HomeAdapter(
    fragmentActivity: FragmentActivity,
    private var weatherDataList: List<WeatherUiModel> = listOf(),
    private var cityNames: List<String> = listOf(),
    private var currentWeatherData: WeatherUiModel?
) : FragmentStateAdapter(fragmentActivity) {

    // Fragment 캐시를 위한 맵 추가
    private val fragments = mutableMapOf<Int, WeatherFragment>()

    override fun getItemCount(): Int {
        return weatherDataList.size + 1 // 현재 위치 + 저장된 도시
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("WeatherFragment", "currentWeatherData = ${ currentWeatherData }")
        val fragment = if (position == 0) {
            WeatherFragment.newInstance(
                currentWeatherData?.cityName ?: "--",
                getCurrentDate(),
                currentWeatherData?.temperature ?: "--",
                currentWeatherData?.skyCondition ?: "--",
                currentWeatherData?.rainfall ?: "--",
                currentWeatherData?.windSpeed ?: "--",
                currentWeatherData?.humidity ?: "--",
                currentWeatherData?.hourlyForecasts ?: emptyList(),
                currentWeatherData?.weeklyForecasts ?: emptyList()
            )
        } else {
            if (position - 1 >= weatherDataList.size || position - 1 >= cityNames.size) {
                Log.e(TAG, "createFragment :: position out of bounds [$position]")
                return Fragment()
            }

            val weatherData = weatherDataList[position - 1]
            val cityName = cityNames[position - 1]

            WeatherFragment.newInstance(
                cityName,
                getCurrentDate(),
                weatherData.temperature,
                weatherData.skyCondition,
                weatherData.rainfall,
                weatherData.windSpeed,
                weatherData.humidity,
                weatherData.hourlyForecasts,
                weatherData.weeklyForecasts
            )
        }

        // 생성된 프래그먼트를 캐시에 저장
        fragments[position] = fragment as WeatherFragment
        return fragment
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(
        newWeatherDataList: List<WeatherUiModel>,
        newCityNames: List<String>,
        newWeather: WeatherUiModel?
    ) {
        Log.d("WeatherFragment", "updateData :: ${currentWeatherData}")
        weatherDataList = newWeatherDataList
        cityNames = newCityNames
        currentWeatherData = newWeather
        notifyDataSetChanged()
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
