package com.example.shared.presentation.clientDashboard

import com.example.shared.domain.entity.Booking

data class ClientDashboardUiState(
    val clientId: String = "",
    val clientName: String = "",
    val clientPhotoUrl: String = "",
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)