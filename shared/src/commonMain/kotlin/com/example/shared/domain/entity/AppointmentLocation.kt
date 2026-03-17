package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentLocation(
    val id: String = "",
    val alias: String = "",
    val province: String = "",
    val district: String = "",
    val canton: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val reference: String = ""
)