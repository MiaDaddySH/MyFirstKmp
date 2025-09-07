package com.example.myfirstkmp.models

import kotlinx.serialization.Serializable

@Serializable
data class ForecastDailyResponse(
    val city: ForecastCity = ForecastCity(),
    val list: List<ForecastDailyItem> = emptyList()
)

@Serializable
data class ForecastCity(
    val name: String = "",
    val country: String = ""
)

@Serializable
data class ForecastDailyItem(
    val dt: Long,
    val temp: ForecastTemp,
    val pressure: Int,
    val humidity: Int,
    val weather: List<WeatherDesc> = emptyList(),
    val speed: Double = 0.0,
    val deg: Int = 0
)

@Serializable
data class ForecastTemp(
    val day: Double,
    val min: Double? = null,
    val max: Double? = null,
    val night: Double? = null,
    val eve: Double? = null,
    val morn: Double? = null
)