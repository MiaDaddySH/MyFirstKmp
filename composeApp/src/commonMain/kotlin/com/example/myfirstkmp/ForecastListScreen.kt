package com.example.myfirstkmp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import com.example.myfirstkmp.models.DailyForecast
import kotlinx.datetime.toLocalDateTime

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
                .statusBarsPadding()
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

            // 用 LazyColumn 替换原先的 forEach 渲染，列表可滚动
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                items(forecasts) { day ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDayClick(day) }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = formatDate(day.date))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "温度: ${day.temperature.to2dpString()}°C，体感: ${day.feelsLike.to2dpString()}°C")
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "天气: ${day.description}")
                    }
                    Divider()
                }
            }
        }
    }
}

fun formatDate(epochSecondsUtc: Long): String {
    val dt = kotlinx.datetime.Instant.fromEpochSeconds(epochSecondsUtc)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    val y = dt.date.year
    val m = dt.date.monthNumber.toString().padStart(2, '0')
    val d = dt.date.dayOfMonth.toString().padStart(2, '0')
    val hh = dt.time.hour.toString().padStart(2, '0')
    val mm = dt.time.minute.toString().padStart(2, '0')
    // 使用 ordinal 映射，避免 when 的 else 警告
    val weekNames = arrayOf("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")
    val week = weekNames.getOrElse(dt.date.dayOfWeek.ordinal) { "未知" }
    return "$y-$m-$d $hh:$mm $week"
}

private fun Double.to2dpString(): String {
    val rounded = kotlin.math.round(this * 100.0) / 100.0
    val s = rounded.toString()
    val i = s.indexOf('.')
    return if (i < 0) s + ".00"
    else {
        val decimals = s.length - i - 1
        when (decimals) {
            0 -> s + "00"
            1 -> s + "0"
            else -> s.substring(0, i + 1 + 2)
        }
    }
}