package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String = "",
    val name: String = "",
    val identification: String = "",
    val phone: String = "",
    val email: String = "",
    val status: String = "",
    val profilePicture: String = "",
    val role: String = "",
    val stars: Double? = null,
    val trustScore: Int? = null,
    val travelTime: Int? = null,
    val categories: List<Category> = emptyList(),
    val createdAt: String  = "",
)