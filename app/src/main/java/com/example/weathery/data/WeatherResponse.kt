package com.example.weathery.data

import com.google.gson.annotations.SerializedName

/**
 * 응답 DTO: API 응답을 처리하기 위한 데이터 클래스
 */

// 최상위 응답 데이터 클래스
data class WeatherResponse(
    @SerializedName("response") val response: ResponseBody
)

// 응답에서 response 필드의 구조
data class ResponseBody(
    @SerializedName("header") val header: Header,
    @SerializedName("body") val body: Body?
)

// 응답의 header 부분
data class Header(
    @SerializedName("resultCode") val resultCode: String, // 결과코드
    @SerializedName("resultMsg") val resultMsg: String, // 결과메시지
    @SerializedName("numOfRows") val numOfRows: Int, // 한 페이지 결과 수
    @SerializedName("pageNo") val pageNo: Int, // 페이지 번호
    @SerializedName("totalCount") val totalCount: Int // 전체 결과 수
)

// 응답의 body 부분, nullable 처리된 body (null 가능성을 대비)
data class Body(
    @SerializedName("dataType") val dataType: String, // 데이터 타입
    @SerializedName("items") val items: Items // 아이템 리스트
)

// items 객체 내부의 리스트
data class Items(
    @SerializedName("item") val item: List<WeatherItem> // WeatherItem 리스트
)

// 실제 날씨 데이터를 담는 WeatherItem
data class WeatherItem(
    @SerializedName("baseDate") val baseDate: String, // 발표일자
    @SerializedName("baseTime") val baseTime: String, // 발표시각
    @SerializedName("category") val category: String, // 자료구분코드
    @SerializedName("nx") val nx: Int, // 예보지점 X 좌표
    @SerializedName("ny") val ny: Int, // 예보지점 Y 좌표
    @SerializedName("fcstDate") val fcstDate: String, // 예측일자
    @SerializedName("fcstTime") val fcstTime: String, // 예측시간
    @SerializedName("fcstValue") val fcstValue: String // 예보 값
)
