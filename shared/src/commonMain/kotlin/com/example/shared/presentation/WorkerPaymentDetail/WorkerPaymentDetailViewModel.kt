package com.example.shared.presentation.WorkerPaymentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
class WorkerPaymentDetailViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val paymentReceiptRepository: IPaymentReceiptRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(WorkerPaymentDetailUiState())
  val uiState: StateFlow<WorkerPaymentDetailUiState> = _uiState.asStateFlow()

  fun loadPaymentDetail(appointmentId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

      try {
        val appointment = appointmentRepository.getAppointmentById(appointmentId).first()

        val paymentReceipt = paymentReceiptRepository.getReceiptByAppointment(appointmentId).first()

        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                appointment = appointment,
                paymentReceipt = paymentReceipt,
                errorMessage = null,
            )
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                appointment = null,
                paymentReceipt = null,
                errorMessage = e.message ?: "Error cargando detalle de pago",
            )
      }
    }
  }

  fun verifyPayment() {
    val appointment = _uiState.value.appointment ?: return
    val receipt = _uiState.value.paymentReceipt ?: return

    viewModelScope.launch {
      try {
        val now = currentDateTimeString()

        paymentReceiptRepository.updateReceiptStatus(
            appointmentId = appointment.id,
            receiptId = receipt.id,
            status = "APPROVED",
            note = null,
            reviewedAt = now,
            reviewedBy = appointment.workerId,
            rejectionReason = null,
        )

        appointmentRepository.confirmPayment(appointment.id)

        _uiState.value = _uiState.value.copy(paymentVerified = true, errorMessage = null)
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Error verificando pago")
      }
    }
  }

  fun reportProblem() {
    val appointment = _uiState.value.appointment ?: return
    val receipt = _uiState.value.paymentReceipt ?: return

    viewModelScope.launch {
      try {
        val now = currentDateTimeString()

        paymentReceiptRepository.updateReceiptStatus(
            appointmentId = appointment.id,
            receiptId = receipt.id,
            status = "REJECTED",
            note = "Problema con comprobante",
            reviewedAt = now,
            reviewedBy = appointment.workerId,
            rejectionReason = "Problema con comprobante",
        )
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(errorMessage = e.message ?: "Error reportando problema con pago")
      }
    }
  }

  private fun currentDateTimeString(): String {
    val now = Clock.System.now()
    val local = now.toLocalDateTime(TimeZone.of("America/Costa_Rica"))

    val year = local.year.toString().padStart(4, '0')
    val month = local.monthNumber.toString().padStart(2, '0')
    val day = local.dayOfMonth.toString().padStart(2, '0')
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')

    return "$year-$month-$day" + "T" + "$hour:$minute"
  }
}
