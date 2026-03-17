package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class OtpAppointment(
    val id: String = "",
    val purpose: String = "",           // start
    val codeHash: String = "",
    val createdAt: String = "",         // YYYY-MM-DDTHH:mm
    val expiresAt: String = "",         // YYYY-MM-DDTHH:mm
    val usedAt: String? = null,         // YYYY-MM-DDTHH:mm
    val status: String = ""             // GENERATED | VERIFIED | EXPIRED
)