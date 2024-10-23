package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.database.CityWithWeather

class CityAdapter(
    private val cityWeatherList: List<CityWithWeather> // 도시와 날씨 데이터를 받음
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityAdapter.CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list, parent, false)
        return CityViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CityAdapter.CityViewHolder, position: Int) {

        val cityWithWeather = cityWeatherList[position]
        val city = cityWithWeather.city
        val weather = cityWithWeather.weather

        Log.d("project-weathery", "onBindViewHolder 호출 ${cityWithWeather.weather?.cityId}")

        // 도시 이름 설정
        holder.nameTextView.text = city.cityName

        // 날씨 데이터가 있는 경우에만 날씨 정보를 표시
        if (weather != null) {
            holder.tempTextView.text = "현재 기온: ${weather.temperature}℃"
            // holder.weatherImageView.setImageResource(weather.icon) // 아이콘 설정
            // holder.weatherImageView.setImageResource(getWeatherIcon(cityWeather.weatherEntity.icon))
        } else {
            holder.tempTextView.text = "날씨 정보를 불러올 수 없습니다."
            // holder.weatherImageView.setImageResource(R.drawable.default_weather_icon) // 기본 아이콘
        }
    }

    override fun getItemCount(): Int {
        return cityWeatherList.size
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
}
