package com.example.shared.presentation.clientAppointmentDetail

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.OtpAppointment
import com.example.shared.domain.entity.ReviewMeta
import com.example.shared.domain.entity.WorkerProfile

data class ClientAppointmentDetailUiState(
    val isLoading: Boolean = false,
    val appointment: Appointment? = null,
    val otp: OtpAppointment? = null,
    val worker: WorkerProfile? = null,
    val cancellationPolicy: CancellationPolicy? = null,
    val reviewMeta: ReviewMeta = ReviewMeta(),
    val errorMessage: String? = null
) {
    val canShowClientSummary: Boolean
        get() = appointment != null

    val canShowOtp: Boolean
        get() = appointment?.status.equals("confirmed", ignoreCase = true) &&
                !otp?.code.isNullOrBlank()

    val canChat: Boolean
        get() = appointment?.status.equals("confirmed", ignoreCase = true) ||
                appointment?.status.equals("in_progress", ignoreCase = true) ||
                appointment?.status.equals("completed", ignoreCase = true)

    val canReview: Boolean
        get() = appointment?.status.equals("completed", ignoreCase = true) &&
                !reviewMeta.clientToWorkerCreated

    val canCancel: Boolean
        get() = appointment?.status.equals("confirmed", ignoreCase = true)
}