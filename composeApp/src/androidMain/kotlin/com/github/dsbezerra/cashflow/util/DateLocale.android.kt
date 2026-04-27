package com.github.dsbezerra.cashflow.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

actual fun formatLongDate(timestamp: Long, pattern: String): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    return formatter.format(calendar.time)
}