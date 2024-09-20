package com.example.weathery.models

import com.google.gson.annotations.SerializedName

/**
 * 응답 DTO: Api 응답을 처리하기 위한 데이터 클래스
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
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultMsg") val resultMsg: String
)

// 응답의 body 부분, nullable 처리된 body (null 가능성을 대비)
data class Body(
    @SerializedName("dataType") val dataType: String,
    @SerializedName("items") val items: Items
)

// items 객체 내부의 리스트
data class Items(
    @SerializedName("item") val item: List<WeatherItem>
)

// 실제 날씨 데이터를 담는 WeatherItem
data class WeatherItem(
    @SerializedName("baseDate") val baseDate: String,
    @SerializedName("baseTime") val baseTime: String,
    @SerializedName("category") val category: String,
    @SerializedName("nx") val nx: Int,
    @SerializedName("ny") val ny: Int,
    @SerializedName("obsrValue") val obsrValue: String
)
