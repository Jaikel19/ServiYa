package com.example.seviya.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.shared.presentation.ClientPaymentUpload.ClientPaymentUploadViewModel


@Composable
actual fun ClientPaymentUploadPlatformScreen(
    appointmentId: String,
    viewModel: ClientPaymentUploadViewModel,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Subir comprobante no disponible en iOS por ahora")
    }
}