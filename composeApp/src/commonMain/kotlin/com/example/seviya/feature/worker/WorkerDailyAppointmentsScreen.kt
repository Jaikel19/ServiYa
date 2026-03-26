package com.example.seviya.feature.worker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.shared.presentation.workerDailyAppointments.WorkerDailyAppointmentsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerDailyAppointmentsScreen(
    workerId: String,
    onBack: () -> Unit,
    onOpenMaps: (latitude: Double, longitude: Double, clientName: String) -> Unit,
) {
  val viewModel: WorkerDailyAppointmentsViewModel = koinViewModel()

  LaunchedEffect(workerId) { viewModel.loadAppointments(workerId) }

  WorkerDailyAppointmentsPlatformScreen(
      workerId = workerId,
      viewModel = viewModel,
      onBack = onBack,
      onOpenMaps = onOpenMaps,
  )
}

@Composable
expect fun WorkerDailyAppointmentsPlatformScreen(
    workerId: String,
    viewModel: WorkerDailyAppointmentsViewModel,
    onBack: () -> Unit,
    onOpenMaps: (latitude: Double, longitude: Double, clientName: String) -> Unit,
)
