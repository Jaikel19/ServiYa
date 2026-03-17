package com.example.shared.presentation.WorkerPaymentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.IBookingRepository
import com.example.shared.domain.entity.Booking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class WorkerPaymentDetailViewModel(
    private val repository: IBookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerPaymentDetailUiState())
    val uiState: StateFlow<WorkerPaymentDetailUiState> = _uiState.asStateFlow()

    fun loadBooking(bookingId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val booking = repository.getBookingById(bookingId)
                _uiState.value = _uiState.value.copy(isLoading = false, booking = booking)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando pago"
                )
            }
        }
    }

    fun verifyPayment() {
        val bookingId = _uiState.value.booking?.id ?: return
        viewModelScope.launch {
            repository.confirmPayment(bookingId)
        }
    }

    fun reportProblem() {
        val bookingId = _uiState.value.booking?.id ?: return
        viewModelScope.launch {
            repository.cancelAppointmentByWorker(bookingId)
        }
    }
}