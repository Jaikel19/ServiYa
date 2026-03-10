package com.example.shared.presentation.calendar

import com.example.shared.domain.entity.Booking

data class MonthlyCalendarUiState(
    val currentMonth: Int,
    val currentYear: Int,
    val bookings: List<Booking> = emptyList(),
    val selectedBooking: Booking? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)