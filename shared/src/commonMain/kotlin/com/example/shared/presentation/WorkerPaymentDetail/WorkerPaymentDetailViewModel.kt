package com.example.shared.presentation.WorkerPaymentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import com.example.shared.data.repository.notifications.INotificationsRepository
import com.example.shared.domain.entity.NotificationDeepLinks
import com.example.shared.domain.entity.NotificationTypes
import com.example.shared.presentation.notifications.pushNotification
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
    private val notificationsRepository: INotificationsRepository,
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

                notificationsRepository.pushNotification(
                    userId = appointment.clientId,
                    recipientRole = "client",
                    title = "Pago verificado",
                    message = "Tu pago fue verificado y la cita quedó confirmada.",
                    type = NotificationTypes.PAYMENT_VERIFIED,
                    appointmentId = appointment.id,
                    deepLink = NotificationDeepLinks.CLIENT_APPOINTMENT_DETAIL,
                    actorId = appointment.workerId,
                )

                notificationsRepository.pushNotification(
                    userId = appointment.workerId,
                    recipientRole = "worker",
                    title = "Cita confirmada",
                    message = "El pago de ${appointment.clientName} fue verificado y la cita quedó confirmada.",
                    type = NotificationTypes.APPOINTMENT_CONFIRMED,
                    appointmentId = appointment.id,
                    deepLink = NotificationDeepLinks.WORKER_DAILY_APPOINTMENTS,
                    actorId = appointment.clientId,
                )

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

                notificationsRepository.pushNotification(
                    userId = appointment.clientId,
                    recipientRole = "client",
                    title = "Problema con el pago",
                    message = "${appointment.workerName} reportó un problema con tu comprobante de pago.",
                    type = NotificationTypes.PAYMENT_ISSUE,
                    appointmentId = appointment.id,
                    deepLink = NotificationDeepLinks.CLIENT_REQUESTS,
                    actorId = appointment.workerId,
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
