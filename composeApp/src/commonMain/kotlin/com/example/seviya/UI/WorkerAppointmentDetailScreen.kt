package com.example.seviya.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.domain.entity.Booking

@Composable
fun WorkerAppointmentDetailScreen(
    booking: Booking,
    onBack: () -> Unit = {},
    onOpenMaps: () -> Unit = {},
    onVerifyPayment: () -> Unit = {},
    onStartAppointment: () -> Unit = {},
    onFinishAppointment: () -> Unit = {},
    onRateClient: () -> Unit = {},
    onCancelAppointment: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }

        Text(
            text = "Detalle de cita",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Cliente: ${booking.clientName}")
        Text("Servicio: ${booking.services.firstOrNull()?.name ?: "Sin servicio"}")
        Text("Fecha: ${extractDateOnlyDetail(booking.date)}")
        Text("Hora: ${extractTimeFromDateTimeDetail(booking.date)}")
        Text("Estado: ${booking.status}")

        Text(
            text = "Ubicación: ${booking.location.alias}, ${booking.location.district}, ${booking.location.province}"
        )

        OutlinedButton(
            onClick = onOpenMaps,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abrir en Google Maps / Waze")
        }

        if (canShowPaymentReceipt(booking)) {
            Text("Comprobante SINPE disponible")

            OutlinedButton(
                onClick = onVerifyPayment,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verificar pago")
            }
        }

        if (canStartAppointment(booking)) {
            Button(
                onClick = onStartAppointment,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar cita")
            }
        }

        if (canFinishAppointment(booking)) {
            Button(
                onClick = onFinishAppointment,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar cita")
            }
        }

        if (canRateClient(booking)) {
            Button(
                onClick = onRateClient,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calificar cliente")
            }
        }

        if (canCancelAppointment(booking)) {
            OutlinedButton(
                onClick = onCancelAppointment,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar cita")
            }
        }
    }
}

private fun canShowPaymentReceipt(booking: Booking): Boolean {
    return booking.paymentReceiptUrl.isNotBlank()
}

private fun canStartAppointment(booking: Booking): Boolean {
    return booking.status.equals("confirmed", ignoreCase = true)
}

private fun canFinishAppointment(booking: Booking): Boolean {
    return booking.status.equals("in_progress", ignoreCase = true)
}

private fun canRateClient(booking: Booking): Boolean {
    return booking.status.equals("completed", ignoreCase = true) &&
            !booking.ratingToClientDone
}

private fun canCancelAppointment(booking: Booking): Boolean {
    return booking.status.equals("confirmed", ignoreCase = true)
}

private fun extractDateOnlyDetail(dateTime: String): String {
    return when {
        dateTime.contains("T") -> dateTime.substringBefore("T")
        dateTime.contains(" ") -> dateTime.substringBefore(" ")
        else -> dateTime
    }
}

private fun extractTimeFromDateTimeDetail(dateTime: String): String {
    return when {
        dateTime.contains("T") -> dateTime.substringAfter("T").take(5)
        dateTime.contains(" ") -> dateTime.substringAfter(" ").take(5)
        else -> ""
    }
}