package com.example.shared.presentation.professionalProfile

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.ProfessionalProfileData

data class ProfessionalProfileUiState(
    val isLoading: Boolean = false,
    val profile: ProfessionalProfileData? = null,
    val workerAppointments: List<Appointment> = emptyList(),
    val errorMessage: String? = null
)