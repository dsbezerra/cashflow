package com.github.dsbezerra.cashflow.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

actual fun formatLongDate(timestamp: Long, pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime =
        instant.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()
    return formatter.format(localDateTime)
}