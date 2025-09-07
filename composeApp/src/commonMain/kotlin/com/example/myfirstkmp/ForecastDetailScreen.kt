package com.example.myfirstkmp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.myfirstkmp.models.DailyForecast

@Composable
fun ForecastDetailScreen(
    city: String,
    day: DailyForecast,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$city 预报详情", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "返回",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onBack() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = formatDate(day.date))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "温度: ${day.temperature}°C")
            Text(text = "体感温度: ${day.feelsLike}°C")
            Text(text = "天气: ${day.description}")
            Text(text = "湿度: ${day.humidity}%")
            Text(text = "气压: ${day.pressure} hPa")
            Text(text = "风速: ${day.windSpeed} m/s")
            Text(text = "风向: ${day.windDirection}°")
        }
    }
}