package com.example.shared.presentation.clientDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ClientDashboardViewModel(
    private val appointmentRepository: IAppointmentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientDashboardUiState())
    val uiState: StateFlow<ClientDashboardUiState> = _uiState.asStateFlow()

    private var appointmentsJob: Job? = null

    fun loadAppointments(clientId: String) {
        if (clientId.isBlank()) return

        appointmentsJob?.cancel()
        appointmentsJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        clientId = clientId,
                        isLoading = true,
                        errorMessage = null,
                        appointments = emptyList(),
                    )

                appointmentRepository
                    .getAppointmentsByClient(clientId)
                    .onEach { appointments ->
                        val fallbackName = appointments.firstOrNull()?.clientName.orEmpty()

                        _uiState.value =
                            _uiState.value.copy(
                                clientId = clientId,
                                clientName = _uiState.value.clientName.ifBlank { fallbackName },
                                appointments = appointments,
                                isLoading = false,
                                errorMessage = null,
                            )
                    }
                    .catch { e ->
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                appointments = emptyList(),
                                errorMessage = e.message ?: "Error al cargar las citas del cliente.",
                            )
                    }
                    .collect()
            }
    }
}