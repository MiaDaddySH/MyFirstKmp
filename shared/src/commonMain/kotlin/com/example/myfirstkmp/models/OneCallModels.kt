package com.example.myfirstkmp.models

import kotlinx.serialization.Serializable

@Serializable
data class OneCallResponse(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val timezone: String = "",
    val timezone_offset: Int = 0,
    val daily: List<OneCallDaily> = emptyList()
)

@Serializable
data class OneCallDaily(
    val dt: Long,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val temp: OneCallTemp,
    val feels_like: OneCallFeelsLike,
    val pressure: Int,
    val humidity: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDesc> = emptyList()
)

@Serializable
data class OneCallTemp(
    val day: Double,
    val min: Double? = null,
    val max: Double? = null,
    val eve: Double? = null,
    val morn: Double? = null,
    val night: Double? = null
)

@Serializable
data class OneCallFeelsLike(
    val day: Double,
    val eve: Double? = null,
    val morn: Double? = null,
    val night: Double? = null
)
