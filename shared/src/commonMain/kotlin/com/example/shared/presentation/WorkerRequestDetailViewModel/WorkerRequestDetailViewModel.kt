package com.example.shared.presentation.WorkerRequestDetailViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class WorkerRequestDetailViewModel(
    private val repository: IAppointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerRequestDetailUiState())
    val uiState: StateFlow<WorkerRequestDetailUiState> = _uiState.asStateFlow()

    fun loadBooking(appointmentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                repository.getAppointmentById(appointmentId)
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Error cargando solicitud"
                        )
                    }
                    .first()
                    .let { appointment ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            appointment = appointment
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando solicitud"
                )
            }
        }
    }

    fun acceptRequest() {
        val bookingId = _uiState.value.appointment?.id ?: return
        viewModelScope.launch {
            repository.confirmPayment(bookingId)
        }
    }

    fun rejectRequest() {
        val bookingId = _uiState.value.appointment?.id ?: return
        viewModelScope.launch {
            repository.rejectAppointmentByWorker(bookingId)
        }
    }
}