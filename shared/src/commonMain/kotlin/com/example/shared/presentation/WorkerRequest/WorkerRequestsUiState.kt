package com.example.shared.presentation.WorkerRequest

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt

data class WorkerRequestsUiState(
    val isLoading: Boolean = false,
    val isLoadingPayments: Boolean = false,
    val requests: List<Appointment> = emptyList(),
    val paymentPendingAppointments: List<Pair<Appointment, PaymentReceipt>> = emptyList(),
    val errorMessage: String? = null,
)
