package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentService(
    val id: String = "",
    val name: String = "",
    val cost: Double = 0.0
)