package com.example.shared.presentation.workerAppointmentDetail

import com.example.shared.domain.entity.PaymentReceipt
import com.example.shared.domain.entity.ReviewMeta
import com.example.shared.presentation.cancellation.AppointmentCancellationPreview

data class WorkerAppointmentDetailUiState(
    val paymentReceipt: PaymentReceipt? = null,
    val reviewMeta: ReviewMeta = ReviewMeta(),
    val cancellationPreview: AppointmentCancellationPreview? = null,
    val showCancellationPreview: Boolean = false,
    val isPreparingCancellationPreview: Boolean = false,
    val isCancellingAppointment: Boolean = false,
    val cancellationCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)
