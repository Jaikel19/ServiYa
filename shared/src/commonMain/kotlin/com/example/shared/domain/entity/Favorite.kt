package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val id: String = "",
    val workerId: String = "",
    val workerName: String = "",
    val addedAt: String = "",
)
