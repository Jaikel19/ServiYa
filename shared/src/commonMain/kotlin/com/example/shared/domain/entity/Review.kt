package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String = "",
    val appointmentId: String = "",
    val authorId: String = "",
    val authorRole: String = "",
    val clientName: String = "",
    val workerName: String = "",
    val comment: String = "",
    val createdAt: Long = 0L,
    val direction: String = "",
    val images: List<String> = emptyList(),
    val rating: Int = 0,
    val serviceSummary: List<String> = emptyList(),
    val status: String = "",
    val targetId: String = "",
    val targetRole: String = ""
)