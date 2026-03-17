package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    val id: String = "",

    val clientId: String = "",
    val clientName: String = "",

    val workerId: String = "",
    val workerName: String = "",

    val status: String = "",

    val timeZone: String = "America/Costa_Rica",
    val dateKey: String = "",           // YYYY-MM-DD
    val monthKey: String = "",          // YYYY-MM
    val dayKey: String = "",            // monday, tuesday...

    val serviceStartAt: String = "",    // YYYY-MM-DDTHH:mm
    val serviceEndAt: String = "",      // YYYY-MM-DDTHH:mm
    val serviceDurationMinutes: Int = 0,

    val travelTimeMinutesSnapshot: Int = 0,
    val bufferBeforeMinutes: Int = 0,
    val bufferAfterMinutes: Int = 0,
    val blockedStartAt: String = "",    // YYYY-MM-DDTHH:mm
    val blockedEndAt: String = "",      // YYYY-MM-DDTHH:mm
    val blockedTotalMinutes: Int = 0,

    val services: List<AppointmentService> = emptyList(),
    val totalCost: Int = 0,
    val currency: String = "CRC",

    val clientAddressId: String = "",
    val location: AppointmentLocation = AppointmentLocation(),

    val clientToWorkerReviewDone: Boolean = false,
    val workerToClientReviewDone: Boolean = false,

    val cancellationBy: String? = null,
    val cancellationReason: String? = null,
    val cancelledAt: String? = null,    // YYYY-MM-DDTHH:mm

    val createdAt: String = "",         // YYYY-MM-DDTHH:mm
    val updatedAt: String = ""          // YYYY-MM-DDTHH:mm
)