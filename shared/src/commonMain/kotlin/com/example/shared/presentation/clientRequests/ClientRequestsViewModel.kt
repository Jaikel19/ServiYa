package com.example.shared.presentation.clientRequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClientRequestsViewModel(
    private val appointmentRepository: IAppointmentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClientRequestsUiState())
    val uiState: StateFlow<ClientRequestsUiState> = _state.asStateFlow()

    fun loadRequests(clientId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                pendingAppointments = emptyList(),
                approvedAppointments = emptyList(),
                errorMessage = null
            )

            appointmentRepository.getAppointmentsByClient(clientId)
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        pendingAppointments = emptyList(),
                        approvedAppointments = emptyList(),
                        errorMessage = e.message ?: "Error fetching client requests"
                    )
                }
                .collectLatest { appointments ->
                    val pending = appointments
                        .filter { it.status.equals("pending", ignoreCase = true) }
                        .sortedBy { it.serviceStartAt }

                    val approved = appointments
                        .filter { it.status.equals("approved", ignoreCase = true) }
                        .sortedBy { it.serviceStartAt }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        pendingAppointments = pending,
                        approvedAppointments = approved,
                        errorMessage = null
                    )
                }
        }
    }
}