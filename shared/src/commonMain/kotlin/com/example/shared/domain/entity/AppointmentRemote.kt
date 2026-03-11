package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentRemote(
    val clientId: String = "",
    val clientName: String = "",
    val workerId: String = "",
    val workerName: String = "",
    val date: String = "",
    val status: String = "",
    val totalCost: Double = 0.0,
    val services: List<Service> = emptyList(),
    val location: Address = Address()
)