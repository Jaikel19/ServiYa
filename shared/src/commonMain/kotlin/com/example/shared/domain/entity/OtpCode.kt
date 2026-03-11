package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class OtpCode(
    val id: String = "",
    val channel: String = "",
    val codeHash: String = "",
    val purpose: String = "",
)