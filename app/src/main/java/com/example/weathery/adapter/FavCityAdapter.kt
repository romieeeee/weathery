package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.model.FavoriteCity

class FavCityAdapter(
    private var cityList: MutableList<FavoriteCity>
) : RecyclerView.Adapter<FavCityAdapter.CityListViewHolder>() {

    class CityListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityNameTextView: TextView = view.findViewById(R.id.city_list_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fav_city, parent, false)
        return CityListViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityListViewHolder, position: Int) {
        val city = cityList[position]
        holder.cityNameTextView.text = city.cityName
    }

    override fun getItemCount(): Int = cityList.size

    // 리스트 갱신 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<FavoriteCity>) {
        cityList.clear()
        cityList.addAll(newList)

        notifyDataSetChanged()
    }
}
