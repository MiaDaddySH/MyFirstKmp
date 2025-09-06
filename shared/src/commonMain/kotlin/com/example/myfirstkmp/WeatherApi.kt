package com.example.myfirstkmp

import com.example.myfirstkmp.models.OpenWeatherResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class WeatherApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }
    
    suspend fun getWeather(city: String): Result<WeatherResponse> = withContext(Dispatchers.Default) {
        try {
            val ow: OpenWeatherResponse = httpClient.get("$OPENWEATHER_API_BASE_URL/weather") {
                parameter("q", city)
                parameter("appid", OPENWEATHER_API_KEY)
                parameter("units", "metric")
            }.body()

            Result.success(
                WeatherResponse(
                    city = ow.name.ifEmpty { city },
                    temperature = ow.main.temp,
                    feelsLike = ow.main.feels_like,
                    description = ow.weather.firstOrNull()?.description.orEmpty(),
                    icon = ow.weather.firstOrNull()?.icon.orEmpty(),
                    humidity = ow.main.humidity,
                    pressure = ow.main.pressure,
                    windSpeed = ow.wind.speed,
                    windDirection = ow.wind.deg,
                    timestamp = ow.dt
                )
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    fun close() {
        httpClient.close()
    }
}