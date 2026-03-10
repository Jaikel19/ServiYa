package com.example.shared.presentation.calendar

import com.example.shared.domain.entity.Booking

data class MonthlyCalendarUiState(
    val isLoading: Boolean = false,
    val currentMonth: Int = 10,
    val currentYear: Int = 2023,
    val bookings: List<Booking> = emptyList(),
    val selectedBooking: Booking? = null,
    val errorMessage: String? = null
)