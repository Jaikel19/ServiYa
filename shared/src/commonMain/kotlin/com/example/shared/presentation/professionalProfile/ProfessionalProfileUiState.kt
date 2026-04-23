package com.example.shared.presentation.professionalProfile

import com.example.shared.domain.entity.ProfessionalProfileData

data class ProfessionalProfileUiState(
    val isLoading: Boolean = false,
    val profile: ProfessionalProfileData? = null,
    val errorMessage: String? = null
)