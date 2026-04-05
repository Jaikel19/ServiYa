package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val recipientRole: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val isRead: Boolean = false,
    val createdAt: String = "",
    val readAt: String? = null,
    val appointmentId: String? = null,
    val deepLink: String = "",
    val actorId: String? = null,
)