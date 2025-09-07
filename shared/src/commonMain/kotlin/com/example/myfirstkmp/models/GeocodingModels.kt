package com.example.myfirstkmp.models

import kotlinx.serialization.Serializable

@Serializable
data class DirectGeocodingItem(
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val country: String? = null,
    val state: String? = null
)