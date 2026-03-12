package com.example.shared.domain.entity

data class WorkerListItemData(
    val workerId: String = "",
    val uid: String = "",
    val name: String = "",
    val profilePictureLink: String = "",
    val stars: Double = 0.0,
    val status: String = "",
    val categoryIds: List<String> = emptyList(),
    val categoryNames: List<String> = emptyList(),
    val province: String = "",
    val district: String = "",
    val startingPrice: Double = 0.0
)