package com.example.weathery.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.adapter.HourlyAdapter
import com.example.weathery.adapter.WeeklyAdapter
import com.example.weathery.model.HourlyWeather
import com.example.weathery.model.WeeklyWeather
import com.example.weathery.utils.WeatherIconMapper.getWeatherIcon
import com.example.weathery.viewmodel.WeatherViewModel

private val TAG = "WeatherFragment"

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

    // hourly recyclerview
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var hourlyAdapter: HourlyAdapter

    // weekly recyclerview
    private lateinit var weeklyRecyclerView: RecyclerView
    private lateinit var weeklyAdapter: WeeklyAdapter

    private lateinit var weatherViewModel: WeatherViewModel

    private var hourlyWeatherList: List<HourlyWeather> = emptyList()
    private var weeklyWeatherList: List<WeeklyWeather> = emptyList()

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
            hourlyWeatherList: List<HourlyWeather>,
            weeklyWeatherList: List<WeeklyWeather>,
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
                putParcelableArrayList("HOURLY_WEATHER_LIST", ArrayList(hourlyWeatherList))
                putParcelableArrayList("WEEKLY_WEATHER_LIST", ArrayList(weeklyWeatherList))
            }
            Log.d(TAG, "newInstance 호출 :: ${fragment.arguments}")
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

        weatherViewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)

        // ViewModel의 데이터 변화 관찰
        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            // 현재 프래그먼트의 위치가 맞을 때만 업데이트
            if (weatherData != null && arguments?.getString("CITY_NAME") == "--") {
                updateWeatherData(
                    weatherData.cityName,
                    weatherData.temperature,
                    weatherData.skyCondition,
                    weatherData.rainfall,
                    weatherData.windSpeed,
                    weatherData.humidity,
                    weatherData.hourlyForecasts,
                    weatherData.weeklyForecasts
                )
            }
        }

        // UI 초기화
        initUI(view)

        // 시간대별 날씨 리스트 받기
        hourlyWeatherList = arguments?.getParcelableArrayList("HOURLY_WEATHER_LIST") ?: emptyList()
        weeklyWeatherList = arguments?.getParcelableArrayList("WEEKLY_WEATHER_LIST") ?: emptyList()

        // recyclerview setup
        hourlyRecyclerView = view.findViewById(R.id.hourly_recycler_view)
        hourlyRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        hourlyAdapter = HourlyAdapter(hourlyWeatherList)
        hourlyRecyclerView.adapter = hourlyAdapter

        // weekly recyclerview setup
        weeklyRecyclerView = view.findViewById(R.id.weekly_recycler_view)
        weeklyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        weeklyAdapter = WeeklyAdapter(weeklyWeatherList)
        weeklyRecyclerView.adapter = weeklyAdapter

        val customDecoration = CustomDecoration(4f, 8f, Color.GRAY)
        weeklyRecyclerView.addItemDecoration(customDecoration)

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
        Log.d(TAG, "setWeatherData 호출 :: arguments = $arguments")

        arguments?.let {
            val cityName = it.getString("CITY_NAME") ?: "알 수 없는 위치"
            Log.d(TAG, "WeatherFragment received city: $cityName")

            tvLocation.text = cityName.replace(" ", "\n")
            tvTodayDate.text = it.getString("DATE") ?: "날짜 없음"
            tvNowTemp.text = it.getString("TEMPERATURE") ?: "정보 없음"
            tvNowWeather.text = it.getString("SKY_CONDITION") ?: "정보 없음"
            tvRainfall.text = "${it.getString("RAINFALL") ?: "정보 없음"}%"
            tvWind.text = "${it.getString("WIND_SPEED") ?: "정보 없음"}m/s"
            tvHumidity.text = "${it.getString("HUMIDITY") ?: "정보 없음"}%"

            ivWeather.setImageResource(getWeatherIcon(it.getString("SKY_CONDITION").toString()))

            // hourly, weekly 데이터 업데이트
            val newHourlyList =
                it.getParcelableArrayList<HourlyWeather>("HOURLY_WEATHER_LIST") ?: emptyList()
            val newWeeklyList =
                it.getParcelableArrayList<WeeklyWeather>("WEEKLY_WEATHER_LIST") ?: emptyList()

            // 어댑터 데이터 업데이트
            hourlyAdapter.updateData(newHourlyList)
            weeklyAdapter.updateData(newWeeklyList)
        }
    }

    private fun updateWeatherData(
        cityName: String?,
        temperature: String?,
        skyCondition: String?,
        rainfall: String?,
        windSpeed: String?,
        humidity: String?,
        hourlyWeatherList: List<HourlyWeather>,
        weeklyWeatherList: List<WeeklyWeather>
    ) {
        Log.d(TAG, "updateWeatherData 호출 :: arguments = $arguments")

        arguments?.apply {
            putString("CITY_NAME", cityName)
            putString("TEMPERATURE", temperature)
            putString("SKY_CONDITION", skyCondition)
            putString("RAINFALL", rainfall)
            putString("WIND_SPEED", windSpeed)
            putString("HUMIDITY", humidity)
            putParcelableArrayList("HOURLY_WEATHER_LIST", ArrayList(hourlyWeatherList))
            putParcelableArrayList("WEEKLY_WEATHER_LIST", ArrayList(weeklyWeatherList))
        }
        setWeatherData()  // UI 갱신
    }
}