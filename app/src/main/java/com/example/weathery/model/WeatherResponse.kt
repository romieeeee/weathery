package com.example.weathery.model

import com.google.gson.annotations.SerializedName

/**
 * 응답 DTO: API 응답을 처리하기 위한 데이터 클래스
 * ex)
 * {
 *     "response": {
 *         "header": {
 *             "resultCode": "00",
 *             "resultMsg": "NORMAL_SERVICE"
 *         },
 *         "body": {
 *             "dataType": "JSON",
 *             "items": {
 *                 "item": [
 *                     {
 *                         "baseDate": "20250204",
 *                         "baseTime": "0500",
 *                         "category": "TMP",
 *                         "fcstDate": "20250204",
 *                         "fcstTime": "0600",
 *                         "fcstValue": "-13",
 *                         "nx": 55,
 *                         "ny": 127
 *                     }
 *                 }
 *             }
 *         }
 *     }
 * }
 */

// 최상위 응답 데이터 클래스
data class WeatherResponse(
    @SerializedName("response") val response: ResponseBody
)

// 응답에서 response 필드의 구조 (Body는 null 가능)
data class ResponseBody(
    @SerializedName("header") val header: Header,
    @SerializedName("body") val body: Body?
)

// 응답의 header 부분
data class Header(
    @SerializedName("resultCode") val resultCode: String, // 결과코드
    @SerializedName("resultMsg") val resultMsg: String, // 결과메시지
)

// 응답의 body
data class Body(
    @SerializedName("dataType") val dataType: String, // 데이터 타입
    @SerializedName("items") val items: Items // 아이템 리스트
)

// items 객체 내부 리스트
data class Items(
    @SerializedName("item") val item: List<WeatherItem> // WeatherItem 리스트
)
