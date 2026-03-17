package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PaymentReceipt(
    val id: String = "",
    val attemptNumber: Int = 0,
    val imageUrl: String = "",
    val note: String? = null,
    val sentAt: String = "",            // YYYY-MM-DDTHH:mm
    val reviewedAt: String? = null,     // YYYY-MM-DDTHH:mm
    val reviewedBy: String? = null,
    val rejectionReason: String? = null,
    val status: String = ""             // PENDING | APPROVED | REJECTED
)