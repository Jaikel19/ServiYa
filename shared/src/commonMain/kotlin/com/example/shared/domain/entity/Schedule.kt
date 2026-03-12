package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val dayKey: String = "",
    val dayNumber: Int = 0,
    val enabled: Boolean = false,
    val timeBlocks: List<String> = emptyList(),
    val updatedAt: Long = 0L
)