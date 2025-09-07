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
import  io.ktor.client.plugins.logging.*

class WeatherApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
                // 新增：安装日志插件
        Logging {
            level = io.ktor.client.plugins.logging.LogLevel.ALL
            logger = io.ktor.client.plugins.logging.Logger.DEFAULT
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

  suspend fun getDailyForecast7Days(city: String): Result<List<com.example.myfirstkmp.models.DailyForecast>> = withContext(Dispatchers.Default) {
        runCatching {
            // 1) 城市名 -> (lat, lon)
            val (lat, lon) = geocodeCity(city).getOrThrow()

            // 2) 调用 /data/2.5/forecast?lat&lon （5天/3小时）
            val forecastResp: com.example.myfirstkmp.models.ForecastResponse =
                httpClient.get("$OPENWEATHER_API_BASE_URL/forecast") {
                    parameter("lat", lat)
                    parameter("lon", lon)
                    parameter("units", "metric")
                    parameter("appid", OPENWEATHER_API_KEY)
                }.body()

            // 3) 将3小时粒度按“天”聚合为 DailyForecast
            val list3h = forecastResp.list.map { item ->
                val weather = item.weather.firstOrNull()
                com.example.myfirstkmp.models.DailyForecast(
                    date = item.dt,                      // 该 3 小时段的 UTC 秒时间戳
                    temperature = item.main.temp,
                    feelsLike = item.main.feels_like,
                    description = weather?.description ?: "",
                    icon = weather?.icon ?: "",
                    humidity = item.main.humidity,
                    pressure = item.main.pressure,
                    windSpeed = item.wind.speed,
                    windDirection = item.wind.deg
                )
            }
            list3h
        }
    }

    private suspend fun geocodeCity(city: String): Result<Pair<Double, Double>> = withContext(Dispatchers.Default) {
        runCatching {
            val list: List<com.example.myfirstkmp.models.DirectGeocodingItem> =
                httpClient.get("$OPENWEATHER_GEO_BASE_URL/direct") {
                    parameter("q", city)
                    parameter("limit", 1)
                    parameter("appid", OPENWEATHER_API_KEY)
                }.body()

            val first = list.firstOrNull() ?: error("未找到城市：$city")
            first.lat to first.lon
        }
    }
    
    fun close() {
        httpClient.close()
    }
}