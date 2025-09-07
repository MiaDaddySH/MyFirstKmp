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
        try {
            val resp: com.example.myfirstkmp.models.ForecastDailyResponse =
                httpClient.get("$OPENWEATHER_API_BASE_URL/forecast/daily") {
                    parameter("q", city)
                    parameter("cnt", 7)
//                    parameter("units", "metric")
                    parameter("appid", OPENWEATHER_API_KEY)
                }.body()

            val daily = resp.list.map { item ->
                com.example.myfirstkmp.models.DailyForecast(
                    date = item.dt,
                    temperature = item.temp.day,
                    feelsLike = item.temp.day,
                    description = item.weather.firstOrNull()?.description ?: "",
                    icon = item.weather.firstOrNull()?.icon ?: "",
                    humidity = item.humidity,
                    pressure = item.pressure,
                    windSpeed = item.speed,
                    windDirection = item.deg
                )
            }

            if (daily.isEmpty()) {
                return@withContext Result.failure(IllegalStateException("未获取到7日预报数据"))
            }
            Result.success(daily)
        } catch (t: Throwable) {
            Result.failure(IllegalStateException("获取7日预报失败: ${t.message}", t))
        }
    }

    fun close() {
        httpClient.close()
    }
}