package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.model.WeeklyWeather
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * 시간별 날씨 데이터를 RecyclerView에 표시
 */
class WeeklyAdapter(
    private var weeklyWeatherList: List<WeeklyWeather>
) : RecyclerView.Adapter<WeeklyAdapter.WeeklyViewHolder>() {

    class WeeklyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.weekly_date)
        val weatherTextView: TextView = view.findViewById(R.id.weekly_weather)
        val weatherIcon: ImageView = view.findViewById(R.id.weekly_icon)
    }

    // View를 생성하고 초기화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weekly_weather, parent, false)
        return WeeklyViewHolder(view)
    }

    // 적절한 데이터를 가져와서 뷰 홀더의 레이아웃을 채운다
    override fun onBindViewHolder(holder: WeeklyViewHolder, position: Int) {
        val weather = weeklyWeatherList[position]
        holder.dateTextView.text = getFormattedDate(weather.date)
        holder.weatherTextView.text = weather.skyCondition
        holder.weatherIcon.setImageResource(weather.iconRes)
    }

    override fun getItemCount(): Int = weeklyWeatherList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<WeeklyWeather>) {
        weeklyWeatherList = newList
        notifyDataSetChanged()
    }

    fun getFormattedDate(time: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)
        inputFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        val date = inputFormat.parse(time) ?: return time // 예외 방지

        val outputFormat = SimpleDateFormat("MM/dd", Locale.KOREAN)
        outputFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        return outputFormat.format(date)
    }

}
