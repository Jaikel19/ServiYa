package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val id: String = "",
    val serviceName: String = "",
    val date: String = "",   // formato: YYYY-MM-DD
    val time: String = "",   // formato: HH:mm
    val status: String = "",
    val notes: String = ""
)