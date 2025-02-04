package com.example.weathery.data

import com.example.weathery.R

/**
 * 날씨 데이터 처리하는 클래스
 * - category를 기준으로 원하는 데이터를 추출하여 반환
 */
class WeatherDataProcessor(private val weatherResponse: WeatherResponse) {

    /**
     * 주어진 카테고리의 데이터를 찾아 반환하는 함수
     * @param category: 추출하려는 데이터의 카테고리 (ex: "T1H", "REH", "PTY" 등)
     * @return 추출된 데이터의 예보 값 (fcstValue) 또는 null
     */
    private fun getWeatherDataByCategory(category: String): String {
        // WeatherItem 중 카테고리가 일치하는 데이터를 찾아 fcstValue 반환
        return weatherResponse.response.body?.items?.item?.find {
            it.category == category
        }?.fcstValue ?: "데이터 없음" // fcstValue가 null인 경우 처리
    }

    /**
     * 현재 온도(TMP)를 반환하는 함수
     */
    fun getCurrentTemperature(): String = getWeatherDataByCategory("TMP")

    /**
     * 강수 형태(PTY)를 반환하는 함수
     * PTY(강수형태)는 코드값으로 나타난다.
     * 0: 없음, 1: 비, 2: 비/눈, 3: 눈, 4: 소나기
     */
    fun getPrecipitationType(): String {
        return when (getWeatherDataByCategory("PTY")) {
            "0" -> "없음"
            "1" -> "비"
            "2" -> "비/눈"
            "3" -> "눈"
            "4" -> "소나기"
            else -> "알 수 없음"
        }
    }

    /**
     * 습도(REH)를 반환하는 함수
     */
    fun getHumidity(): String = getWeatherDataByCategory("REH")

    /**
     * 1시간 강수확률(POP)을 반환하는 함수
     * - 강수 없음으로 응답이 오는 경우 0%로 표시한다.
     */
    fun getRainfall(): String {
        val rainfallData = getWeatherDataByCategory("POP")
        return rainfallData
    }

    /**
     * 하늘 상태(SKY)를 반환하는 함수
     * - SKY 코드에 따라 상태를 명확하게 반환한다.
     */
    fun getSkyCondition(): String {
        // SKY 코드에 따른 상태를 반환
        return when (getWeatherDataByCategory("SKY")) {
            "1" -> "맑음"
            "3" -> "구름 많음"
            "4" -> "흐림"
            else -> "알 수 없음" // 해당 코드가 없을 경우 처리
        }
    }

    /**
     * 동서바람성분(UUU)를 반환하는 함수
     */
    fun getEastWestWindComponent(): String = getWeatherDataByCategory("UUU")

    /**
     * 남북바람성분(VVV)를 반환하는 함수
     */
    fun getNorthSouthWindComponent(): String = getWeatherDataByCategory("VVV")

    /**
     * 풍속(WSD)를 반환하는 함수
     */
    fun getWindSpeed(): String = getWeatherDataByCategory("WSD")

    /**
     * 풍향(VEC)을 반환하는 함수
     */
    fun getWindDirection(): String = getWeatherDataByCategory("VEC")

    /**
     * 낙뢰(LGT)를 반환하는 함수
     */
    fun getLightning(): String = getWeatherDataByCategory("LGT")

    /**
     * 날씨에 따라 아이콘을 반환하는 함수
     */
    private fun getWeatherIcon(precipitationType: String?, skyCondition: String?): Int {
        if (precipitationType == "없음") { // 강수가 없을 경우
            return when (skyCondition) {
                "맑음" -> R.drawable.ic_sunny
                "구름 많음", "흐림" -> R.drawable.ic_cloudy
                else -> R.drawable.ic_unknown // 기본 아이콘 (예: 물음표 아이콘)
            }
        } else { // 강수가 있을 경우
            return when (precipitationType) {
                "비" -> R.drawable.ic_rainy
                "비/눈" -> R.drawable.ic_rainysnow
                "눈" -> R.drawable.ic_snow
                else -> R.drawable.ic_unknown
            }
        }
    }
}
