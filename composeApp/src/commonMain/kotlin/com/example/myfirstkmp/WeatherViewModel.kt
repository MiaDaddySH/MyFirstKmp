package com.example.myfirstkmp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherApi = WeatherApi()

    var cityName by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var weatherData by mutableStateOf<WeatherResponse?>(null)

    fun getWeather() {
        if (cityName.isBlank()) {
            errorMessage = "请输入城市名称"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            weatherApi.getWeather(cityName).fold(
                onSuccess = { response ->
                    weatherData = response
                    isLoading = false
                },
                onFailure = { error ->
                    errorMessage = "获取天气信息失败: ${error.message}"
                    isLoading = false
                }
            )
        }
    }
    override fun onCleared() {
        super.onCleared()
        weatherApi.close()
    }
}