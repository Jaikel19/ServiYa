package com.example.shared.presentation.workerDashboard

import com.example.shared.domain.entity.Booking
import com.example.shared.domain.entity.ProfessionalProfileData

data class WorkerDashboardUiState(
    val isLoading: Boolean = false,
    val profile: ProfessionalProfileData? = null,
    val bookings: List<Booking> = emptyList(),
    val errorMessage: String? = null,
)
