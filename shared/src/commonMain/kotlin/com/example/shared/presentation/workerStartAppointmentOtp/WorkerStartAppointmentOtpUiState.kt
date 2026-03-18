package com.example.shared.presentation.workerStartAppointmentOtp

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.OtpAppointment

data class WorkerStartAppointmentOtpUiState(
    val appointment: Appointment? = null,
    val otp: OtpAppointment? = null,
    val otpInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val startSuccess: Boolean = false
)