package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String = "",
    val name: String = ""
)