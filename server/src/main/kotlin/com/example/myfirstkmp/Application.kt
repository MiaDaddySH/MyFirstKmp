package com.example.myfirstkmp

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import com.example.myfirstkmp.models.WeatherRequest
import com.example.myfirstkmp.services.WeatherService

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 安装内容协商插件
    install(ContentNegotiation) {
        json()
    }

    // 创建天气服务实例
    val weatherService = WeatherService()

    routing {
        get("/") {
            call.respondText("Ktor")
        }

        // 添加天气API路由
        route("/api") {
            // 获取天气信息
            post("/weather") {
                val request = call.receive<WeatherRequest>()
                val city = request.city ?: "Unknown Location"
                val weatherData = weatherService.getWeather(request.latitude, request.longitude, city)
                call.respond(weatherData)
            }

            // 简单的健康检查端点
            get("/health") {
                call.respondText("Weather API is running!")
            }
        }
    }
}