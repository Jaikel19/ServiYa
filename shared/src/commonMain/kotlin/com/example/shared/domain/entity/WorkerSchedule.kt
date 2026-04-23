package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class TimeBlock(
    val start: String = "",
    val end: String = ""
)

@Serializable
data class WorkerSchedule(
    val dayKey: String = "",
    val dayNumber: Int = 0,
    val enabled: Boolean = false,
    val timeBlocks: List<TimeBlock> = emptyList()
)