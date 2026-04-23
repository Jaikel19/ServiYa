package com.example.shared.presentation.workerDailyAppointments

import com.example.shared.domain.entity.Appointment

enum class DailyView {
  LIST,
  MAP,
}

data class WorkerDailyAppointmentsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val appointments: List<Appointment> = emptyList(),
    val currentView: DailyView = DailyView.MAP,
    val searchQuery: String = "",
)
