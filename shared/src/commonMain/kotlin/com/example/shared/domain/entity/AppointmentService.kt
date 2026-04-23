package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentService(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val cost: Int = 0,
    val durationMinutes: Int = 0,
    val subtotal: Int = 0,
)
