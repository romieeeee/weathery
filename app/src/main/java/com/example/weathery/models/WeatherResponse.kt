package com.example.weathery.models

import com.google.gson.annotations.SerializedName

/**
 * 응답 DTO
 * open api에서 사용하고자 하는 서비스의 응답 model을 참고하여 만듬
 */
data class WeatherResponse(
    @SerializedName("resultCode") val resultCode: String,  // 결과코드
    @SerializedName("resultMsg") val resultMsg: String,    // 결과메시지
    @SerializedName("numOfRows") val numOfRows: Int,        // 한 페이지 결과 수
    @SerializedName("pageNo") val pageNo: Int,              // 페이지번호
    @SerializedName("totalCount") val totalCount: Int,      // 전체 결과 수
    @SerializedName("dataType") val dataType: String,       // 응답 자료형식 (XML/JSON)
    @SerializedName("baseDate") val baseDate: String,       // 발표일자
    @SerializedName("baseTime") val baseTime: String,       // 발표시각
    @SerializedName("nx") val nx: Int,                      // 예보지점 X 좌표
    @SerializedName("ny") val ny: Int,                      // 예보지점 Y 좌표
    @SerializedName("items") val items: Items               // 실질적인 예보 데이터
)

data class Items(
    @SerializedName("item") val itemList: List<Item>       // 여러 데이터 항목
)

data class Item(
    @SerializedName("category") val category: String,       // 자료구분코드
    @SerializedName("fcstDate") val fcstDate: String,       // 예측 날짜
    @SerializedName("fcstTime") val fcstTime: String,       // 예측 시간
    @SerializedName("fcstValue") val fcstValue: String      // 예보 값
)
