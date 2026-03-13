package com.example.shared.presentation.clientAppointmentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.IBookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClientAppointmentDetailViewModel(
    private val bookingRepository: IBookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientAppointmentDetailUiState(isLoading = false))
    val uiState: StateFlow<ClientAppointmentDetailUiState> = _uiState.asStateFlow()

    fun loadBookingDetail(bookingId: String) {
        viewModelScope.launch {
            _uiState.value = ClientAppointmentDetailUiState(isLoading = true)

            try {
                val booking = bookingRepository.getBookingById(bookingId)

                if (booking == null) {
                    _uiState.value = ClientAppointmentDetailUiState(
                        isLoading = false,
                        errorMessage = "No se encontró la cita"
                    )
                    return@launch
                }

                val cancellationPolicy =
                    bookingRepository.getCancellationPolicyByWorkerId(booking.workerId)

                val worker =
                    bookingRepository.getWorkerProfile(booking.workerId)

                _uiState.value = ClientAppointmentDetailUiState(
                    isLoading = false,
                    booking = booking,
                    worker = worker,
                    cancellationPolicy = cancellationPolicy,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = ClientAppointmentDetailUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar el detalle de la cita"
                )
            }
        }
    }

    fun cancelAppointmentByClient() {
        val booking = _uiState.value.booking ?: return

        if (booking.status != "confirmed") {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Solo se pueden cancelar citas confirmadas"
            )
            return
        }

        viewModelScope.launch {
            try {
                bookingRepository.cancelAppointmentByClient(booking.id)
                loadBookingDetail(booking.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cancelar la cita"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}