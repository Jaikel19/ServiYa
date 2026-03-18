package com.example.shared.presentation.workerAppointmentDetail

import com.example.shared.domain.entity.PaymentReceipt

data class WorkerAppointmentDetailUiState(
    val paymentReceipt: PaymentReceipt? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)