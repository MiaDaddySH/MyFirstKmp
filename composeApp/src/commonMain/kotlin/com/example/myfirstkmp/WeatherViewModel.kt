package com.example.myfirstkmp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.myfirstkmp.models.DailyForecast

class WeatherViewModel : ViewModel() {
    private val weatherApi = WeatherApi()

    var cityName by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var weatherData by mutableStateOf<WeatherResponse?>(null)
    var forecastData by mutableStateOf<List<com.example.myfirstkmp.models.DailyForecast>>(emptyList())

     sealed class Screen {
        data object Search : Screen()
        data object ForecastList : Screen()
        data class ForecastDetail(val day: DailyForecast) : Screen()
    }

    var currentScreen by mutableStateOf<Screen>(Screen.Search)

    fun loadForecast() {
        if (cityName.isBlank()) {
            errorMessage = "请输入城市名称"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            weatherApi.getDailyForecast7Days(cityName).fold(
                onSuccess = { list ->
                    forecastData = list
                    isLoading = false
                    currentScreen = Screen.ForecastList
                },
                onFailure = { e ->
                    errorMessage = "获取预报失败: ${e.message}"
                    isLoading = false
                }
            )
        }
    }

    fun openForecastDetail(day: DailyForecast) {
        currentScreen = Screen.ForecastDetail(day)
    }

    fun navigateBack() {
        currentScreen = Screen.ForecastList
    }
    
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