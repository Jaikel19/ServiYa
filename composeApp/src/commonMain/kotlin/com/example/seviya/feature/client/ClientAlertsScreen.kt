package com.example.seviya.feature.client

import androidx.compose.runtime.Composable
import com.example.seviya.feature.shared.NotificationCenterScreen
import com.example.shared.domain.entity.AppNotification
import com.example.shared.domain.entity.NotificationDeepLinks
import com.example.shared.domain.entity.NotificationTypes

@Composable
fun ClientAlertsScreen(
    clientId: String,
    onBack: () -> Unit,
    onOpenAppointmentDetail: (String) -> Unit,
    onOpenRequests: () -> Unit,
    onOpenPaymentUpload: (String) -> Unit,
) {
    NotificationCenterScreen(
        userId = clientId,
        title = "Alertas",
        emptyTitle = "Sin alertas",
        emptySubtitle = "Aquí verás solicitudes, pagos, citas y recordatorios.",
        onBack = onBack,
        onNotificationClick = { notification ->
            handleClientNotification(
                notification = notification,
                onOpenAppointmentDetail = onOpenAppointmentDetail,
                onOpenRequests = onOpenRequests,
                onOpenPaymentUpload = onOpenPaymentUpload,
            )
        },
    )
}

private fun handleClientNotification(
    notification: AppNotification,
    onOpenAppointmentDetail: (String) -> Unit,
    onOpenRequests: () -> Unit,
    onOpenPaymentUpload: (String) -> Unit,
) {
    when (notification.type) {
        NotificationTypes.REQUEST_ACCEPTED,
        NotificationTypes.PAYMENT_PENDING -> {
            notification.appointmentId?.let(onOpenPaymentUpload) ?: onOpenRequests()
        }

        NotificationTypes.REQUEST_REJECTED -> {
            notification.appointmentId?.let(onOpenAppointmentDetail) ?: onOpenRequests()
        }

        NotificationTypes.PAYMENT_ISSUE -> {
            notification.appointmentId?.let(onOpenPaymentUpload) ?: onOpenRequests()
        }

        NotificationTypes.PAYMENT_VERIFIED,
        NotificationTypes.APPOINTMENT_CONFIRMED,
        NotificationTypes.APPOINTMENT_REMINDER_24H,
        NotificationTypes.APPOINTMENT_REMINDER_2H,
        NotificationTypes.APPOINTMENT_STARTED,
        NotificationTypes.APPOINTMENT_COMPLETED,
        NotificationTypes.APPOINTMENT_CANCELLED,
        NotificationTypes.REVIEW_PENDING_CLIENT,
        NotificationTypes.REVIEW_SUBMITTED_SUCCESS -> {
            notification.appointmentId?.let(onOpenAppointmentDetail) ?: onOpenRequests()
        }

        NotificationTypes.REVIEW_RECEIVED -> {
            // Reseña hacia el cliente: no redirige a ninguna pantalla.
            Unit
        }

        else -> {
            when (notification.deepLink) {
                NotificationDeepLinks.CLIENT_APPOINTMENT_DETAIL -> {
                    notification.appointmentId?.let(onOpenAppointmentDetail) ?: onOpenRequests()
                }

                NotificationDeepLinks.CLIENT_PAYMENT_UPLOAD -> {
                    notification.appointmentId?.let(onOpenPaymentUpload) ?: onOpenRequests()
                }

                NotificationDeepLinks.CLIENT_REQUESTS -> onOpenRequests()

                else -> onOpenRequests()
            }
        }
    }
}