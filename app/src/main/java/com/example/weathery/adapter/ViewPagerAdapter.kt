package com.example.weathery.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.fragments.WeatherFragment

/**
 * ViewPager2를 위한 어댑터
 * - 각 도시별로 new fragment 생성
 * - API에서 받아온 날씨 데이터를 각 fragment로 전달
 */
class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var weatherDataList: MutableList<WeatherDataProcessor> = mutableListOf(), // MutableList로 변경
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    // fragment 생성하면서 data 전달
    override fun createFragment(position: Int): Fragment {
        val weatherData = weatherDataList[position]
        return WeatherFragment.newInstance(
            weatherData.getCityName(),
            weatherData.getDate(),
            weatherData.getCurrentTemperature(),
            weatherData.getSkyCondition(),
            weatherData.getRainfall(),
            weatherData.getWindSpeed(),
            weatherData.getHumidity()
        )
    }

    // 어댑터 데이터 갱신
    fun updateData(
        newDataList: List<WeatherDataProcessor>,
        newCityNames: List<String>,
        newDates: List<String>
    ) {
        this.weatherDataList = newDataList
        notifyItemRangeChanged(0, weatherDataList.size) // 변경된 데이터만 갱신
    }
}