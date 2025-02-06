package com.example.weathery.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.adapter.HourlyAdapter
import com.example.weathery.model.HourlyWeather

/**
 * 각 도시의 날씨 정보를 받아서 UI에 표시
 */
class WeatherFragment : Fragment() {
    private lateinit var tvLocation: TextView
    private lateinit var tvTodayDate: TextView
    private lateinit var tvNowTemp: TextView
    private lateinit var tvNowWeather: TextView
    private lateinit var tvRainfall: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var ivWeather: ImageView

    private lateinit var hourlyWeatherList: List<HourlyWeather>

    // recyclerview
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HourlyAdapter

    companion object {
        // newInstance를 사용해 데이터를 전달받음
        fun newInstance(
            cityName: String?,
            date: String?,
            temperature: String?,
            skyCondition: String?,
            rainfall: String?,
            windSpeed: String?,
            humidity: String?,
            precipitation_type: String?,
            hourlyWeatherList: List<HourlyWeather>
        ): WeatherFragment {
            val fragment = WeatherFragment()
            val args = Bundle().apply {
                putString("CITY_NAME", cityName)
                putString("DATE", date)
                putString("TEMPERATURE", temperature)
                putString("SKY_CONDITION", skyCondition)
                putString("RAINFALL", rainfall)
                putString("WIND_SPEED", windSpeed)
                putString("HUMIDITY", humidity)
                putString("PRECIPITATION_TYPE", precipitation_type)
                putParcelableArrayList("HOURLY_WEATHER_LIST", ArrayList(hourlyWeatherList)) // 시간대별 날씨 추가
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 초기화
        initUI(view)

        // 시간대별 날씨 리스트 받기
        hourlyWeatherList = arguments?.getParcelableArrayList("HOURLY_WEATHER_LIST") ?: listOf()

        // recyclerview setup
        recyclerView = view.findViewById(R.id.hourly_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        adapter = HourlyAdapter(hourlyWeatherList)
        recyclerView.adapter = adapter

        Log.d("WeatherFragment", hourlyWeatherList.toString())

        // 날씨 데이터 설정
        setWeatherData()
    }

    private fun initUI(view: View) {
        tvLocation = view.findViewById(R.id.location)
        tvTodayDate = view.findViewById(R.id.today_date)
        tvNowTemp = view.findViewById(R.id.now_temperature)
        tvNowWeather = view.findViewById(R.id.now_weather)
        tvRainfall = view.findViewById(R.id.rainfall)
        tvWind = view.findViewById(R.id.wind)
        tvHumidity = view.findViewById(R.id.humidity)
        ivWeather = view.findViewById(R.id.iv_weather)
    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherData() {
        arguments?.let {
            tvLocation.text = it.getString("CITY_NAME")?.replace(" ", "\n") ?: "알 수 없는 위치"
            tvTodayDate.text = it.getString("DATE") ?: "날짜 없음"
            tvNowTemp.text = it.getString("TEMPERATURE") ?: "정보 없음"
            tvNowWeather.text = it.getString("SKY_CONDITION") ?: "정보 없음"
            tvRainfall.text = "${it.getString("RAINFALL") ?: "정보 없음"}%"
            tvWind.text = "${it.getString("WIND_SPEED") ?: "정보 없음"}m/s"
            tvHumidity.text = "${it.getString("HUMIDITY") ?: "정보 없음"}%"

            // 날씨 아이콘 설정
            val skyCondition = it.getString("SKY_CONDITION")
            val precipitationType = it.getString("PRECIPITATION_TYPE")

            ivWeather.setImageResource(getWeatherIcon(precipitationType, skyCondition))
        }
    }

    private fun getWeatherIcon(precipitationType: String?, skyCondition: String?): Int {
        if (precipitationType == "없음") { // 강수가 없을 경우
            return when (skyCondition) {
                "맑음" -> R.drawable.ic_sunny
                "구름 많음", "흐림" -> R.drawable.ic_cloudy
                else -> R.drawable.ic_unknown
            }
        } else { // 강수가 있을 경우
            return when (precipitationType) {
                "비" -> R.drawable.ic_rainy
                "비/눈" -> R.drawable.ic_rainysnow
                "눈" -> R.drawable.ic_snow
                else -> R.drawable.ic_unknown
            }
        }
    }
}