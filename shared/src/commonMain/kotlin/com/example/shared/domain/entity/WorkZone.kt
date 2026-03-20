package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class WorkZone(
    val id: String = "",
    val province: String = "",
    val canton: String = "",
    val district: String = "",
    val locationCode: String = "",
    val blocked: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)