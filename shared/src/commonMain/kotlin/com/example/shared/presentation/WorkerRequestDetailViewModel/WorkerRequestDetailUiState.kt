package com.example.shared.presentation.WorkerRequestDetailViewModel

import com.example.shared.domain.entity.Booking


data class WorkerRequestDetailUiState(
    val booking: Booking? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

