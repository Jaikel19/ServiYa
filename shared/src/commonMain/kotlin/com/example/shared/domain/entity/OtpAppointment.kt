package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class OtpAppointment(
    val id: String = "",
    val codeHash: String = "",
    val createdAt: Long = 0L,
)