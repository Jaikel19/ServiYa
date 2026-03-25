package com.example.seviya.feature.client

import androidx.compose.runtime.Composable
import com.example.shared.presentation.ClientPaymentUpload.ClientPaymentUploadViewModel


@Composable
expect fun ClientPaymentUploadScreen(
    appointmentId: String,
    viewModel: ClientPaymentUploadViewModel,
    onBack: () -> Unit
)