package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String = "",
    val appointmentId: String = "",
    val authorId: String = "",
    val authorRole: String = "",
    val targetId: String = "",
    val targetRole: String = "",
    val clientName: String = "",
    val workerName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val images: List<String> = emptyList(),
    val direction: String = "",
    val status: String = "published",
    val serviceSummary: String = "",
    val createdAt: String = ""
)