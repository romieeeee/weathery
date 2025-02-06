package com.example.weathery.model

import android.os.Parcel
import android.os.Parcelable
import com.example.weathery.R

data class WeeklyWeather(
    val date: String, // 예: "20250204"
    val weather: String,
    val iconRes: Int = R.drawable.ic_unknown // 예: R.drawable.ic_sunny
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

    companion object CREATOR : Parcelable.Creator<WeeklyWeather> {
        override fun createFromParcel(parcel: Parcel): WeeklyWeather {
            return WeeklyWeather(parcel)
        }

        override fun newArray(size: Int): Array<WeeklyWeather?> {
            return arrayOfNulls(size)
        }
    }
}

