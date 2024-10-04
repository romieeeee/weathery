package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weathery.MainActivity
import com.example.weathery.R
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.network.RetrofitClient
import com.example.weathery.network.WeatherApi
import com.example.weathery.utils.ApiKey
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DefaultLocFragment : Fragment() {

    private lateinit var weatherInfoTextView: TextView // 기본 날씨
    private lateinit var rainChanceTextView: TextView // 강수 확률
    private lateinit var temperatureDetailsTextView: TextView // 온도 세부 사항

    private val locationManager = object : LocationListener {
        override fun onLocationChanged(loc: Location) {
            val latitude = loc.latitude // 위도
            val longitude = loc.longitude // 경도
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃을 인플레이트하고 View를 반환
        return inflater.inflate(R.layout.fragment_default_loc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TextView 초기화
        weatherInfoTextView = view.findViewById(R.id.tv_weather_condition)
        rainChanceTextView = view.findViewById(R.id.tv_rain_chance)
        temperatureDetailsTextView = view.findViewById(R.id.tv_temperature_details)

        // 화면을 클릭하면 상세 프래그먼트로 이동
        view.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
        }

        getLocationData()

        // 기본 값 설정
        val baseDate = arguments?.getString("baseDate") ?: getFormattedDate()
        val baseTime = arguments?.getString("baseTime") ?: "0600"
        val nx = arguments?.getInt("nx") ?: 55
        val ny = arguments?.getInt("ny") ?: 127

        // 날씨 데이터를 가져와서 화면에 표시하는 함수 호출
        fetchWeatherData(baseDate, baseTime, nx, ny)
    }

    /**
     * 위치 정보를 받아오는 함수
     *
     */
    private fun getLocationData(){
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        // longitude latitude 반환

    }

    /**
     * 날씨 데이터를 가져와서 UI에 표시하는 함수
     * baseDate, baseTime, nx, ny에 맞춰 API 호출 후 데이터를 가져옴
     */
    @SuppressLint("SetTextI18n")
    private fun fetchWeatherData(baseDate: String, baseTime: String, nx: Int, ny: Int) {
        val apiKey = ApiKey.API_KEY // API Key 설정
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("API", "Request: baseDate=$baseDate, baseTime=$baseTime, nx=$nx, ny=$ny")

                // 날씨 API 호출 (Retrofit 사용)
                val response = RetrofitClient.getInstance().create(WeatherApi::class.java)
                    .getWeatherData(apiKey, 1, 1000, "JSON", baseDate, baseTime, nx, ny)

                Log.d("API", "Response: ${response.toString()}")

                // UI 업데이트는 메인 스레드에서 처리
                withContext(Dispatchers.Main) {
                    if (response.response.header.resultCode == "00") {
                        // 응답을 처리할 WeatherDataProcessor 객체 생성
                        val processor = WeatherDataProcessor(response)

                        // UI에 날씨 정보를 표시 (weatherInfoTextView)
                        weatherInfoTextView.text = processor.getSkyCondition() ?: "날씨 정보 없음"

                        // UI에 강수 확률을 표시 (rainChanceTextView)
                        rainChanceTextView.text = "강수 확률: ${processor.getRainfall()}%"

                        // UI에 온도 관련 세부 정보를 표시 (temperatureDetailsTextView)
                        temperatureDetailsTextView.text = "기온: ${processor.getCurrentTemperature()}° • 습도: ${processor.getHumidity()}% • 풍속: ${processor.getWindSpeed()} m/s"
                    } else {
                        // 오류가 발생한 경우의 처리
                        weatherInfoTextView.text = "Error: ${response.response.header.resultMsg}"
                        Log.e("API", "Error: ${response.response.header.resultMsg}")
                    }
                }
            } catch (e: Exception) {
                // 예외 발생 시 로그 출력
                Log.e("API", "API Call Failed: ${e.message}")
            }
        }
    }

    private fun getFormattedDate() : String {
        // 현재 시간 가져오기
        val currentTime = LocalDateTime.now()

        // 포맷팅 (yyyymmdd 형식)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formattedTime = currentTime.format(formatter)

        return formattedTime
    }

    /**
     * Fragment 인스턴스를 생성하고 인자를 설정하는 함수
     * 주어진 baseDate, baseTime, nx, ny 값을 기반으로 새 Fragment 생성
     */
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
