package com.example.weathery.utils

import com.example.weathery.R
import com.example.weathery.model.HourlyWeather
import com.example.weathery.model.WeatherResponse
import com.example.weathery.model.WeeklyWeather

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

    // 아이콘 매핑
    private val iconResourceMap = mapOf(
        "1" to R.drawable.ic_sunny,
        "3" to R.drawable.ic_cloudy,
        "4" to R.drawable.ic_cloudy
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
            val skyCode = categoryMap["SKY"]
            val weather = skyConditionMap[skyCode] ?: "정보 없음"
            val iconRes = iconResourceMap[skyCode] ?: R.drawable.ic_unknown

            weatherList.add(HourlyWeather(time, weather, iconRes))
        }

        return weatherList.sortedBy { it.time }
    }

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
            val skyCode = categoryMap["SKY"]
            val weather = skyConditionMap[skyCode] ?: "정보 없음"
            val iconRes = iconResourceMap[skyCode] ?: R.drawable.ic_unknown

            weatherList.add(WeeklyWeather(date, weather, iconRes))
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

    /**
     * 현재 온도(TMP)를 반환
     */
    fun getCurrentTemperature(): String = getWeatherDataByCategory("TMP")

    /**
     * 강수 형태(PTY)를 반환
     */
    fun getPrecipitationType(): String {
        return precipitationTypeMap[getWeatherDataByCategory("PTY")] ?: "알 수 없음"
    }

    /**
     * 습도(REH)를 반환
     */
    fun getHumidity(): String = getWeatherDataByCategory("REH")

    /**
     * 1시간 강수확률(POP)을 반환
     */
    fun getRainfall(): String = getWeatherDataByCategory("POP")

    /**
     * 하늘 상태(SKY)를 반환
     */
    fun getSkyCondition(): String {
        return skyConditionMap[getWeatherDataByCategory("SKY")] ?: "알 수 없음"
    }

    /**
     * 동서바람성분(UUU)을 반환
     */
    fun getEastWestWindComponent(): String = getWeatherDataByCategory("UUU")

    /**
     * 남북바람성분(VVV)을 반환
     */
    fun getNorthSouthWindComponent(): String = getWeatherDataByCategory("VVV")

    /**
     * 풍속(WSD)을 반환
     */
    fun getWindSpeed(): String = getWeatherDataByCategory("WSD")

    /**
     * 풍향(VEC)을 반환
     */
    fun getWindDirection(): String = getWeatherDataByCategory("VEC")

    /**
     * 낙뢰(LGT)를 반환
     */
    fun getLightning(): String = getWeatherDataByCategory("LGT")
}
