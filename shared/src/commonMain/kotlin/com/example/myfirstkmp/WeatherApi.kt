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
            val tz = forecastResp.city.timezone // 秒
            val dayBuckets = forecastResp.list.groupBy { item ->
                ((item.dt + tz) / 86_400L) // 按本地天分组
            }

            val daily = dayBuckets.entries
                .sortedBy { it.key }
                .map { (_, entries) ->
                    val avgTemp = entries.map { it.main.temp }.average()
                    val avgFeels = entries.map { it.main.feels_like }.average()
                    val avgHumidity = entries.map { it.main.humidity }.average()
                    val avgPressure = entries.map { it.main.pressure }.average()
                    val avgWindSpeed = entries.map { it.wind.speed }.average()
                    val avgWindDeg = entries.map { it.wind.deg }.average()

                    val allWeathers = entries.flatMap { it.weather }
                    val topWeather = allWeathers
                        .groupBy { it.icon to it.description }
                        .maxByOrNull { it.value.size }?.key

                    val dateEpochSec = entries.minOf { it.dt } // 该天最早的UTC时间点

                    com.example.myfirstkmp.models.DailyForecast(
                        date = dateEpochSec,
                        temperature = avgTemp,
                        feelsLike = avgFeels,
                        description = topWeather?.second ?: (allWeathers.firstOrNull()?.description ?: ""),
                        icon = topWeather?.first ?: (allWeathers.firstOrNull()?.icon ?: ""),
                        humidity = avgHumidity.toInt(),
                        pressure = avgPressure.toInt(),
                        windSpeed = avgWindSpeed,
                        windDirection = avgWindDeg.toInt()
                    )
                }
                .take(7) // 上限7天（/forecast 实际约5天）
            daily
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