package com.example.weathery.data

class MultiWeatherProcessor(private val weatherResponse: WeatherResponse) {

    fun getThreeDayForecast(): List<ThreeDayForecast> {
        val forecastMap = mutableMapOf<String, MutableList<ForecastDetail>>()

        weatherResponse.response.body?.items?.item?.forEach { item ->

            val date = item.fcstDate
            val forecastDetail = ForecastDetail(
                time = item.fcstTime,
                temperature = item.fcstValue.takeIf { item.category == "TMP" },
                sky = item.fcstValue.takeIf { item.category == "SKY" },
                rainfall = item.fcstValue.takeIf { item.category == "POP" },
                humidity = item.fcstValue.takeIf { item.category == "REH" },
                windSpeed = item.fcstValue.takeIf { item.category == "WSD" }
            )
            forecastMap.getOrPut(date) { mutableListOf() }.add(forecastDetail)
        }

        return forecastMap.map { (date, details) ->
            ThreeDayForecast(date = date, forecastDetails = details)
        }
    }
}

data class ThreeDayForecast(
    val date: String,
    val forecastDetails: List<ForecastDetail>
)

data class ForecastDetail(
    val time: String,
    val temperature: String?,
    val sky: String?,
    val rainfall: String?,
    val humidity: String?,
    val windSpeed: String?,
)


