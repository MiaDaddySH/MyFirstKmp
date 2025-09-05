package com.example.myfirstkmp

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class WeatherApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }
    
    // 假设服务器运行在本地，使用localhost或10.0.2.2（Android模拟器访问主机的特殊IP）
    private val baseUrl = "http://localhost:$SERVER_PORT"
    
    suspend fun getWeather(city: String): Result<WeatherResponse> = withContext(Dispatchers.Default) {
        try {
            // 创建请求对象
            val request = WeatherRequest(city)
            
            // 发送请求到服务器
            val response = client.post {
                url("$baseUrl/api/weather")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            // 解析响应
            val weatherData = response.body<WeatherResponse>()
            Result.success(weatherData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun close() {
        client.close()
    }
}