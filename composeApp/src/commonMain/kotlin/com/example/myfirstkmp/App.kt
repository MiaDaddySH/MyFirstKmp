@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myfirstkmp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon

@Composable
fun App() {
    ProvideViewModelOwnerIfAbsent {
        MaterialTheme {
            val factory = rememberWeatherViewModelFactory()
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<WeatherViewModel>(factory = factory)

            var selectedTab by remember { mutableStateOf(0) }

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(if (selectedTab == 0) "天气" else "关于我们") }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                // 切回“天气搜索”页面
                                viewModel.currentScreen = WeatherViewModel.Screen.Search
                            },
                            // 这里 icon 是必填，直接用文字占位，避免额外依赖图标库
                            icon = { Icon(Icons.Outlined.Cloud, contentDescription = "天气") },
                            label = { Text("天气") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Outlined.Info, contentDescription = "关于我们")},
                            label = { Text("关于我们") }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (selectedTab == 0) {
                        when (val screen = viewModel.currentScreen) {
                            is WeatherViewModel.Screen.Search -> WeatherScreen(viewModel)
                            is WeatherViewModel.Screen.ForecastList -> ForecastListScreen(
                                city = viewModel.cityName,
                                forecasts = viewModel.forecastData,
                                isLoading = viewModel.isLoading,
                                errorMessage = viewModel.errorMessage,
                                onBack = { viewModel.currentScreen = WeatherViewModel.Screen.Search },
                                onDayClick = { viewModel.openForecastDetail(it) }
                            )
                            is WeatherViewModel.Screen.ForecastDetail -> ForecastDetailScreen(
                                city = viewModel.cityName,
                                day = screen.day,
                                onBack = { viewModel.navigateBack() }
                            )
                        }
                    } else {
                        AboutScreen()
                    }
                }
            }
        }
    }
}

// 在文件中新增：封装缺省 ViewModelStoreOwner 的适配
@Composable
private fun ProvideViewModelOwnerIfAbsent(content: @Composable () -> Unit) {
    val parentOwner = androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner.current
    if (parentOwner != null) {
        content()
        return
    }
    val viewModelStore = remember { androidx.lifecycle.ViewModelStore() }
    DisposableEffect(Unit) { onDispose { viewModelStore.clear() } }
    val fallbackOwner = remember {
        object : androidx.lifecycle.ViewModelStoreOwner {
            override val viewModelStore: androidx.lifecycle.ViewModelStore = viewModelStore
        }
    }
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner provides fallbackOwner
    ) {
        content()
    }
}
    
// 在文件中新增：提供 WeatherViewModel 的 Factory（保持原逻辑）
@Composable
fun rememberWeatherViewModelFactory(): androidx.lifecycle.ViewModelProvider.Factory {
    return remember {
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
}

// 在文件中新增：将页面 UI 提取到独立的 Composable
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
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
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.loadForecast() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("查看最近7天预报")
            }

            // iOS 专属按钮：仅在 iOS 下显示
            if (getPlatform().name.startsWith("iOS")) {
                Button(
                    onClick = { /* iOS-only action */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("仅 iOS 功能")
                }
                Spacer(modifier = Modifier.height(8.dp))
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
            viewModel.weatherData?.let { weather ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    getWeatherInfoList(weather).forEach { (label, value) ->
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

