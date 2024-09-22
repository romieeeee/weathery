package com.example.weathery.data

import com.example.weathery.data.WeatherResponse

/**
 * 날씨 데이터를 처리하고 필요한 정보를 추출하는 클래스
 * - category를 기준으로 원하는 데이터를 추출하여 반환한다.
 */
class WeatherDataProcessor(private val weatherResponse: WeatherResponse) {

    /**
     * 주어진 카테고리의 데이터를 찾아 반환하는 함수
     * @param category: 추출하려는 데이터의 카테고리 (ex: "T1H", "SKY", "PTY" 등)
     * @return 추출된 데이터의 관측값 (obsrValue) 또는 null
     */
    private fun getWeatherDataByCategory(category: String): String? {
        // WeatherItem 중 카테고리가 일치하는 데이터를 찾아 obsrValue 반환
        return weatherResponse.response.body?.items?.item?.find {
            it.category == category
        }?.obsrValue
    }

    /**
     * 현재 온도(T1H)를 반환하는 함수
     */
    fun getCurrentTemperature(): String? = getWeatherDataByCategory("T1H")

    /**
     * 강수 확률(POP)을 반환하는 함수
     */
    fun getRainChance(): String? = getWeatherDataByCategory("POP")

    /**
     * 하늘 상태(SKY)를 반환하는 함수
     */
    fun getSkyCondition(): String? = getWeatherDataByCategory("SKY")

    /**
     * 최고 온도(TMX)를 반환하는 함수
     */
    fun getMaxTemperature(): String? = getWeatherDataByCategory("TMX")

    /**
     * 최저 온도(TMN)를 반환하는 함수
     */
    fun getMinTemperature(): String? = getWeatherDataByCategory("TMN")

    /**
     * 한국어 날씨 정보(WfKor)를 반환하는 함수
     */
    fun getWeatherDescription(): String? = getWeatherDataByCategory("WfKor")

}
