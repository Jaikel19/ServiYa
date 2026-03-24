package com.example.shared.presentation.workerAppointmentDetail

import com.example.shared.domain.entity.PaymentReceipt
import com.example.shared.domain.entity.ReviewMeta

data class WorkerAppointmentDetailUiState(
    val paymentReceipt: PaymentReceipt? = null,
    val reviewMeta: ReviewMeta = ReviewMeta(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)