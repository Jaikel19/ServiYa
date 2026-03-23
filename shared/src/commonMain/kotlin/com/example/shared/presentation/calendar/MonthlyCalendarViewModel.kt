package com.example.shared.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.domain.entity.Appointment
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
import com.example.shared.presentation.calendar.CalendarUserRole

@OptIn(ExperimentalTime::class)
class MonthlyCalendarViewModel(
    private val appointmentRepository: IAppointmentRepository
) : ViewModel() {

    private val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    private val _uiState = MutableStateFlow(
        MonthlyCalendarUiState(
            currentMonth = today.monthNumber,
            currentYear = today.year,
            currentWeekDay = today.dayOfMonth,
            appointments = emptyList(),
            selectedAppointment = null,
            isLoading = false,
            errorMessage = null
        )
    )

    val uiState: StateFlow<MonthlyCalendarUiState> = _uiState.asStateFlow()

    fun loadAppointments(userId: String, role: CalendarUserRole) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                appointments = emptyList(),
                errorMessage = null
            )

            val appointmentsFlow = when (role) {
                CalendarUserRole.WORKER -> appointmentRepository.getAppointmentsByWorker(userId)
                CalendarUserRole.CLIENT -> appointmentRepository.getAppointmentsByClient(userId)
            }

            appointmentsFlow
                .onEach { appointments ->
                    val visibleAppointments = appointments.filter { appointment ->
                        appointment.status.equals("confirmed", ignoreCase = true) ||
                                appointment.status.equals("in_progress", ignoreCase = true) ||
                                appointment.status.equals("completed", ignoreCase = true)
                    }

                    println("DEBUG appointments visibles en agenda ($role): $visibleAppointments")

                    val currentSelected = _uiState.value.selectedAppointment
                    val refreshedSelected = if (currentSelected != null) {
                        visibleAppointments.firstOrNull { it.id == currentSelected.id }
                    } else {
                        null
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        appointments = visibleAppointments,
                        selectedAppointment = refreshedSelected,
                        errorMessage = null
                    )
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        appointments = emptyList(),
                        errorMessage = e.message ?: "Error fetching appointments"
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

    fun selectAppointment(appointment: Appointment) {
        _uiState.value = _uiState.value.copy(
            selectedAppointment = appointment
        )
    }

    fun clearSelectedAppointment() {
        _uiState.value = _uiState.value.copy(
            selectedAppointment = null
        )
    }

    fun setCurrentWeekDay(day: Int) {
        _uiState.value = _uiState.value.copy(
            currentWeekDay = day
        )
    }

    fun approveAppointment(appointmentId: String) {
        viewModelScope.launch {
            try {
                appointmentRepository.approveAppointment(appointmentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al aprobar cita"
                )
            }
        }
    }

    fun confirmPayment(appointmentId: String) {
        viewModelScope.launch {
            try {
                appointmentRepository.confirmPayment(appointmentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al confirmar pago"
                )
            }
        }
    }

    fun startAppointment(appointmentId: String) {
        viewModelScope.launch {
            try {
                appointmentRepository.startAppointment(appointmentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al iniciar cita"
                )
            }
        }
    }

    fun completeAppointment(appointmentId: String) {
        viewModelScope.launch {
            try {
                appointmentRepository.completeAppointment(appointmentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al completar cita"
                )
            }
        }
    }

    fun rejectAppointmentByWorker(appointmentId: String) {
        viewModelScope.launch {
            try {
                appointmentRepository.rejectAppointmentByWorker(appointmentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al rechazar cita"
                )
            }
        }
    }

    fun cancelAppointmentByWorker(appointmentId: String) {
        viewModelScope.launch {
            try {
                appointmentRepository.cancelAppointmentByWorker(appointmentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cancelar cita"
                )
            }
        }
    }

    fun cancelAppointmentByClient(appointmentId: String) {
        viewModelScope.launch {
            try {
                appointmentRepository.cancelAppointmentByClient(appointmentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cancelar cita"
                )
            }
        }
    }
}