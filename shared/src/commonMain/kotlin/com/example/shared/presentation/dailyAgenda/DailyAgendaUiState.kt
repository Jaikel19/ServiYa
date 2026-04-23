package com.example.shared.presentation.dailyAgenda

import com.example.shared.domain.entity.Appointment

data class DailyAgendaUiState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
