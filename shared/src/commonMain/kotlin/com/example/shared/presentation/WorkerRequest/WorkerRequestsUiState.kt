package com.example.shared.presentation.WorkerRequest

import com.example.shared.domain.entity.Booking

data class WorkerRequestsUiState(
    val isLoading: Boolean = false,
    val requests: List<Booking  > = emptyList(),
    val errorMessage: String? = null
)