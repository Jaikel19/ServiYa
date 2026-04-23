package com.example.seviya.feature.worker

import androidx.compose.runtime.Composable
import com.example.seviya.feature.shared.NotificationCenterScreen
import com.example.shared.domain.entity.AppNotification
import com.example.shared.domain.entity.NotificationDeepLinks
import com.example.shared.domain.entity.NotificationTypes

@Composable
fun WorkerAlertsScreen(
    workerId: String,
    onBack: () -> Unit,
    onOpenRequestDetail: (String) -> Unit,
    onOpenPaymentDetail: (String) -> Unit,
    onOpenRequests: () -> Unit,
    onOpenDailyAppointments: (String) -> Unit,
    onOpenAppointmentDetail: (String) -> Unit,
    onOpenProfessionalProfile: (String) -> Unit,
) {
    NotificationCenterScreen(
        userId = workerId,
        title = "Alertas",
        emptyTitle = "Sin alertas",
        emptySubtitle = "Aquí verás solicitudes, pagos, citas y recordatorios.",
        onBack = onBack,
        onNotificationClick = { notification ->
            handleWorkerNotification(
                workerId = workerId,
                notification = notification,
                onOpenRequestDetail = onOpenRequestDetail,
                onOpenPaymentDetail = onOpenPaymentDetail,
                onOpenRequests = onOpenRequests,
                onOpenDailyAppointments = onOpenDailyAppointments,
                onOpenAppointmentDetail = onOpenAppointmentDetail,
                onOpenProfessionalProfile = onOpenProfessionalProfile,
            )
        },
    )
}

private fun handleWorkerNotification(
    workerId: String,
    notification: AppNotification,
    onOpenRequestDetail: (String) -> Unit,
    onOpenPaymentDetail: (String) -> Unit,
    onOpenRequests: () -> Unit,
    onOpenDailyAppointments: (String) -> Unit,
    onOpenAppointmentDetail: (String) -> Unit,
    onOpenProfessionalProfile: (String) -> Unit,
) {
    when (notification.type) {
        NotificationTypes.NEW_REQUEST_RECEIVED -> {
            notification.appointmentId?.let(onOpenRequestDetail) ?: onOpenRequests()
        }

        NotificationTypes.PAYMENT_RECEIPT_UPLOADED,
        NotificationTypes.PAYMENT_PENDING,
        NotificationTypes.PAYMENT_ISSUE,
        NotificationTypes.PAYMENT_VERIFIED -> {
            notification.appointmentId?.let(onOpenPaymentDetail) ?: onOpenRequests()
        }

        NotificationTypes.APPOINTMENT_CONFIRMED,
        NotificationTypes.APPOINTMENT_REMINDER_24H,
        NotificationTypes.APPOINTMENT_REMINDER_2H,
        NotificationTypes.APPOINTMENT_STARTED,
        NotificationTypes.APPOINTMENT_COMPLETED,
        NotificationTypes.APPOINTMENT_CANCELLED -> {
            notification.appointmentId?.let(onOpenAppointmentDetail)
                ?: onOpenDailyAppointments(workerId)
        }

        NotificationTypes.REVIEW_RECEIVED -> {
            // Al trabajador le hicieron una reseña:
            // abre su perfil profesional.
            onOpenProfessionalProfile(workerId)
        }

        NotificationTypes.REVIEW_PENDING_WORKER -> {
            // Reseña hacia el cliente: no redirige.
            Unit
        }

        NotificationTypes.REVIEW_SUBMITTED_SUCCESS -> {
            // Si venía del trabajador calificando al cliente,
            // no redirige a ninguna pantalla.
            Unit
        }

        NotificationTypes.REQUEST_ACCEPTED,
        NotificationTypes.REQUEST_REJECTED -> {
            notification.appointmentId?.let(onOpenRequestDetail) ?: onOpenRequests()
        }

        else -> {
            when (notification.deepLink) {
                NotificationDeepLinks.WORKER_REQUEST_DETAIL -> {
                    notification.appointmentId?.let(onOpenRequestDetail) ?: onOpenRequests()
                }

                NotificationDeepLinks.WORKER_PAYMENT_DETAIL -> {
                    notification.appointmentId?.let(onOpenPaymentDetail) ?: onOpenRequests()
                }

                NotificationDeepLinks.WORKER_REQUESTS -> onOpenRequests()

                NotificationDeepLinks.WORKER_DAILY_APPOINTMENTS -> {
                    onOpenDailyAppointments(workerId)
                }

                else -> onOpenRequests()
            }
        }
    }
}