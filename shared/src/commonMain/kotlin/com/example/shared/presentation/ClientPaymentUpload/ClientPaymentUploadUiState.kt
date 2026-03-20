package com.example.shared.presentation.ClientPaymentUpload

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt

data class ClientPaymentUploadUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val appointment: Appointment? = null,
    val paymentReceipt: PaymentReceipt? = null,
    val selectedImageBytes: ByteArray? = null,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
)