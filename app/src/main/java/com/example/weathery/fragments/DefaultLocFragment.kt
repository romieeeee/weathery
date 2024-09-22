package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weathery.R
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.network.RetrofitClient
import com.example.weathery.network.WeatherApi
import com.example.weathery.utils.ApiKey
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
        val baseDate = arguments?.getString("baseDate") ?: "20240922"
        val baseTime = arguments?.getString("baseTime") ?: "0600"
        val nx = arguments?.getInt("nx") ?: 55
        val ny = arguments?.getInt("ny") ?: 127

        // 날씨 데이터 요청하기
        fetchWeatherData(baseDate, baseTime, nx, ny)
    }

    @SuppressLint("SetTextI18n")
    private fun fetchWeatherData(baseDate: String, baseTime: String, nx: Int, ny: Int) {
        val apiKey = ApiKey.API_KEY // API Key 설정
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // API 요청 로그
                Log.d("API", "Requesting weather data: baseDate=$baseDate, baseTime=$baseTime, nx=$nx, ny=$ny")

                val response = RetrofitClient.getInstance().create(WeatherApi::class.java)
                    .getWeatherData(apiKey, 1, 1000, "JSON", baseDate, baseTime, nx, ny)

                Log.d("API", "Response received: ${response.toString()}")

                // UI 업데이트는 Main thread에서 처리
                withContext(Dispatchers.Main) {
                    if (response.response.header.resultCode == "00") {
                        val processor = WeatherDataProcessor(response)

                        // 데이터 처리 및 UI 업데이트
                        weatherInfoTextView.text = processor.getWeatherDescription() ?: "Unknown weather"
                        rainChanceTextView.text = "Chance of rain: ${processor.getRainChance()}%"
                        temperatureDetailsTextView.text = "Temp: ${processor.getCurrentTemperature()}° • Max: ${processor.getMaxTemperature()}° • Min: ${processor.getMinTemperature()}°"
                    } else {
                        // 오류 메시지 처리
                        weatherInfoTextView.text = "Error: ${response.response.header.resultMsg}"
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