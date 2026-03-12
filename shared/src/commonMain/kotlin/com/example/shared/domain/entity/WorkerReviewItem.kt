package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class WorkerReviewItem(
    val id: String = "",
    val appointmentId: String = "",
    val authorId: String = "",
    val authorRole: String = "",
    val clientName: String = "",
    val comment: String = "",
    val direction: String = "",
    val images: List<String> = emptyList(),
    val rating: Int = 0,
    val serviceSummary: ReviewServiceSummary = ReviewServiceSummary(),
    val status: String = "",
    val targetId: String = "",
    val targetRole: String = "",
    val workerName: String = ""
)