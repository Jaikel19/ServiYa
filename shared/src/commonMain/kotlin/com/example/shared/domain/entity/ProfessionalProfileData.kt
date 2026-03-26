package com.example.shared.domain.entity

data class ProfessionalProfileData(
    val workerId: String = "",
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val description: String = "",
    val profilePictureLink: String = "",
    val role: String = "",
    val stars: Double = 0.0,
    val status: String = "",
    val travelTime: Int = 0,
    val trustScore: Int = 0,
    val locationProvince: String = "",
    val categoryNames: List<String> = emptyList(),
    val services: List<Service> = emptyList(),
    val cancellationPolicy: CancellationPolicy? = null,
    val schedule: List<WorkerSchedule> = emptyList(),
    val portfolios: List<PortfolioItem> = emptyList(),
    val reviews: List<WorkerReviewItem> = emptyList(),
)
