package com.example.seviya.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seviya.theme.SoftBlueSurface
import com.example.seviya.theme.SoftSurface
import com.example.seviya.theme.TextSecondaryAlt
import com.example.seviya.theme.White
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt
import com.example.shared.domain.entity.ReviewMeta
import com.example.shared.presentation.cancellation.AppointmentCancellationPreview
import com.example.shared.presentation.workerAppointmentDetail.WorkerAppointmentDetailUiState
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.DotsVertical
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Tool
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun WorkerAppointmentDetailScreen(
    appointment: Appointment,
    uiState: WorkerAppointmentDetailUiState = WorkerAppointmentDetailUiState(),
    paymentReceipt: PaymentReceipt? = null,
    reviewMeta: ReviewMeta = ReviewMeta(),
    onBack: () -> Unit = {},
    onOpenGoogleMaps: () -> Unit = {},
    onOpenWaze: () -> Unit = {},
    onVerifyPayment: () -> Unit = {},
    onStartAppointment: () -> Unit = {},
    onFinishAppointment: () -> Unit = {},
    onRateClient: () -> Unit = {},
    onRequestCancellationPreview: () -> Unit = {},
    onCancelAppointment: () -> Unit = {},
    onDismissCancellationPreview: () -> Unit = {},
    onGoServices: () -> Unit = {},
    onGoMap: () -> Unit = {},
    onGoSearch: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    val errorMessageToShow = uiState.errorMessage
    val successMessageToShow = uiState.successMessage
    val cancellationPreviewToShow = uiState.cancellationPreview
    val showCancellationPreview = uiState.showCancellationPreview
    val isCancellingAppointment = uiState.isCancellingAppointment

    Scaffold(
        containerColor = Color(0xFFF3F5F8),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F5F8))
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            DetailHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (errorMessageToShow != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFFFFF3F3),
                        border = BorderStroke(1.dp, Color(0xFFF4D0D0))
                    ) {
                        Text(
                            text = errorMessageToShow,
                            color = Color(0xFFE54848),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                if (successMessageToShow != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFFEAF7EE),
                        border = BorderStroke(1.dp, Color(0xFFCFE8D6))
                    ) {
                        Text(
                            text = successMessageToShow,
                            color = Color(0xFF177245),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                StepCard(
                    step = "1",
                    title = "REVISAR DATOS DE CITA"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = appointment.services.firstOrNull()?.name ?: "Servicio",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color(0xFF18233A),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }

                        StatusChip(status = appointment.status)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    DetailInfoRow(
                        label = "CLIENTE",
                        value = appointment.clientName,
                        icon = TablerIcons.Tool
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DetailInfoRow(
                        label = "FECHA Y HORA",
                        value = "${extractDateOnlyDetail(appointment.serviceStartAt)} • ${extractTimeFromDateTimeDetail(appointment.serviceStartAt)}",
                        icon = TablerIcons.CalendarEvent
                    )
                }

                StepCard(
                    step = "2",
                    title = "PLANIFICAR RUTA"
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = SoftSurface,
                        border = BorderStroke(1.dp, Color(0xFFE4E8EF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        color = Color(0xFF0A4DB3),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = TablerIcons.MapPin,
                                    contentDescription = null,
                                    tint = White
                                )
                            }

                            Spacer(modifier = Modifier.size(14.dp))

                            Column {
                                Text(
                                    text = "${appointment.location.alias}, ${appointment.location.province}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF18233A)
                                    )
                                )
                                Text(
                                    text = "${appointment.location.district} • ${appointment.location.reference}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color(0xFF8A97AB)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        RouteButton(
                            text = "Google Maps",
                            onClick = {
                                val lat = appointment.location.latitude
                                val lng = appointment.location.longitude
                                uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                            },
                            modifier = Modifier.weight(1f)
                        )

                        RouteButton(
                            text = "Abrir en Waze",
                            onClick = {
                                val lat = appointment.location.latitude
                                val lng = appointment.location.longitude
                                uriHandler.openUri("https://waze.com/ul?ll=$lat,$lng&navigate=yes")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (canShowPaymentReceipt(paymentReceipt)) {
                    StepCard(
                        step = "3",
                        title = "VERIFICAR PAGO SINPE"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Comprobante enviado",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF18233A)
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isPaymentVerified(appointment, paymentReceipt))
                                    Color(0xFFE4F7EC)
                                else
                                    Color(0xFFFCE9E9)
                            ) {
                                Text(
                                    text = if (isPaymentVerified(appointment, paymentReceipt))
                                        "VERIFICADO"
                                    else
                                        "PENDIENTE",
                                    color = if (isPaymentVerified(appointment, paymentReceipt))
                                        Color(0xFF18A55B)
                                    else
                                        Color(0xFFE54848),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xFFF8FAFD),
                            border = BorderStroke(1.dp, Color(0xFFE4E8EF))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                KamelImage(
                                    resource = asyncPainterResource(data = paymentReceipt?.imageUrl.orEmpty()),
                                    contentDescription = "Comprobante SINPE",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(18.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                if (canVerifyPayment(appointment, paymentReceipt)) {
                                    Button(
                                        onClick = onVerifyPayment,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF0A4DB3)
                                        ),
                                        shape = RoundedCornerShape(18.dp)
                                    ) {
                                        Text("Confirmar pago")
                                    }
                                }
                            }
                        }
                    }
                }

                if (canStartAppointment(appointment)) {
                    PrimaryActionButton(
                        text = "Iniciar cita",
                        onClick = onStartAppointment
                    )
                }

                if (canFinishAppointment(appointment)) {
                    PrimaryActionButton(
                        text = "Completar cita",
                        onClick = onFinishAppointment
                    )
                }

                if (canRateClient(appointment, reviewMeta)) {
                    PrimaryActionButton(
                        text = "Calificar cliente",
                        onClick = onRateClient
                    )
                }

                if (canCancelAppointment(appointment)) {
                    OutlinedButton(
                        onClick = onRequestCancellationPreview,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        enabled = !uiState.isPreparingCancellationPreview && !uiState.isCancellingAppointment
                    ) {
                        Text(
                            if (uiState.isPreparingCancellationPreview) {
                                "Calculando reembolso..."
                            } else {
                                "Cancelar cita"
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCancellationPreview && cancellationPreviewToShow != null) {
        WorkerCancellationPreviewDialog(
            preview = cancellationPreviewToShow,
            confirmText = if (isCancellingAppointment) "Cancelando..." else "Confirmar cancelación",
            confirmEnabled = !isCancellingAppointment,
            onDismiss = onDismissCancellationPreview,
            onConfirm = onCancelAppointment
        )
    }
}

@Composable
private fun DetailHeader(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(Color(0xFF0A4DB3))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            CircleIconButton(
                icon = TablerIcons.ArrowLeft,
                onClick = onBack
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Guía Pre-Servicio",
                    color = White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = "PREPARACIÓN DETALLADA",
                    color = White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            CircleIconButton(
                icon = TablerIcons.DotsVertical,
                onClick = {}
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .background(
                color = White.copy(alpha = 0.18f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = White
        )
    }
}

@Composable
private fun StepCard(
    step: String,
    title: String,
    content: @Composable () -> Unit
) {
    Box {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = White,
            border = BorderStroke(1.dp, Color(0xFFE4E8EF)),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF0A4DB3),
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                content()
            }
        }

        Surface(
            modifier = Modifier
                .padding(start = 6.dp, top = 4.dp)
                .size(34.dp),
            shape = CircleShape,
            color = Color(0xFF0A4DB3),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = step,
                    color = White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val text: String
    val background: Color
    val textColor: Color

    when (status.lowercase()) {
        "payment_pending" -> {
            text = "PAGO PENDIENTE"
            background = Color(0xFFFFF1E6)
            textColor = Color(0xFFFF8C00)
        }

        "approved" -> {
            text = "APROBADA"
            background = Color(0xFFE8F0FF)
            textColor = Color(0xFF0A4DB3)
        }

        "confirmed" -> {
            text = "CONFIRMADA"
            background = Color(0xFFE6F7EC)
            textColor = Color(0xFF1F9D55)
        }

        "in_progress" -> {
            text = "EN PROGRESO"
            background = Color(0xFFFFF6D6)
            textColor = Color(0xFFD97706)
        }

        "completed" -> {
            text = "FINALIZADA"
            background = Color(0xFFEAF2FF)
            textColor = Color(0xFF0A4DB3)
        }

        "cancelled" -> {
            text = "CANCELADA"
            background = Color(0xFFFCE9E9)
            textColor = Color(0xFFE54848)
        }

        "rejected" -> {
            text = "RECHAZADA"
            background = Color(0xFFFCE9E9)
            textColor = Color(0xFFE54848)
        }

        else -> {
            text = status.uppercase()
            background = Color(0xFFEDEFF3)
            textColor = TextSecondaryAlt
        }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = background
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF2F5F8)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF8A97AB),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF8A97AB),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFF18233A),
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

@Composable
private fun RouteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(128.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = SoftBlueSurface,
        border = BorderStroke(1.dp, Color(0xFFC9DAF8))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color(0xFF0A4DB3),
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0A4DB3)
        )
    ) {
        Text(text)
    }
}

@Composable
private fun WorkerCancellationPreviewDialog(
    preview: AppointmentCancellationPreview,
    confirmText: String,
    confirmEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val hasRefund = preview.refundAmount > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = RoundedCornerShape(30.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Confirmar cancelación",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF18233A)
                    )
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFE9E9)
                ) {
                    Text(
                        text = "Reembolso ${preview.refundPercentage}%",
                        color = Color(0xFFE54848),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFFFF4F4),
                    border = BorderStroke(1.dp, Color(0xFFF4D0D0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (hasRefund) "Monto estimado a reembolsar" else "Resultado de la cancelación",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color(0xFFE54848),
                                fontWeight = FontWeight.ExtraBold
                            )
                        )

                        Text(
                            text = if (hasRefund) {
                                formatWorkerCurrency(preview.refundAmount)
                            } else {
                                "No aplica reembolso"
                            },
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color(0xFFE54848),
                                fontWeight = FontWeight.ExtraBold
                            )
                        )

                        Text(
                            text = "El cliente recibirá el resultado según esta cancelación.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF8A97AB),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFFF8FAFD),
                    border = BorderStroke(1.dp, Color(0xFFE4E8EF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WorkerCancellationDetailRow(
                            label = "Cancela",
                            value = "Trabajador"
                        )

                        WorkerCancellationDetailRow(
                            label = "Motivo aplicado",
                            value = preview.policyLabel
                        )

                        WorkerCancellationDetailRow(
                            label = "Total de la cita",
                            value = formatWorkerCurrency(preview.appointmentTotal)
                        )

                        WorkerCancellationDetailRow(
                            label = "Monto no reembolsable",
                            value = formatWorkerCurrency(preview.nonRefundableAmount),
                            valueColor = if (preview.nonRefundableAmount > 0) Color(0xFFE54848) else Color(0xFF18233A)
                        )
                    }
                }

                if (hasRefund && preview.warningMessage.isNotBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFF8E8),
                        border = BorderStroke(1.dp, Color(0xFFF2D693))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Importante",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFF8C6500),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )

                            Text(
                                text = preview.warningMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF8C6500),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = confirmEnabled
            ) {
                Text(
                    text = confirmText,
                    color = if (confirmEnabled) Color(0xFFE54848) else Color(0xFFE3A5A5),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Volver",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    )
}

@Composable
private fun WorkerCancellationDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF18233A)
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF8A97AB),
                fontWeight = FontWeight.ExtraBold
            )
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = valueColor,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun WorkerPreviewLine(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF8A97AB),
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF18233A),
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

