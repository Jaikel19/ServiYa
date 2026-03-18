package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PaymentReceipt(
    val id: String = "",
    val attemptNumber: Long = 0L,
    val imageUrl: String = "",
    val note: String? = null,
    val sentAt: String = "",            // YYYY-MM-DDTHH:mm
    val reviewedAt: String? = null,     // YYYY-MM-DDTHH:mm
    val reviewedBy: String? = null,
    val rejectionReason: String? = null,
    val status: String = ""             // pending ||payment_pending | APPROVED | REJECTED
)