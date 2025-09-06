package com.example.myfirstkmp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
fun App() {
    // 若当前环境（如 iOS）没有 ViewModelStoreOwner，则提供一个
    val parentOwner = androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner.current
    val viewModelStore = remember { androidx.lifecycle.ViewModelStore() }
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose { viewModelStore.clear() }
    }
    val fallbackOwner = remember {
        object : androidx.lifecycle.ViewModelStoreOwner {
            override val viewModelStore: androidx.lifecycle.ViewModelStore = viewModelStore
        }
    }

        // 自定义 Factory：在非 Android 环境默认工厂未实现时，显式提供
    val factory = remember {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(
                modelClass: kotlin.reflect.KClass<T>,
                extras: androidx.lifecycle.viewmodel.CreationExtras
            ): T {
                if (modelClass == WeatherViewModel::class) {
                    @Suppress("UNCHECKED_CAST")
                    return WeatherViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
            }
        }
    }


    val content: @Composable () -> Unit = {
        MaterialTheme {
            // 使用自定义 Factory 获取 ViewModel，避免默认工厂在 iOS 上未实现的问题
                        val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<WeatherViewModel>(factory = factory)
            val weatherData = viewModel.weatherData
            val isLoading = viewModel.isLoading
            val errorMessage = viewModel.errorMessage

            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "天气查询",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = viewModel.cityName,
                    onValueChange = { viewModel.cityName = it },
                    label = { Text("输入城市名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.getWeather() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("确认")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                weatherData?.let { weather ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(getWeatherInfoList(weather)) { (label, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label)
                                Text(text = value)
                            }
                            Divider()
                        }
                    }
                }
            }
        }
        }
    }

    if (parentOwner == null) {
        // iOS 等无默认 owner 的场景，提供一个
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner provides fallbackOwner
        ) {
            content()
        }
    } else {
        content()
    }
}

private fun getWeatherInfoList(weather: WeatherResponse): List<Pair<String, String>> {
    return listOf(
        "城市" to weather.city,
        "温度" to "${weather.temperature}°C",
        "体感温度" to "${weather.feelsLike}°C",
        "天气描述" to weather.description,
        "湿度" to "${weather.humidity}%",
        "气压" to "${weather.pressure} hPa",
        "风速" to "${weather.windSpeed} m/s",
        "风向" to "${weather.windDirection}°"
    )
}