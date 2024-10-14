package com.example.weathery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.database.City

class CityAdapter(
    private val mList: MutableList<City>
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityAdapter.CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list, parent, false)

        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityAdapter.CityViewHolder, position: Int) {
        val cityData = mList[position]
        holder.nameTextView.text = cityData.cityName
//        holder.weatherImageView.setImageResource(cityData.cityWeatherIcon) // 아이콘 설정
    }

    override fun getItemCount(): Int {
        return mList.count()
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_city_name)
        val tempTextView: TextView = itemView.findViewById(R.id.tv_city_temp)
//        val weatherImageView: ImageView = itemView.findViewById(R.id.iv_city_weather)
    }
}
