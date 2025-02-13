package com.example.weathery.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.databinding.ItemFavCityBinding
import com.example.weathery.model.FavoriteCity

class FavCityAdapter(
    private val cityList: MutableList<FavoriteCity> = mutableListOf()  // 기본값 설정
) : RecyclerView.Adapter<FavCityAdapter.CityListViewHolder>() {

    // 클릭 리스너 인터페이스 (필요한 경우)
    interface OnItemClickListener {
        fun onItemClick(city: FavoriteCity)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    class CityListViewHolder(
        private val binding: ItemFavCityBinding  // ViewBinding 사용 추천
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: FavoriteCity) {
            binding.cityListName.text = city.cityName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityListViewHolder {
        val binding = ItemFavCityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CityListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityListViewHolder, position: Int) {
        try {
            val city = cityList[position]
            holder.bind(city)

            // 아이템 클릭 리스너 설정
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(city)
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("FavCityAdapter", "Invalid position: $position", e)
        }
    }

    override fun getItemCount(): Int = cityList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<FavoriteCity>) {
        cityList.clear()
        cityList.addAll(newList)
        notifyDataSetChanged()
    }

    // 단일 아이템 추가 메서드
    fun addCity(city: FavoriteCity) {
        cityList.add(city)
        notifyItemInserted(cityList.size - 1)
    }

    // 단일 아이템 제거 메서드
    fun removeCity(position: Int) {
        if (position in 0 until cityList.size) {
            cityList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
