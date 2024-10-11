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
    private val weatherDataList: List<WeatherDataProcessor> = emptyList() // 기본값으로 빈 리스트 설정
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return weatherDataList.size // 도시의 개수만큼 페이지 생성
    }

    // fragment 생성하면서 data 전달
    override fun createFragment(position: Int): Fragment {
        val weatherData = weatherDataList[position]
        return WeatherFragment.newInstance(
            weatherData.getCurrentTemperature(),
            weatherData.getSkyCondition(),
            weatherData.getRainfall(),
            weatherData.getWindSpeed(),
            weatherData.getHumidity()
        )
    }
}