package com.example.shared.presentation.WorkerRequest

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

class WorkerRequestsViewModel(
    private val requestRepository: IBookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerRequestsUiState())
    val uiState: StateFlow<WorkerRequestsUiState> = _uiState.asStateFlow()

    fun loadRequests(workerId: String) {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            requestRepository.getBookingsByWorker(workerId)
                .onEach { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        requests = list
                    )
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error fetching requests"
                    )
                }
                .collect()
        }
    }

    fun acceptRequest(booking: Booking) {
        viewModelScope.launch {
            requestRepository.approvedAppointment(booking.id)//→aproved
        }
    }

    fun rejectRequest(booking: Booking) {
        viewModelScope.launch {
            requestRepository.rejectAppointmentByWorker(booking.id) // → "reject"
        }
    }
    fun confirmPayment(booking: Booking) {
        viewModelScope.launch {
            requestRepository.confirmPayment(booking.id) // → "confirmed"
        }
    }

    fun cancelPayment(booking: Booking) {
        viewModelScope.launch {
            requestRepository.cancelAppointmentByWorker(booking.id) // → "cancelled"
        }
    }
}
