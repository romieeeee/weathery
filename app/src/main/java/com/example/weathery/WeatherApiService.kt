package com.example.weathery

import com.example.weathery.ApiKey.Companion.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 날씨 API
 * API 요청의 엔드포인트와 HTTP 메서드를 정의하는 인터페이스
 */
interface WeatherApiService {
    @GET("getUltraSrtNcst")
    fun getWeatherData(
        @Query("serviceKey") serviceKey: String = API_KEY,  // ApiKey에서 가져온 serviceKey 사용
        @Query("pageNo") pageNo: String,
        @Query("numOfRows") numOfRows: String,
        @Query("dataType") dataType: String = "JSON",
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: String,
        @Query("ny") ny: String
    ): Call<WeatherResponse>
}
