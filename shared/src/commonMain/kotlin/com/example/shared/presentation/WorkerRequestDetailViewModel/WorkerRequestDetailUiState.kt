package com.example.shared.presentation.WorkerRequestDetailViewModel

import com.example.shared.domain.entity.Appointment


data class WorkerRequestDetailUiState(
    val appointment: Appointment? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

