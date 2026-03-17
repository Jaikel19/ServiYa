package com.example.shared.presentation.WorkerPaymentDetail

import com.example.shared.domain.entity.Booking

data class WorkerPaymentDetailUiState(
    val booking: Booking? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)