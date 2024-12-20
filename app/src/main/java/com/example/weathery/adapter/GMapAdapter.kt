package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.data.WeatherDataProcessor

class GMapAdapter(
    private var weatherDataList: MutableList<WeatherDataProcessor> = mutableListOf(), // WeatherEntity 리스트
    private var cityNames: List<String> // 도시 이름 리스트
) : RecyclerView.Adapter<GMapAdapter.CityViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GMapAdapter.CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list, parent, false)
        return CityViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GMapAdapter.CityViewHolder, position: Int) {

        val weatherData = weatherDataList[position]
        val cityName = cityNames[position] // 도시 이름을 리스트에서 가져옴

        // 도시 이름 설정
        holder.nameTextView.text = cityName

        // 날씨 정보가 없으면 기본 메시지 설정
        if (weatherData.getCurrentTemperature().isEmpty()) {
            holder.tempTextView.text = "날씨 정보를 불러올 수 없습니다."
//            holder.weatherImageView.setImageResource(R.drawable.default_weather_icon) // 기본 아이콘
        }

        // 날씨 데이터가 있는 경우에만 날씨 정보를 표시
        holder.tempTextView.text = "현재 기온: ${weatherData.getCurrentTemperature() ?: "온도 없음"}℃"

        // 날씨 아이콘 설정 - 아이콘 리소스는 필요에 따라 설정
//        holder.weatherImageView.setImageResource(getWeatherIcon(weatherData.getSkyCondition() ?: "default"))


    }

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_city_name)
        val tempTextView: TextView = itemView.findViewById(R.id.tv_city_temp)
        val weatherImageView: ImageView = itemView.findViewById(R.id.iv_city_weather)
    }

    // 날씨 아이콘을 리턴하는 메서드 (예시)
//    private fun getWeatherIcon(iconName: String): Int {
//        return when (iconName) {
//            "clear" -> R.drawable.ic_clear // 예시 아이콘 리소스
//            "cloudy" -> R.drawable.ic_cloudy
//            "rain" -> R.drawable.ic_rain
//            // 필요한 아이콘을 추가
//            else -> R.drawable.ic_default // 기본 아이콘
//        }
//    }

    // 어댑터 데이터 갱신
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<WeatherDataProcessor>, newCityNames: List<String>) {
        this.weatherDataList = newDataList.toMutableList()
        this.cityNames = newCityNames
        notifyDataSetChanged() // 데이터가 변경되었음을 알림
    }
}
