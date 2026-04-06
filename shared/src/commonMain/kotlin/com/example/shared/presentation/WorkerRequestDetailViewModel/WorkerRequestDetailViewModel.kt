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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkerRequestDetailViewModel(
    private val repository: IAppointmentRepository,
    private val paymentReceiptRepository: IPaymentReceiptRepository,
    private val notificationsRepository: INotificationsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerRequestDetailUiState())
    val uiState: StateFlow<WorkerRequestDetailUiState> = _uiState.asStateFlow()

    fun loadAppointment(appointmentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
            )

            try {
                val appointment = repository
                    .getAppointmentById(appointmentId)
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Error cargando solicitud",
                        )
                    }
                    .first()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    appointment = appointment,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando solicitud",
                )
            }
        }
    }

    fun acceptRequest() {
        val appointment = _uiState.value.appointment ?: return
        val appointmentId = appointment.id

        viewModelScope.launch {
            repository.approveAppointment(appointmentId)

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
                appointmentId = appointmentId,
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

            loadAppointment(appointmentId)
        }
    }

    fun rejectRequest() {
        val appointment = _uiState.value.appointment ?: return
        val appointmentId = appointment.id

        viewModelScope.launch {
            repository.rejectAppointmentByWorker(appointmentId)

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

            loadAppointment(appointmentId)
        }
    }
}