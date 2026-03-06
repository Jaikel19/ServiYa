package com.example.shared.domain.entity

data class Service(
    val id: String = "",
    val name: String = "",
    val cost: Double = 0.0,
    val duration: String = "",
    val description: String = ""
)