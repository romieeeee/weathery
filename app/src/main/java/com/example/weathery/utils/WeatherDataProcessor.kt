package com.example.weathery.utils

import com.example.weathery.model.HourlyWeather
import com.example.weathery.model.WeatherResponse
import com.example.weathery.model.WeatherUiModel
import com.example.weathery.model.WeeklyWeather

/**
 * API 응답이 응답 코드 형태로 와서
 * UI에 데이터 던지기 전에 가공하는 클래스
 * 응답 코드 -> 문자열
 */
class WeatherDataProcessor(private val weatherResponse: WeatherResponse) {

    // SKY 코드 매핑
    private val skyConditionMap = mapOf(
        "1" to "맑음",
        "3" to "구름 많음",
        "4" to "흐림"
    )

    // PTY(강수 형태) 코드 매핑
    private val precipitationTypeMap = mapOf(
        "0" to "없음",
        "1" to "비",
        "2" to "비/눈",
        "3" to "눈",
        "4" to "소나기"
    )

    /**
     * 시간별 예보 데이터를 그룹화하여 반환
     */
    fun getHourlyWeatherList(): List<HourlyWeather> {
        val hourlyDataMap = mutableMapOf<String, MutableMap<String, String>>()
        val weatherList = mutableListOf<HourlyWeather>()

        weatherResponse.response.body?.items?.item?.forEach { item ->
            val time = item.fcstTime
            val category = item.category
            val value = item.fcstValue

            hourlyDataMap.getOrPut(time) { mutableMapOf() }[category] = value
        }

        hourlyDataMap.forEach { (time, categoryMap) ->
            val skyCondition = getSkyCondition(categoryMap)

            weatherList.add(HourlyWeather(time, skyCondition))
        }

        return weatherList.sortedBy { it.time }
    }

    /**
     * 주간별 예보 데이터를 그룹화하여 반환
     */
    fun getWeeklyWeatherList(): List<WeeklyWeather> {
        val weeklyDataMap = mutableMapOf<String, MutableMap<String, String>>()
        val weatherList = mutableListOf<WeeklyWeather>()

        weatherResponse.response.body?.items?.item?.forEach { item ->
            val date = item.fcstDate
            val category = item.category
            val value = item.fcstValue

            weeklyDataMap.getOrPut(date) { mutableMapOf() }[category] = value
        }

        weeklyDataMap.forEach { (date, categoryMap) ->
            val skyCondition = getSkyCondition(categoryMap)

            weatherList.add(WeeklyWeather(date, skyCondition))
        }

        return weatherList.sortedBy { it.date }
    }

    /**
     * 주어진 카테고리 데이터를 반환하는 함수
     */
    private fun getWeatherDataByCategory(category: String): String {
        return weatherResponse.response.body?.items?.item
            ?.find { it.category == category }
            ?.fcstValue ?: "데이터 없음"
    }

    fun getSkyCondition(): String {
        val latestData = weatherResponse.response.body?.items?.item
            ?.groupBy { it.fcstTime } // 시간별로 그룹화
            ?.minByOrNull { it.key }?.value // 가장 가까운 시간 데이터 가져오기

        val categoryMap = latestData?.associate { it.category to it.fcstValue } ?: emptyMap()

        return getSkyCondition(categoryMap)
    }

    /**
     * SKY와 PTY 값을 고려해서 날씨 상태 반환
     */
    private fun getSkyCondition(categoryMap: Map<String, String>): String {
        val skyCode = categoryMap["SKY"]
        val ptyCode = categoryMap["PTY"]

        return if (ptyCode == "0" || ptyCode.isNullOrEmpty()) {
            skyConditionMap[skyCode] ?: "알 수 없음"
        } else {
            precipitationTypeMap[ptyCode] ?: "알 수 없음"
        }
    }

    /**
     * 현재 온도(TMP)를 반환
     */
    fun getCurrentTemperature(): String = getWeatherDataByCategory("TMP")

    /**
     * 습도(REH)를 반환
     */
    fun getHumidity(): String = getWeatherDataByCategory("REH")

    /**
     * 1시간 강수확률(POP)을 반환
     */
    fun getRainfall(): String = getWeatherDataByCategory("POP")

    /**
     * 풍속(WSD)을 반환
     */
    fun getWindSpeed(): String = getWeatherDataByCategory("WSD")

    /**
     * UI 모델로 변환하는 함수 추가
     */
    fun toUiModel(): WeatherUiModel {
        return WeatherUiModel(
            temperature = getCurrentTemperature(),
            skyCondition = getSkyCondition(),
            humidity = getHumidity(),
            rainfall = getRainfall(),
            windSpeed = getWindSpeed(),
            hourlyForecasts = getHourlyWeatherList(),
            weeklyForecasts = getWeeklyWeatherList()
        )
    }
}
