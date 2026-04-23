package com.example.shared.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkerProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val identification: String = "",
    val phone: String = "",
    val profilePicture: String = "",
    @SerialName("_description") val description: String = "",
    val role: String = "",
    val stars: Double = 0.0,
    val status: String = "",
    val travelTime: Int = 0,
    val trustScore: Int = 0,
    val categories: List<String> = emptyList(),
)
