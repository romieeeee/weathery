package com.example.weathery.model

import android.os.Parcel
import android.os.Parcelable
import com.example.weathery.R
import com.example.weathery.utils.WeatherIconMapper.getWeatherIcon

data class HourlyWeather(
    val time: String,  // 예: "0600"
    val skyCondition: String,
    val iconRes: Int = getWeatherIcon(skyCondition)
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<HourlyWeather> {
        override fun createFromParcel(parcel: Parcel): HourlyWeather {
            return HourlyWeather(parcel)
        }

        override fun newArray(size: Int): Array<HourlyWeather?> {
            return arrayOfNulls(size)
        }
    }
}
