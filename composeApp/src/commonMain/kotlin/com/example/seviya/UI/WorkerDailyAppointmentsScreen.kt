package com.example.seviya.UI

import androidx.compose.runtime.Composable
import com.example.shared.presentation.workerDailyAppointments.WorkerDailyAppointmentsViewModel

@Composable
expect fun WorkerDailyAppointmentsScreen(
    workerId: String,
    viewModel: WorkerDailyAppointmentsViewModel,
    onBack: () -> Unit,
    onOpenMaps: (latitude: Double, longitude: Double, clientName: String) -> Unit
)