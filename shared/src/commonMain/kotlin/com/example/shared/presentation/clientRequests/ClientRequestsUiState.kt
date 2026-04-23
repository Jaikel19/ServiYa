package com.example.shared.presentation.clientRequests

import com.example.shared.domain.entity.Appointment

data class ClientRequestsUiState(
    val isLoading: Boolean = false,
    val pendingAppointments: List<Appointment> = emptyList(),
    val approvedAppointments: List<Appointment> = emptyList(),
    val errorMessage: String? = null,
)
