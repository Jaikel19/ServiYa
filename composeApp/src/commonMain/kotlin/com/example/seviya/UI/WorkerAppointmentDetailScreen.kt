package com.example.seviya.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.seviya.theme.*
import com.example.shared.domain.entity.Booking
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
    booking: Booking,
    onBack: () -> Unit = {},
    onOpenGoogleMaps: () -> Unit = {},
    onOpenWaze: () -> Unit = {},
    onVerifyPayment: () -> Unit = {},
    onStartAppointment: () -> Unit = {},
    onFinishAppointment: () -> Unit = {},
    onRateClient: () -> Unit = {},
    onCancelAppointment: () -> Unit = {},
    onGoServices: () -> Unit = {},
    onGoMap: () -> Unit = {},
    onGoSearch: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

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
                                text = booking.services.firstOrNull()?.name ?: "Servicio",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color(0xFF18233A),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }

                        StatusChip(status = booking.status)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    DetailInfoRow(
                        label = "CLIENTE",
                        value = booking.clientName,
                        icon = TablerIcons.Tool
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DetailInfoRow(
                        label = "FECHA Y HORA",
                        value = "${extractDateOnlyDetail(booking.date)} • ${extractTimeFromDateTimeDetail(booking.date)}",
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
                                    text = "${booking.location.alias}, ${booking.location.province}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF18233A)
                                    )
                                )
                                Text(
                                    text = "${booking.location.district} • ${booking.location.reference}",
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
                                val lat = booking.location.latitude
                                val lng = booking.location.longitude
                                uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                            },
                            modifier = Modifier.weight(1f)
                        )

                        RouteButton(
                            text = "Abrir en Waze",
                            onClick = {
                                val lat = booking.location.latitude
                                val lng = booking.location.longitude
                                uriHandler.openUri("https://waze.com/ul?ll=$lat,$lng&navigate=yes")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (canShowPaymentReceipt(booking)) {
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
                                color = if (isPaymentVerified(booking))
                                    Color(0xFFE4F7EC)
                                else
                                    Color(0xFFFCE9E9)
                            ) {
                                Text(
                                    text = if (isPaymentVerified(booking))
                                        "VERIFICADO"
                                    else
                                        "PENDIENTE",
                                    color = if (isPaymentVerified(booking))
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
                                    resource = asyncPainterResource(data = booking.paymentReceiptUrl),
                                    contentDescription = "Comprobante SINPE",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(18.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                if (canVerifyPayment(booking)) {
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

                if (canStartAppointment(booking)) {
                    PrimaryActionButton(
                        text = "Iniciar cita",
                        onClick = onStartAppointment
                    )
                }

                if (canFinishAppointment(booking)) {
                    PrimaryActionButton(
                        text = "Completar cita",
                        onClick = onFinishAppointment
                    )
                }

                if (canRateClient(booking)) {
                    PrimaryActionButton(
                        text = "Calificar cliente",
                        onClick = onRateClient
                    )
                }

                if (canCancelAppointment(booking)) {
                    OutlinedButton(
                        onClick = onCancelAppointment,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Cancelar cita")
                    }
                }
            }
        }
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

private fun canShowPaymentReceipt(booking: Booking): Boolean {
    return booking.paymentReceiptUrl.isNotBlank()
}

private fun canVerifyPayment(booking: Booking): Boolean {
    return booking.status.equals("payment_pending", ignoreCase = true) &&
            booking.paymentReceiptUrl.isNotBlank()
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

private fun isPaymentVerified(booking: Booking): Boolean {
    return booking.status.equals("confirmed", ignoreCase = true) ||
            booking.status.equals("in_progress", ignoreCase = true) ||
            booking.status.equals("completed", ignoreCase = true)
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