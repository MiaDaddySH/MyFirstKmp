package com.example.myfirstkmp.services

import com.example.myfirstkmp.OPENWEATHER_API_BASE_URL
import com.example.myfirstkmp.OPENWEATHER_API_KEY
import com.example.myfirstkmp.models.OpenWeatherResponse
import com.example.myfirstkmp.models.WeatherResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.Closeable

class WeatherService : Closeable {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun getWeather(latitude: Double, longitude: Double, city: String): WeatherResponse {
        val url = "$OPENWEATHER_API_BASE_URL/weather?lat=$latitude&lon=$longitude&appid=$OPENWEATHER_API_KEY&units=metric"

        val response: OpenWeatherResponse = client.get(url).body()

        return WeatherResponse(
            city = city.ifEmpty { response.name },
            temperature = response.main.temp,
            feelsLike = response.main.feels_like,
            description = response.weather.firstOrNull()?.description ?: "",
            icon = response.weather.firstOrNull()?.icon ?: "",
            humidity = response.main.humidity,
            pressure = response.main.pressure,
            windSpeed = response.wind.speed,
            windDirection = response.wind.deg,
            timestamp = response.dt
        )
    }

    override fun close() {
        client.close()
    }
}