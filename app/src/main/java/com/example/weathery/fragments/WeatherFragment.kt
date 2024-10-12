package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.weathery.R

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

    companion object {
        // newInstance를 사용해 데이터를 전달받음
        fun newInstance(
            cityName: String?,
            date: String?,
            temperature: String?,
            skyCondition: String?,
            rainfall: String?,
            windSpeed: String?,
            humidity: String?
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        initUI(view)

        // set text
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
    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherData() {
        arguments?.let {
            tvLocation.text = it.getString("CITY_NAME") ?: "알 수 없는 위치"
            tvTodayDate.text = it.getString("DATE") ?: "날짜 없음"
            tvNowTemp.text = it.getString("TEMPERATURE") ?: "정보 없음"
            tvNowWeather.text = it.getString("SKY_CONDITION") ?: "정보 없음"
            tvRainfall.text = "${it.getString("RAINFALL") ?: "정보 없음"}%"
            tvWind.text = "${it.getString("WIND_SPEED") ?: "정보 없음"} m/s"
            tvHumidity.text = "${it.getString("HUMIDITY") ?: "정보 없음"}%"
        }
    }
}
