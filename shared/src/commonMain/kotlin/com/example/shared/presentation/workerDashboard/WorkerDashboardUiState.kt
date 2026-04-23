package com.example.shared.presentation.workerDashboard

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.ProfessionalProfileData

data class WorkerDashboardUiState(
    val isLoading: Boolean = false,
    val profile: ProfessionalProfileData? = null,
    val appointments: List<Appointment> = emptyList(),
    val errorMessage: String? = null,
)