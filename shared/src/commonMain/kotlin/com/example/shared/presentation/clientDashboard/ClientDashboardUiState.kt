package com.example.shared.presentation.clientDashboard

import com.example.shared.domain.entity.Appointment

data class ClientDashboardUiState(
    val clientId: String = "",
    val clientName: String = "",
    val clientPhotoUrl: String = "",
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)