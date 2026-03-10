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

@Serializable
data class WorkerProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val identification: String = "",
    val phone: String = "",
    val profilePicture: String = "",
    val role: String = "",
    val stars: Double = 0.0,
    val status: String = "",
    val travelTime: Int = 0,
    val trustScore: Int = 0,
    val categories: List<String> = emptyList()
)

@Serializable
data class Category(
    val id: String = "",
    val name: String = ""
)

@Serializable
data class Address(
    val id: String = "",
    val alias: String = "",
    val district: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val province: String = "",
    val reference: String = ""
)

data class ProfessionalProfileData(
    val workerId: String = "",
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePictureLink: String = "",
    val role: String = "",
    val stars: Double = 0.0,
    val status: String = "",
    val travelTime: Int = 0,
    val trustScore: Int = 0,
    val locationProvince: String = "",
    val categoryNames: List<String> = emptyList(),
    val services: List<Service> = emptyList()
)