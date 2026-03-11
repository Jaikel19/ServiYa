package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PaymentReceipt(
    val id: String = "",
    val imageUrl: String = "",
    val sentAt: Long = 0L,
    val status: String = "",
    val note: String? = null
)