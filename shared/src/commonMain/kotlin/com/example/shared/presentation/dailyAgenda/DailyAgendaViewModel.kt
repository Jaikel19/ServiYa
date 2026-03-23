package com.example.shared.presentation.dailyAgenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.presentation.calendar.CalendarUserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DailyAgendaViewModel(
    private val appointmentRepository: IAppointmentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyAgendaUiState())
    val uiState: StateFlow<DailyAgendaUiState> = _state.asStateFlow()

    fun loadAppointments(userId: String, role: CalendarUserRole) {
        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                .toString()

            val flow = when (role) {
                CalendarUserRole.WORKER -> appointmentRepository.getAppointmentsByWorker(userId)
                CalendarUserRole.CLIENT -> appointmentRepository.getAppointmentsByClient(userId)
            }

            flow
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error cargando citas"
                    )
                }

                .collectLatest { appointments ->

                    val visibleAppointments = appointments
                        .filter { appointment ->
                            !appointment.status.equals("cancelled", ignoreCase = true) &&
                                    !appointment.status.equals("rejected", ignoreCase = true)
                        }
                        .sortedBy { appointment ->
                            appointment.serviceStartAt
                        }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        appointments = visibleAppointments
                    )
                }
        }
    }
}