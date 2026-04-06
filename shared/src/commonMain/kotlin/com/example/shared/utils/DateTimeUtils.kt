package com.example.shared.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateTimeUtils {

  @OptIn(ExperimentalTime::class)
  fun nowIsoMinute(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = now.hour.toString().padStart(2, '0')
    val minute = now.minute.toString().padStart(2, '0')
    return "${now.date}T$hour:$minute"
  }

  @OptIn(ExperimentalTime::class)
  fun todayDateKey(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.of("America/Costa_Rica"))
    return now.date.toString() // YYYY-MM-DD
  }
}
