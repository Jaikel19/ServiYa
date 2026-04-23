package com.example.shared.presentation.WorkerRequestDetailViewModel

import com.example.shared.domain.entity.Appointment

data class WorkerRequestDetailUiState(
    val isLoading: Boolean = false,
    val appointment: Appointment? = null,
    val errorMessage: String? = null,
    val actionInProgress: Boolean = false,
    val requestHandled: Boolean = false,
)
