package com.example.seviya.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.shared.presentation.workerDailyAppointments.WorkerDailyAppointmentsViewModel

@Composable
actual fun WorkerDailyAppointmentsPlatformScreen(
    workerId: String,
    viewModel: WorkerDailyAppointmentsViewModel,
    onBack: () -> Unit,
    onOpenMaps: (latitude: Double, longitude: Double, clientName: String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Mapa de citas no disponible en iOS por ahora")
    }
}