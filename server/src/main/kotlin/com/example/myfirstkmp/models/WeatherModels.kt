package com.example.myfirstkmp.models

import kotlinx.serialization.Serializable

// 客户端请求模型
@Serializable
data class WeatherRequest(
    val latitude: Double,
    val longitude: Double,
    val city: String? = null
)

// 客户端响应模型
@Serializable
data class WeatherResponse(
    val city: String,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val timestamp: Long
)

// OpenWeatherMap API响应模型
@Serializable
data class OpenWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)

@Serializable
data class Coord(
    val lon: Double,
    val lat: Double
)

@Serializable
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int? = null,
    val grnd_level: Int? = null
)

@Serializable
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

@Serializable
data class Clouds(
    val all: Int
)

@Serializable
data class Sys(
    val type: Int? = null,
    val id: Int? = null,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)