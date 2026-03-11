package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class History(
    val id: String = "",
    val previousStatus: String? = null,
    val newStatus: String = "",
    val changedBy: String = "",
    val changedAt: Long = 0L,
    val note: String? = null
)