package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val body: String? = null,
    val read: Boolean = false,
    val createdAt: Long = 0L
)
