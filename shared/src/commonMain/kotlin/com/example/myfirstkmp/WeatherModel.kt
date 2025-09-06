package com.example.myfirstkmp

import kotlinx.serialization.Serializable

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