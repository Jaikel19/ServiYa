package com.example.shared.presentation.clientAppointmentDetail

import com.example.shared.domain.entity.Booking
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.WorkerProfile

data class ClientAppointmentDetailUiState(
    val isLoading: Boolean = false,
    val booking: Booking? = null,
    val worker: WorkerProfile? = null,
    val cancellationPolicy: CancellationPolicy? = null,
    val errorMessage: String? = null
) {
    val canShowClientSummary: Boolean
        get() = booking?.status == "confirmed" &&
                !booking.paymentReceiptUrl.isNullOrBlank()

    val canShowOtp: Boolean
        get() = booking?.status == "confirmed" && !booking.otpCode.isNullOrBlank()

    val canCancel: Boolean
        get() = booking?.status == "confirmed"

    val canChat: Boolean
        get() = booking?.status == "confirmed"

    val canReview: Boolean
        get() = booking?.status == "confirmed"

    val hasReceipt: Boolean
        get() = !booking?.paymentReceiptUrl.isNullOrBlank()
}