private fun canShowPaymentReceipt(paymentReceipt: PaymentReceipt?): Boolean {
    return paymentReceipt?.imageUrl?.isNotBlank() == true
}

private fun canVerifyPayment(
    appointment: Appointment,
    paymentReceipt: PaymentReceipt?
): Boolean {
    return appointment.status.equals("payment_pending", ignoreCase = true) &&
            paymentReceipt?.imageUrl?.isNotBlank() == true &&
            !paymentReceipt.status.equals("APPROVED", ignoreCase = true)
}

private fun canStartAppointment(appointment: Appointment): Boolean {
    return appointment.status.equals("confirmed", ignoreCase = true)
}

private fun canFinishAppointment(appointment: Appointment): Boolean {
    return appointment.status.equals("in_progress", ignoreCase = true)
}

private fun canRateClient(
    appointment: Appointment,
    reviewMeta: ReviewMeta
): Boolean {
    return appointment.status.equals("completed", ignoreCase = true) &&
            !reviewMeta.workerToClientCreated
}

private fun canCancelAppointment(appointment: Appointment): Boolean {
    return appointment.status.equals("confirmed", ignoreCase = true)
}

private fun isPaymentVerified(
    appointment: Appointment,
    paymentReceipt: PaymentReceipt?
): Boolean {
    return paymentReceipt?.status.equals("APPROVED", ignoreCase = true) ||
            appointment.status.equals("confirmed", ignoreCase = true) ||
            appointment.status.equals("in_progress", ignoreCase = true) ||
            appointment.status.equals("completed", ignoreCase = true)
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

private fun formatWorkerCurrency(amount: Int): String {
    val raw = amount.coerceAtLeast(0).toString()
    val reversed = raw.reversed()
    val grouped = reversed.chunked(3).joinToString(".")
    return "₡ ${grouped.reversed()}"
}