package com.example.myfirstkmp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfirstkmp.models.DailyForecast

@Composable
fun ForecastListScreen(
    city: String,
    forecasts: List<DailyForecast>,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onDayClick: (DailyForecast) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$city 最近7天预报", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "返回",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onBack() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            forecasts.forEach { day ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDayClick(day) }
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "时间戳: ${day.date}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "温度: ${day.temperature}°C，体感: ${day.feelsLike}°C")
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "天气: ${day.description}")
                }
                Divider()
            }
        }
    }
}