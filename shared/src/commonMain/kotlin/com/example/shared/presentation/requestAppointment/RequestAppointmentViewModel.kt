package com.example.shared.presentation.requestAppointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Address.IAddressRepository
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.domain.entity.Appointment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RequestAppointmentViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val addressRepository: IAddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestAppointmentUiState())
    val uiState: StateFlow<RequestAppointmentUiState> = _uiState.asStateFlow()

    private var addressesJob: Job? = null

    fun loadClientAddresses(clientId: String) {
        addressesJob?.cancel()

        addressesJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingAddresses = true,
                errorMessage = null
            )

            try {
                addressRepository.getAddressesByUser(clientId).collect { addresses ->
                    val currentSelected = _uiState.value.selectedAddressId

                    val selectedId = when {
                        currentSelected.isNotBlank() && addresses.any { it.id == currentSelected } -> currentSelected
                        addresses.isNotEmpty() -> addresses.first().id
                        else -> ""
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoadingAddresses = false,
                        savedAddresses = addresses,
                        selectedAddressId = selectedId,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingAddresses = false,
                    savedAddresses = emptyList(),
                    selectedAddressId = "",
                    errorMessage = e.message ?: "No se pudieron cargar las ubicaciones."
                )
            }
        }
    }

    fun selectAddress(addressId: String) {
        _uiState.value = _uiState.value.copy(
            selectedAddressId = addressId
        )
    }

    fun createAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCreating = true,
                isCreated = false,
                createdAppointmentId = null,
                errorMessage = null
            )

            try {
                val appointmentId = appointmentRepository.createAppointment(
                    appointment.copy(status = "pending")
                )

                if (appointmentId.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        isCreated = false,
                        errorMessage = "No se pudo crear la solicitud."
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        isCreated = true,
                        createdAppointmentId = appointmentId,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    isCreated = false,
                    errorMessage = e.message ?: "Ocurrió un error al crear la solicitud."
                )
            }
        }
    }
}