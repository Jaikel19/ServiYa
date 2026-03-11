package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: String = "",
    val name: String = "",
    val cost: Double = 0.0,
    val duration: String = "",
    val description: String = ""
)







