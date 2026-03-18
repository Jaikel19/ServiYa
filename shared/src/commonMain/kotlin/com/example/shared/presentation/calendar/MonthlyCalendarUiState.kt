package com.example.shared.presentation.calendar

import com.example.shared.domain.entity.Appointment

data class MonthlyCalendarUiState(
    val currentMonth: Int,
    val currentYear: Int,
    val currentWeekDay: Int,
    val appointments: List<Appointment> = emptyList(),
    val selectedAppointment: Appointment? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)