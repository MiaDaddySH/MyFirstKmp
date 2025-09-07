package com.example.myfirstkmp.models

import kotlinx.serialization.Serializable

// 多日天气预报响应模型
@Serializable
data class ForecastResponse(
    val city: City,
    val list: List<ForecastItem>
)

@Serializable
data class City(
    val name: String,
    val country: String,
    val timezone: Int
)

@Serializable
data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<WeatherDesc>,
    val wind: Wind,
    val dt_txt: String
)

// 每日天气预报详情
@Serializable
data class DailyForecast(
    val date: Long,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDirection: Int
)