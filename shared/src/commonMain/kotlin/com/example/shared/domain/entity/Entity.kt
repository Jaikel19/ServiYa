package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: Long,
    val title: String,
    val description: String? = null,
    val category: String? = null,
    val price: Double? = null,
    val isActive: Boolean = true
)