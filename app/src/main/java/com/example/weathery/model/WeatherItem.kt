package com.example.weathery.model

import com.google.gson.annotations.SerializedName

data class WeatherItem(
    @SerializedName("baseDate") val baseDate: String, // 발표 일자
    @SerializedName("baseTime") val baseTime: String, // 발표 시각
    @SerializedName("category") val category: String, // 자료 구분 코드
    @SerializedName("nx") val nx: Int, // 예보 지점 X 좌표
    @SerializedName("ny") val ny: Int, // 예보 지점 Y 좌표
    @SerializedName("fcstDate") val fcstDate: String, // 예측 일자
    @SerializedName("fcstTime") val fcstTime: String, // 예측 시간
    @SerializedName("fcstValue") val fcstValue: String // 예보 값
)
