package com.example.shared.presentation.clientAppointmentDetail

import com.example.shared.domain.entity.Booking
import com.example.shared.domain.entity.CancellationPolicy

data class ClientAppointmentDetailUiState(
    val isLoading: Boolean = false,
    val booking: Booking? = null,
    val cancellationPolicy: CancellationPolicy? = null,
    val errorMessage: String? = null
) {
    val canShowOtp: Boolean
        get() = booking?.status == "confirmed" && !booking.otpCode.isNullOrBlank()

    val canCancel: Boolean
        get() = booking?.status == "confirmed"

    val canChat: Boolean
        get() = booking != null && booking.status != "cancelled"

    val canReview: Boolean
        get() = booking?.status == "completed" && booking.ratingToWorkerDone == false

    val hasReceipt: Boolean
        get() = !booking?.paymentReceiptUrl.isNullOrBlank()
}