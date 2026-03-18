package com.example.shared.presentation.WorkerRequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkerRequestsViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val paymentReceiptRepository: IPaymentReceiptRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WorkerRequestsUiState())
    val uiState: StateFlow<WorkerRequestsUiState> = _state.asStateFlow()

    fun loadRequests(workerId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            appointmentRepository.getAppointmentsByWorker(workerId)
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error fetching requests"
                    )
                }
                .collectLatest { appointments ->
                    val pending = appointments.filter { it.status == "pending" }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        requests = pending
                    )
                }
        }
    }

    fun loadPaymentPending(workerId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingPayments = true)

            try {
                val appointments = appointmentRepository
                    .getAppointmentsByWorker(workerId)
                    .first()
                    .filter { it.status == "approved" }

                val pairs = mutableListOf<Pair<Appointment, PaymentReceipt>>()

                for (appointment in appointments) {
                    try {
                        val receipt = paymentReceiptRepository
                            .getReceiptByAppointment(appointment.id)
                            .first()

                        if (receipt?.status == "payment_pending") {
                            pairs.add(Pair(appointment, receipt))
                        }
                    } catch (e: Exception) {
                        println("ERROR loading receipt for ${appointment.id}: ${e.message}")
                    }
                }

                _state.value = _state.value.copy(
                    isLoadingPayments = false,
                    paymentPendingAppointments = pairs
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingPayments = false,
                    errorMessage = e.message ?: "Error fetching payment pending"
                )
            }
        }
    }

    fun acceptRequest(appointment: Appointment) {
        viewModelScope.launch {
            appointmentRepository.approveAppointment(appointment.id)
        }
    }

    fun rejectRequest(appointment: Appointment) {
        viewModelScope.launch {
            appointmentRepository.rejectAppointmentByWorker(appointment.id)
        }
    }

    fun confirmPayment(appointment: Appointment) {
        viewModelScope.launch {
            appointmentRepository.confirmPayment(appointment.id)
        }
    }

    fun cancelPayment(appointment: Appointment) {
        viewModelScope.launch {
            appointmentRepository.cancelAppointmentByWorker(appointment.id)
        }
    }
}