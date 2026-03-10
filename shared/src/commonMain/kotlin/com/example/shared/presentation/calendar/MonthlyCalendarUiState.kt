package com.example.shared.presentation.calendar

import com.example.shared.domain.entity.Booking

data class MonthlyCalendarUiState(
    val isLoading: Boolean = false,
    val currentMonth: Int = 3,
    val currentYear: Int = 2025,
    val bookings: List<Booking> = emptyList(),
    val selectedBooking: Booking? = null,
    val errorMessage: String? = null
)