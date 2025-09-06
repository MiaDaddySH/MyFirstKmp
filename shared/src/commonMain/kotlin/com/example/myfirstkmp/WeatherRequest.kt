package com.example.myfirstkmp

import kotlinx.serialization.Serializable

@Serializable
data class WeatherRequest(
    val city: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)