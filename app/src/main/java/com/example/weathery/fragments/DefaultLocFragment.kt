package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.weathery.R
import com.example.weathery.network.RetrofitClient
import com.example.weathery.network.WeatherApi
import com.example.weathery.utils.ApiKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultLocFragment : Fragment() {

    private lateinit var weatherInfoTextView: TextView
    private lateinit var rainChanceTextView: TextView // 강수 확률 TextView
    private lateinit var temperatureDetailsTextView: TextView // 온도 세부 사항 TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_loc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        weatherInfoTextView = view.findViewById(R.id.tv_weather_condition)
        rainChanceTextView = view.findViewById(R.id.tv_rain_chance)
        temperatureDetailsTextView = view.findViewById(R.id.tv_temperature_details)

        // 클릭 시 detail fragment로 이동
        view.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
        }

        // 기본값 set
        val baseDate = arguments?.getString("baseDate") ?: "20240920"
        val baseTime = arguments?.getString("baseTime") ?: "0600"
        val nx = arguments?.getInt("nx") ?: 55
        val ny = arguments?.getInt("ny") ?: 127

        // 날씨 데이터 요청하기
        fetchWeatherData(baseDate, baseTime, nx, ny)
    }

    @SuppressLint("SetTextI18n")
    private fun fetchWeatherData(baseDate: String, baseTime: String, nx: Int, ny: Int) {
        val apiKey = ApiKey.API_KEY
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 요청 파라미터 확인
                Log.d("API", "API Request - baseDate: $baseDate, baseTime: $baseTime, nx: $nx, ny: $ny")

                // API 호출
                val response = RetrofitClient.getInstance().create(WeatherApi::class.java)
                    .getWeatherData(apiKey, 1, 1000, "JSON", baseDate, baseTime, nx, ny)

                // API 응답 출력
                Log.d("API", "API Raw Response: ${response.toString()}")

                withContext(Dispatchers.Main) {
                    if (response.response.header.resultCode == "00") {
                        // null-safe 연산자 사용하여 body가 null이 아닌 경우에만 접근
                        val weatherItems = response.response.body?.items?.item
                        if (!weatherItems.isNullOrEmpty()) {
                            // 첫 번째 WeatherItem에 접근하여 데이터 설정
                            val weatherItem = weatherItems[0]
                            weatherInfoTextView.text = weatherItem.obsrValue // 날씨 정보 표시

                            // 강수 확률 및 온도 세부 사항 추가
                            rainChanceTextView.text = "Chance of rain ${weatherItems[1].obsrValue}%" // 강수 확률
                            temperatureDetailsTextView.text = "${weatherItems[2].obsrValue}° F • ${weatherItems[3].obsrValue} • ${weatherItems[4].obsrValue} • ${weatherItems[5].obsrValue} mp/h" // 온도 세부 사항
                        } else {
                            weatherInfoTextView.text = "No weather data available"
                            Log.e("API", "Weather data is null.")
                        }
                    } else {
                        weatherInfoTextView.text = response.response.header.resultMsg
                        Log.e("API", "Error: ${response.response.header.resultMsg}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "API call failed: ${e.message}")
            }
        }
    }

    companion object {
        fun newInstance(baseDate: String, baseTime: String, nx: Int, ny: Int): DefaultLocFragment {
            val fragment = DefaultLocFragment()
            val args = Bundle().apply {
                putString("baseDate", baseDate)
                putString("baseTime", baseTime)
                putInt("nx", nx)
                putInt("ny", ny)
            }
            fragment.arguments = args
            return fragment
        }
    }
}