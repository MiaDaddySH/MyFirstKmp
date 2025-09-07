package com.example.myfirstkmp.models

import kotlinx.serialization.Serializable

@Serializable
data class OpenWeatherResponse(
    val name: String = "",
    val weather: List<WeatherDesc> = emptyList(),
    val main: Main = Main(),
    val wind: Wind = Wind(),
    val dt: Long = 0
)

@Serializable
data class WeatherDesc(
    val description: String = "",
    val icon: String = ""
)

@Serializable
data class Main(
    val temp: Double = 0.0,
    val feels_like: Double = 0.0,
    val pressure: Int = 0,
    val humidity: Int = 0
)

@Serializable
data class Wind(
    val speed: Double = 0.0,
    val deg: Int = 0
)