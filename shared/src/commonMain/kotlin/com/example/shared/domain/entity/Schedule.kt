package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val days: String = "",
    val startTime: String = "",
    val endTime: String = ""
)