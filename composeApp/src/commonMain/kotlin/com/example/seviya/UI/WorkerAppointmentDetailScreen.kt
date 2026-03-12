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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.domain.entity.Booking
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Bell
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.DotsVertical
import compose.icons.tablericons.Globe
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Menu2
import compose.icons.tablericons.Search
import compose.icons.tablericons.Tool
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

private object DetailColors {
    val Blue = Color(0xFF0A4DB3)
    val BlueDark = Color(0xFF083A86)
    val BlueSoft = Color(0xFFEAF2FF)
    val BlueBorder = Color(0xFFC9DAF8)

    val Background = Color(0xFFF3F5F8)
    val Surface = Color(0xFFFFFFFF)
    val Border = Color(0xFFE4E8EF)

    val Text = Color(0xFF18233A)
    val Muted = Color(0xFF8A97AB)
    val GreenSoft = Color(0xFFE4F7EC)
    val GreenText = Color(0xFF18A55B)

    val RedSoft = Color(0xFFFCE9E9)
    val RedText = Color(0xFFE54848)
}

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
    Scaffold(
        containerColor = DetailColors.Background,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            DetailBottomBar(
                onGoServices = onGoServices,
                onGoMap = onGoMap,
                onGoSearch = onGoSearch,
                onGoAlerts = onGoAlerts,
                onGoMenu = onGoMenu
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DetailColors.Background)
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
                                    color = DetailColors.Text,
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
                        color = Color(0xFFF1F4F8),
                        border = BorderStroke(1.dp, DetailColors.Border)
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
                                        color = DetailColors.Blue,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = TablerIcons.MapPin,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.size(14.dp))

                            Column {
                                Text(
                                    text = "${booking.location.alias}, ${booking.location.province}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DetailColors.Text
                                    )
                                )
                                Text(
                                    text = "${booking.location.district} • ${booking.location.reference}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = DetailColors.Muted
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
                            onClick = onOpenGoogleMaps,
                            modifier = Modifier.weight(1f)
                        )

                        RouteButton(
                            text = "Abrir en Waze",
                            onClick = onOpenWaze,
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
                                    color = DetailColors.Text
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DetailColors.RedSoft
                            ) {
                                Text(
                                    text = "PENDIENTE",
                                    color = DetailColors.RedText,
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
                            border = BorderStroke(1.dp, DetailColors.Border)
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

                                Button(
                                    onClick = onVerifyPayment,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DetailColors.Blue
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Text("Confirmar pago")
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
                        text = "Finalizar cita",
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
            .background(DetailColors.Blue)
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
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = "PREPARACIÓN DETALLADA",
                    color = Color.White.copy(alpha = 0.85f),
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .background(
                color = Color.White.copy(alpha = 0.18f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White
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
            color = Color.White,
            border = BorderStroke(1.dp, DetailColors.Border),
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
                        color = DetailColors.Blue,
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
            color = DetailColors.Blue,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = step,
                    color = Color.White,
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
    val isConfirmed = status.equals("confirmed", ignoreCase = true)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isConfirmed) DetailColors.GreenSoft else DetailColors.RedSoft
    ) {
        Text(
            text = if (isConfirmed) "CONFIRMADA" else status.uppercase(),
            color = if (isConfirmed) DetailColors.GreenText else DetailColors.RedText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
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
                    tint = DetailColors.Muted,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = DetailColors.Muted,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = DetailColors.Text,
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
        color = DetailColors.BlueSoft,
        border = BorderStroke(1.dp, DetailColors.BlueBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = DetailColors.Blue,
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
            containerColor = DetailColors.Blue
        )
    ) {
        Text(text)
    }
}

@Composable
private fun DetailBottomBar(
    onGoServices: () -> Unit,
    onGoMap: () -> Unit,
    onGoSearch: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoMenu: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        DetailBottomItem("Servicios", TablerIcons.Tool, false, onGoServices)
        DetailBottomItem("Mapa", TablerIcons.Globe, false, onGoMap)
        DetailBottomItem("Buscar", TablerIcons.Search, false, onGoSearch)
        DetailBottomItem("Alertas", TablerIcons.Bell, false, onGoAlerts)
        DetailBottomItem("Menú", TablerIcons.Menu2, true, onGoMenu)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.DetailBottomItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val color = if (active) DetailColors.Blue else DetailColors.Muted

    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
        },
        label = {
            Text(
                text = label,
                color = color,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium
                )
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
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