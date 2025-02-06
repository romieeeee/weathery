package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.utils.WeatherDataProcessor

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

    }

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.city_list_name)
        val tempTextView: TextView = itemView.findViewById(R.id.tv_city_temp)
    }

    // 어댑터 데이터 갱신
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<WeatherDataProcessor>, newCityNames: List<String>) {
        this.weatherDataList = newDataList.toMutableList()
        this.cityNames = newCityNames
        notifyDataSetChanged() // 데이터가 변경되었음을 알림
    }
}
