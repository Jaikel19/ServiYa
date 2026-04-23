package com.example.shared.presentation.cancellation

data class AppointmentCancellationPreview(
    val cancelledBy: String = "",
    val policyLabel: String = "",
    val refundPercentage: Int = 0,
    val refundAmount: Int = 0,
    val nonRefundableAmount: Int = 0,
    val appointmentTotal: Int = 0,
    val warningMessage: String = "",
)
