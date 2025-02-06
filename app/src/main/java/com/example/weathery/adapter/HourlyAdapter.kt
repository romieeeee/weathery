package com.example.weathery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.model.HourlyWeather
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * 시간별 날씨 데이터를 RecyclerView에 표시
 */
class HourlyAdapter(
    private val weatherList: List<HourlyWeather> // 시간별 날씨 데이터 리스트
) : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    // ViewHolder: 각 아이템의 View 참조
    class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeTextView: TextView = view.findViewById(R.id.hourly_time)
        val weatherTextView: TextView = view.findViewById(R.id.hourly_weather)
        val weatherIcon: ImageView = view.findViewById(R.id.hourly_icon)
    }

    // View를 생성하고 초기화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_weather, parent, false)
        return HourlyViewHolder(view)
    }

    // 적절한 데이터를 가져와서 뷰 홀더의 레이아웃을 채운다
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val weather = weatherList[position]
        holder.timeTextView.text = getFormattedTime(weather.time)
        holder.weatherTextView.text = weather.weather
        holder.weatherIcon.setImageResource(weather.iconRes)
    }

    override fun getItemCount(): Int = weatherList.size

    fun getFormattedTime(time: String): String {
        // 입력값을 Date 객체로 변환
        val inputFormat = SimpleDateFormat("HHmm", Locale.KOREAN)
        inputFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        val date = inputFormat.parse(time) ?: return time // 예외 방지

        // 변환된 Date 객체를 원하는 "HH:mm" 형식으로 출력
        val outputFormat = SimpleDateFormat("HH:mm", Locale.KOREAN)
        outputFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        return outputFormat.format(date)
    }

}
