package com.example.shared.presentation.WorkerPaymentDetail

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt

data class WorkerPaymentDetailUiState(
    val appointment: Appointment? = null,
    val paymentReceipt: PaymentReceipt? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)