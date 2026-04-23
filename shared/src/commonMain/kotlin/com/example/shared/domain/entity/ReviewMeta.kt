package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class ReviewMeta(
    val id: String = "",
    val clientToWorkerCreated: Boolean = false,
    val clientToWorkerReviewId: String? = null,
    val workerToClientCreated: Boolean = false,
    val workerToClientReviewId: String? = null,
)
