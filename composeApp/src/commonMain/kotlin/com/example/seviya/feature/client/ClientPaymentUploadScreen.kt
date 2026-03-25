package com.example.seviya.feature.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.shared.presentation.ClientPaymentUpload.ClientPaymentUploadViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientPaymentUploadRoute(
    appointmentId: String,
    onBack: () -> Unit
) {
    val viewModel: ClientPaymentUploadViewModel = koinViewModel()

    LaunchedEffect(appointmentId) {
        viewModel.loadData(appointmentId)
    }

    ClientPaymentUploadScreen(
        appointmentId = appointmentId,
        viewModel = viewModel,
        onBack = onBack
    )
}


@Composable
expect fun ClientPaymentUploadScreen(
    appointmentId: String,
    viewModel: ClientPaymentUploadViewModel,
    onBack: () -> Unit
)