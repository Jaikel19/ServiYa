package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioItem(
    val id: String = "",
    val description: String = "",
    val image: String = "",
    val services: List<String> = emptyList(),
    val workerId: String = ""
)