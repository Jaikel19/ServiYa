package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class OtpAppointment(
    val id: String = "current",
    val purpose: String = "start",          // start
    val code: String = "",                  // OTP visible en detalle
    val codeHash: String = "",
    val createdAt: String = "",             // YYYY-MM-DDTHH:mm
    val expiresAt: String = "",             // YYYY-MM-DDTHH:mm
    val usedAt: String? = null,             // YYYY-MM-DDTHH:mm
    val status: String = "GENERATED"        // GENERATED | VERIFIED | EXPIRED
)