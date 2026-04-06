package com.example.shared.presentation.workerDailyAppointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.utils.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WorkerDailyAppointmentsViewModel(private val appointmentRepository: IAppointmentRepository) :
    ViewModel() {

  private val _state = MutableStateFlow(WorkerDailyAppointmentsUiState())
  val uiState: StateFlow<WorkerDailyAppointmentsUiState> = _state.asStateFlow()

  fun loadAppointments(workerId: String) {
    viewModelScope.launch {
      _state.value = _state.value.copy(isLoading = true, errorMessage = null)

      val today = DateTimeUtils.todayDateKey()

      appointmentRepository
          .getAppointmentsByWorker(workerId)
          .catch { e ->
            _state.value =
                _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando citas",
                )
          }
          .collectLatest { appointments ->
            _state.value =
                _state.value.copy(
                    isLoading = false,
                    appointments =
                        appointments.filter {
                          it.dateKey == today &&
                              it.status !in listOf("cancelled", "rejected")
                        },
                )
          }
    }
  }

  fun onViewChanged(view: DailyView) {
    _state.value = _state.value.copy(currentView = view)
  }
}
