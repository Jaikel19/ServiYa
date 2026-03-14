package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    val id: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val workerId: String = "",
    val workerName: String = "",
    val date: Long = 0L,
    val startTime: String = "",
    val endTime: String = "",
    val endDate: Long = 0L,
    val status: String = "",
    val clientAddressId: String = "",
    val location: AppointmentLocation = AppointmentLocation(),
    val services: List<AppointmentService> = emptyList(),
    val totalCost: Double = 0.0,
    val totalDurationMinutes: Int = 0,
    val blockingDurationMinutes: Int = 0,
    val travelTimeMinutesSnapshot: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)