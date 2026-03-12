package com.example.shared.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.IBookingRepository
import com.example.shared.domain.entity.Booking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class MonthlyCalendarViewModel(
    private val bookingRepository: IBookingRepository
) : ViewModel() {

    private val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    private val _uiState = MutableStateFlow(
        MonthlyCalendarUiState(
            currentMonth = today.monthNumber,
            currentYear = today.year,
            currentWeekDay = today.dayOfMonth,
            bookings = emptyList(),
            selectedBooking = null,
            isLoading = false,
            errorMessage = null
        )
    )

    val uiState: StateFlow<MonthlyCalendarUiState> = _uiState.asStateFlow()

    fun loadBookings(workerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                bookings = emptyList(),
                errorMessage = null
            )

            bookingRepository.getBookingsByWorker(workerId)
                .onEach { bookings ->
                    println("DEBUG bookings recibidos en ViewModel: $bookings")

                    val currentSelected = _uiState.value.selectedBooking
                    val refreshedSelected = if (currentSelected != null) {
                        bookings.firstOrNull { it.id == currentSelected.id }
                    } else {
                        null
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        bookings = bookings,
                        selectedBooking = refreshedSelected,
                        errorMessage = null
                    )
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        bookings = emptyList(),
                        errorMessage = e.message ?: "Error fetching bookings"
                    )
                }
                .collect()
        }
    }

    fun goToPreviousMonth() {
        val currentMonth = _uiState.value.currentMonth
        val currentYear = _uiState.value.currentYear

        val newMonth: Int
        val newYear: Int

        if (currentMonth == 1) {
            newMonth = 12
            newYear = currentYear - 1
        } else {
            newMonth = currentMonth - 1
            newYear = currentYear
        }

        val newWeekDay =
            if (newMonth == today.monthNumber && newYear == today.year) {
                today.dayOfMonth
            } else {
                1
            }

        _uiState.value = _uiState.value.copy(
            currentMonth = newMonth,
            currentYear = newYear,
            currentWeekDay = newWeekDay
        )
    }

    fun goToNextMonth() {
        val currentMonth = _uiState.value.currentMonth
        val currentYear = _uiState.value.currentYear

        val newMonth: Int
        val newYear: Int

        if (currentMonth == 12) {
            newMonth = 1
            newYear = currentYear + 1
        } else {
            newMonth = currentMonth + 1
            newYear = currentYear
        }

        val newWeekDay =
            if (newMonth == today.monthNumber && newYear == today.year) {
                today.dayOfMonth
            } else {
                1
            }

        _uiState.value = _uiState.value.copy(
            currentMonth = newMonth,
            currentYear = newYear,
            currentWeekDay = newWeekDay
        )
    }

    fun selectBooking(booking: Booking) {
        _uiState.value = _uiState.value.copy(
            selectedBooking = booking
        )
    }

    fun clearSelectedBooking() {
        _uiState.value = _uiState.value.copy(
            selectedBooking = null
        )
    }

    fun setCurrentWeekDay(day: Int) {
        _uiState.value = _uiState.value.copy(
            currentWeekDay = day
        )
    }

    fun confirmPayment(bookingId: String) {
        viewModelScope.launch {
            try {
                bookingRepository.confirmPayment(bookingId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al confirmar pago"
                )
            }
        }
    }

    fun startAppointment(bookingId: String) {
        viewModelScope.launch {
            try {
                bookingRepository.startAppointment(bookingId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al iniciar cita"
                )
            }
        }
    }

    fun completeAppointment(bookingId: String) {
        viewModelScope.launch {
            try {
                bookingRepository.completeAppointment(bookingId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al completar cita"
                )
            }
        }
    }

    fun cancelAppointmentByWorker(bookingId: String) {
        viewModelScope.launch {
            try {
                bookingRepository.cancelAppointmentByWorker(bookingId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cancelar cita"
                )
            }
        }
    }
}