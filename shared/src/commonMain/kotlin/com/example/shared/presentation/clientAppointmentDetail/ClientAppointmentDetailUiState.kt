package com.example.shared.presentation.clientAppointmentDetail

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.OtpAppointment
import com.example.shared.domain.entity.ReviewMeta
import com.example.shared.domain.entity.WorkerProfile
import com.example.shared.presentation.cancellation.AppointmentCancellationPreview

data class ClientAppointmentDetailUiState(
    val isLoading: Boolean = false,
    val appointment: Appointment? = null,
    val otp: OtpAppointment? = null,
    val worker: WorkerProfile? = null,
    val cancellationPolicy: CancellationPolicy? = null,
    val reviewMeta: ReviewMeta = ReviewMeta(),
    val cancellationPreview: AppointmentCancellationPreview? = null,
    val showCancellationPreview: Boolean = false,
    val isPreparingCancellationPreview: Boolean = false,
    val isCancellingAppointment: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
) {
    val canShowClientSummary: Boolean
        get() = appointment != null

    val canShowOtp: Boolean
        get() = appointment?.status.equals("confirmed", ignoreCase = true) &&
                !otp?.code.isNullOrBlank()

    val canCancel: Boolean
        get() = appointment?.status == "confirmed" && !isCancellingAppointment

    val canChat: Boolean
        get() = appointment?.status.equals("confirmed", ignoreCase = true) ||
                appointment?.status.equals("in_progress", ignoreCase = true) ||
                appointment?.status.equals("completed", ignoreCase = true)

    val canReview: Boolean
        get() = appointment?.status.equals("completed", ignoreCase = true) &&
                !reviewMeta.clientToWorkerCreated
}