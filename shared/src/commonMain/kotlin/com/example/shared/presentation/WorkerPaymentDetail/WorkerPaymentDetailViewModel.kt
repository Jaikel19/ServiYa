package com.example.shared.presentation.WorkerPaymentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkerPaymentDetailViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val paymentReceiptRepository: IPaymentReceiptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerPaymentDetailUiState())
    val uiState: StateFlow<WorkerPaymentDetailUiState> = _uiState.asStateFlow()

    fun loadPaymentDetail(appointmentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val appointment = appointmentRepository
                    .getAppointmentById(appointmentId)
                    .first()

                val paymentReceipt = paymentReceiptRepository
                    .getReceiptByAppointment(appointmentId)
                    .first()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    appointment = appointment,
                    paymentReceipt = paymentReceipt,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    appointment = null,
                    paymentReceipt = null,
                    errorMessage = e.message ?: "Error cargando detalle de pago"
                )
            }
        }
    }

    fun verifyPayment() {
        val appointment = _uiState.value.appointment ?: return
        val receipt = _uiState.value.paymentReceipt ?: return

        viewModelScope.launch {
            try {
                paymentReceiptRepository.updateReceiptStatus(
                    appointmentId = appointment.id,
                    receiptId = receipt.id,
                    status = "APPROVED",
                    note = null
                )

                appointmentRepository.confirmPayment(appointment.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error verificando pago"
                )
            }
        }
    }

    fun reportProblem() {
        val appointment = _uiState.value.appointment ?: return
        val receipt = _uiState.value.paymentReceipt ?: return

        viewModelScope.launch {
            try {
                paymentReceiptRepository.updateReceiptStatus(
                    appointmentId = appointment.id,
                    receiptId = receipt.id,
                    status = "REJECTED",
                    note = "Problema con comprobante"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error reportando problema con pago"
                )
            }
        }
    }
}