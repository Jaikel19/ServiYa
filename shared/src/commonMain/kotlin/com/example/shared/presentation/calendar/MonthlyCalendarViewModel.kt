package com.example.shared.presentation.calendar

import androidx.lifecycle.ViewModel
import com.example.shared.domain.entity.Booking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MonthlyCalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MonthlyCalendarUiState(
            currentMonth = 10,
            currentYear = 2023
        )
    )
    val uiState: StateFlow<MonthlyCalendarUiState> = _uiState.asStateFlow()

    init {
        loadFakeBookings()
    }

    private fun loadFakeBookings() {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            bookings = listOf(
                Booking(
                    id = "1",
                    serviceName = "Corte de pelo",
                    date = "2023-10-03",
                    time = "10:00",
                    status = "scheduled",
                    notes = "Cliente pidió corte clásico"
                ),
                Booking(
                    id = "2",
                    serviceName = "Baño y grooming",
                    date = "2023-10-06",
                    time = "09:00",
                    status = "scheduled",
                    notes = "Mascota pequeña"
                ),
                Booking(
                    id = "3",
                    serviceName = "Baño y grooming",
                    date = "2023-10-06",
                    time = "14:30",
                    status = "scheduled",
                    notes = "Llevar shampoo especial"
                ),
                Booking(
                    id = "4",
                    serviceName = "Corte premium",
                    date = "2023-10-10",
                    time = "08:00",
                    status = "scheduled",
                    notes = "Incluye lavado"
                ),
                Booking(
                    id = "5",
                    serviceName = "Corte premium",
                    date = "2023-10-10",
                    time = "11:00",
                    status = "scheduled",
                    notes = "Cliente frecuente"
                ),
                Booking(
                    id = "6",
                    serviceName = "Corte premium",
                    date = "2023-10-11",
                    time = "12:00",
                    status = "scheduled",
                    notes = "Agregar secado"
                ),
                Booking(
                    id = "7",
                    serviceName = "Corte premium",
                    date = "2023-10-11",
                    time = "13:30",
                    status = "scheduled",
                    notes = "Cliente nuevo"
                )
            )
        )
    }

    fun goToPreviousMonth() {
        val currentMonth = _uiState.value.currentMonth
        val currentYear = _uiState.value.currentYear

        if (currentMonth == 1) {
            _uiState.value = _uiState.value.copy(
                currentMonth = 12,
                currentYear = currentYear - 1
            )
        } else {
            _uiState.value = _uiState.value.copy(
                currentMonth = currentMonth - 1
            )
        }
    }

    fun goToNextMonth() {
        val currentMonth = _uiState.value.currentMonth
        val currentYear = _uiState.value.currentYear

        if (currentMonth == 12) {
            _uiState.value = _uiState.value.copy(
                currentMonth = 1,
                currentYear = currentYear + 1
            )
        } else {
            _uiState.value = _uiState.value.copy(
                currentMonth = currentMonth + 1
            )
        }
    }

    fun selectBooking(booking: Booking) {
        _uiState.value = _uiState.value.copy(selectedBooking = booking)
    }

    fun clearSelectedBooking() {
        _uiState.value = _uiState.value.copy(selectedBooking = null)
    }
}