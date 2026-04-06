package com.example.shared.presentation.WorkerRequestDetailViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import com.example.shared.data.repository.notifications.INotificationsRepository
import com.example.shared.domain.entity.NotificationDeepLinks
import com.example.shared.domain.entity.NotificationTypes
import com.example.shared.domain.entity.PaymentReceipt
import com.example.shared.presentation.notifications.pushNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WorkerRequestDetailViewModel(
    private val repository: IAppointmentRepository,
    private val paymentReceiptRepository: IPaymentReceiptRepository,
    private val notificationsRepository: INotificationsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerRequestDetailUiState())
    val uiState: StateFlow<WorkerRequestDetailUiState> = _uiState.asStateFlow()

    fun loadBooking(appointmentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                requestHandled = false,
            )

            repository
                .getAppointmentById(appointmentId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        appointment = null,
                        errorMessage = e.message ?: "Error al cargar la solicitud",
                    )
                }
                .collect { appointment ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        appointment = appointment,
                        errorMessage = null,
                    )
                }
        }
    }

    fun acceptRequest() {
        val appointment = _uiState.value.appointment ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                actionInProgress = true,
                errorMessage = null,
                requestHandled = false,
            )

            try {
                repository.approveAppointment(appointment.id)

                val receipt = PaymentReceipt(
                    id = "",
                    attemptNumber = 0L,
                    imageUrl = "",
                    note = null,
                    sentAt = "",
                    reviewedAt = null,
                    reviewedBy = null,
                    rejectionReason = null,
                    status = "pending",
                )

                paymentReceiptRepository.createReceipt(
                    appointmentId = appointment.id,
                    receipt = receipt,
                )

                notificationsRepository.pushNotification(
                    userId = appointment.clientId,
                    recipientRole = "client",
                    title = "Solicitud aceptada",
                    message = "${appointment.workerName} aceptó tu solicitud de cita.",
                    type = NotificationTypes.REQUEST_ACCEPTED,
                    appointmentId = appointment.id,
                    deepLink = NotificationDeepLinks.CLIENT_PAYMENT_UPLOAD,
                    actorId = appointment.workerId,
                )

                notificationsRepository.pushNotification(
                    userId = appointment.clientId,
                    recipientRole = "client",
                    title = "Pago pendiente",
                    message = "Tu solicitud fue aceptada. Ahora debes subir el comprobante SINPE.",
                    type = NotificationTypes.PAYMENT_PENDING,
                    appointmentId = appointment.id,
                    deepLink = NotificationDeepLinks.CLIENT_PAYMENT_UPLOAD,
                    actorId = appointment.workerId,
                )

                _uiState.value = _uiState.value.copy(
                    actionInProgress = false,
                    requestHandled = true,
                    errorMessage = null,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    actionInProgress = false,
                    requestHandled = false,
                    errorMessage = e.message ?: "No se pudo aceptar la solicitud",
                )
            }
        }
    }

    fun rejectRequest() {
        val appointment = _uiState.value.appointment ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                actionInProgress = true,
                errorMessage = null,
                requestHandled = false,
            )

            try {
                repository.rejectAppointmentByWorker(appointment.id)

                notificationsRepository.pushNotification(
                    userId = appointment.clientId,
                    recipientRole = "client",
                    title = "Solicitud rechazada",
                    message = "${appointment.workerName} rechazó tu solicitud de cita.",
                    type = NotificationTypes.REQUEST_REJECTED,
                    appointmentId = appointment.id,
                    deepLink = NotificationDeepLinks.CLIENT_REQUESTS,
                    actorId = appointment.workerId,
                )

                _uiState.value = _uiState.value.copy(
                    actionInProgress = false,
                    requestHandled = true,
                    errorMessage = null,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    actionInProgress = false,
                    requestHandled = false,
                    errorMessage = e.message ?: "No se pudo rechazar la solicitud",
                )
            }
        }
    }
}