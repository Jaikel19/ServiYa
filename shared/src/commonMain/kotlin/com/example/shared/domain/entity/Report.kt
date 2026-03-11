package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: String = "",
    val clientId: String = "",
    val workerId: String = "",
    val reason: String = "",
    val description: String? = null,
    val createdAt: Long = 0L
)