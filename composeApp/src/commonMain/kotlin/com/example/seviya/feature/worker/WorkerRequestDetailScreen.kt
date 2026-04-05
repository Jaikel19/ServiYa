package com.example.seviya.feature.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.app.FeaturePlaceholder
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.AppointmentService
import com.example.shared.presentation.WorkerRequestDetailViewModel.WorkerRequestDetailViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.Check
import compose.icons.tablericons.CreditCard
import compose.icons.tablericons.FileText
import compose.icons.tablericons.InfoCircle
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.User
import compose.icons.tablericons.X
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerRequestDetailScreen(bookingId: String, onBack: () -> Unit) {
    val viewModel: WorkerRequestDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(bookingId) { viewModel.loadBooking(bookingId) }

    uiState.appointment?.let { appointment ->
        WorkerRequestDetailContent(
            booking = appointment,
            onBack = onBack,
            onAccept = {
                viewModel.acceptRequest()
                onBack()
            },
            onReject = {
                viewModel.rejectRequest()
                onBack()
            },
        )
    } ?: FeaturePlaceholder(title = "Solicitud", subtitle = "No se encontró la solicitud.")
}

@Composable
private fun WorkerRequestDetailContent(
    booking: Appointment,
    onBack: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onGoServices: () -> Unit = {},
    onGoAgenda: () -> Unit = {},
    onGoRequests: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {},
) {
    val formattedDate = formatAppointmentDate(booking.serviceStartAt)
    val serviceTimeline = buildServiceTimeline(booking.serviceStartAt, booking.services, booking.currency)

    Scaffold(
        containerColor = AppBackgroundAlt,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
        ) {
            WorkerRequestDetailHeader(onBack = onBack)

            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Detalle de solicitud",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextBluePrimary,
                    )
                    Text(
                        text = "Revisa toda la información de la cita antes de aceptar o rechazar la solicitud.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier =
                                    Modifier.size(42.dp)
                                        .clip(CircleShape)
                                        .background(SoftBlueSurface),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = TablerIcons.FileText,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(20.dp),
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SERVICIOS SOLICITADOS",
                                    style =
                                        MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp,
                                        ),
                                    color = TextSecondary,
                                )
                                Text(
                                    text = "${booking.services.size} servicio${if (booking.services.size == 1) "" else "s"}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextBluePrimary,
                                )
                            }
                        }

                        AppointmentDateRow(value = formattedDate)

                        InfoCard(
                            text =
                                "Aquí puedes revisar el detalle de cada servicio, su duración estimada y el horario aproximado dentro de la cita."
                        )

                        if (serviceTimeline.isEmpty()) {
                            EmptyServicesCard(
                                totalDurationLabel = formatMinutesLabel(booking.serviceDurationMinutes),
                                totalCostLabel = formatCurrency(booking.totalCost, booking.currency),
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                serviceTimeline.forEach { item ->
                                    ServiceItemCard(item = item)
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = "Información general",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextBluePrimary,
                        )

                        HorizontalDivider(color = BorderSoft)

                        DetailRow(
                            icon = TablerIcons.User,
                            label = "CLIENTE",
                            value = booking.clientName.ifBlank { "Sin nombre" },
                        )

                        HorizontalDivider(color = BorderSoft)

                        DetailRow(
                            icon = TablerIcons.MapPin,
                            label = "UBICACIÓN",
                            value = booking.location.district.ifBlank { "Sin ubicación" },
                        )

                        HorizontalDivider(color = BorderSoft)

                        DetailRow(
                            icon = TablerIcons.CreditCard,
                            label = "COSTO TOTAL",
                            value = formatCurrency(booking.totalCost, booking.currency),
                            highlightValue = true,
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = BrandBlue,
                                contentColor = White,
                            ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    ) {
                        Icon(
                            imageVector = TablerIcons.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aceptar Solicitud",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandRed),
                        border = BorderStroke(1.dp, BrandRed.copy(alpha = 0.28f)),
                    ) {
                        Icon(
                            imageVector = TablerIcons.X,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rechazar Solicitud",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerRequestDetailHeader(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "worker_request_detail_header")

    val leftBadgeScale by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "left_badge_scale",
    )

    val rightBadgeScale by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "right_badge_scale",
    )

    val bubbleOffsetLarge by
    infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bubble_offset_large",
    )

    val bubbleOffsetSmall by
    infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bubble_offset_small",
    )

    val bubbleScaleLarge by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bubble_scale_large",
    )

    val bubbleScaleSmall by
    infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bubble_scale_small",
    )

    val shimmerOffset by
    infiniteTransition.animateFloat(
        initialValue = -260f,
        targetValue = 620f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(3200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmer_offset",
    )

    val entranceVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { entranceVisible.value = true }

    AnimatedVisibility(
        visible = entranceVisible.value,
        enter =
            fadeIn(animationSpec = tween(500)) +
                    slideInVertically(
                        initialOffsetY = { -it / 3 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing),
                    ),
    ) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                    .background(BrandBlue)
        ) {
            Box(
                modifier =
                    Modifier.matchParentSize()
                        .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
            ) {
                Box(
                    modifier =
                        Modifier.fillMaxHeight()
                            .width(140.dp)
                            .offset(x = shimmerOffset.dp)
                            .graphicsLayer {
                                rotationZ = -18f
                                alpha = 0.16f
                            }
                            .background(
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            White.copy(alpha = 0.45f),
                                            Color.Transparent,
                                        )
                                )
                            )
                )
            }

            Row(
                modifier =
                    Modifier.align(Alignment.TopStart)
                        .padding(start = 20.dp, top = 42.dp)
                        .graphicsLayer {
                            scaleX = leftBadgeScale
                            scaleY = leftBadgeScale
                        }
                        .clip(RoundedCornerShape(999.dp))
                        .background(White.copy(alpha = 0.14f))
                        .border(
                            width = 1.dp,
                            color = White.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(999.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val arrowFloat by
                infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = -2f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "arrow_float",
                )

                Box(
                    modifier =
                        Modifier.size(28.dp)
                            .graphicsLayer { translationY = arrowFloat }
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.14f))
                            .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = TablerIcons.ArrowLeft,
                        contentDescription = "Volver",
                        tint = White,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Servi", color = White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Ya", color = BrandRed, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Text(
                text = "SOLICITUD",
                modifier =
                    Modifier.align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 42.dp)
                        .graphicsLayer {
                            scaleX = rightBadgeScale
                            scaleY = rightBadgeScale
                        }
                        .clip(RoundedCornerShape(999.dp))
                        .background(White.copy(alpha = 0.14f))
                        .border(
                            width = 1.dp,
                            color = White.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(999.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)) {
                Box(
                    modifier =
                        Modifier.size(82.dp)
                            .graphicsLayer {
                                translationY = bubbleOffsetLarge
                                scaleX = bubbleScaleLarge
                                scaleY = bubbleScaleLarge
                            }
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.08f))
                )

                Box(
                    modifier =
                        Modifier.size(46.dp)
                            .align(Alignment.BottomStart)
                            .graphicsLayer {
                                translationY = bubbleOffsetSmall
                                scaleX = bubbleScaleSmall
                                scaleY = bubbleScaleSmall
                            }
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.10f))
                )
            }
        }
    }
}

@Composable
private fun AppointmentDateRow(value: String) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SoftBlueSurface.copy(alpha = 0.75f))
                .border(
                    width = 1.dp,
                    color = BorderSoft,
                    shape = RoundedCornerShape(14.dp),
                )
                .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier =
                Modifier.size(34.dp)
                    .clip(CircleShape)
                    .background(BrandBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = TablerIcons.CalendarEvent,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "FECHA DE LA CITA",
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                    ),
                color = TextSecondary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = TextBluePrimary,
            )
        }
    }
}

@Composable
private fun InfoCard(text: String) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SoftBlueSurface)
                .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = TablerIcons.InfoCircle,
            contentDescription = null,
            tint = BrandBlue,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = TextBluePrimary,
        )
    }
}

@Composable
private fun EmptyServicesCard(
    totalDurationLabel: String,
    totalCostLabel: String,
) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SoftBlueSurface.copy(alpha = 0.65f))
                .border(
                    width = 1.dp,
                    color = BorderSoft,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "No hay detalle individual de servicios en esta cita.",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextBluePrimary,
        )

        Text(
            text = "Duración total: $totalDurationLabel",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )

        Text(
            text = "Monto total: $totalCostLabel",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
}

@Composable
private fun ServiceItemCard(item: ServiceTimelineUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SoftBlueSurface.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier =
                        Modifier.size(34.dp)
                            .clip(CircleShape)
                            .background(BrandBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.index.toString(),
                        color = BrandBlue,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextBluePrimary,
                    )
                    if (item.description.isNotBlank()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderSoft)

            ServiceMetaRow(
                label = "Horario estimado",
                value = "${item.startTimeLabel} - ${item.endTimeLabel}",
            )
            ServiceMetaRow(
                label = "Duración",
                value = item.durationLabel,
            )
            ServiceMetaRow(
                label = "Monto",
                value = item.priceLabel,
                highlightValue = true,
            )
        }
    }
}

@Composable
private fun ServiceMetaRow(
    label: String,
    value: String,
    highlightValue: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = if (highlightValue) BrandBlue else TextBluePrimary,
        )
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    highlightValue: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier.size(42.dp)
                    .clip(CircleShape)
                    .background(SoftBlueSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(19.dp),
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                    ),
            )
            Text(
                text = value,
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (highlightValue) BrandBlue else TextBluePrimary,
                    ),
            )
        }
    }
}

private data class ServiceTimelineUi(
    val index: Int,
    val name: String,
    val description: String,
    val durationLabel: String,
    val priceLabel: String,
    val startTimeLabel: String,
    val endTimeLabel: String,
)

private fun buildServiceTimeline(
    serviceStartAt: String,
    services: List<AppointmentService>,
    currency: String,
): List<ServiceTimelineUi> {
    val startMinutes = extractMinutesOfDay(serviceStartAt) ?: return emptyList()

    var currentMinutes = startMinutes

    return services.mapIndexed { index, service ->
        val duration = service.durationMinutes.coerceAtLeast(0)
        val itemStart = currentMinutes
        val itemEnd = currentMinutes + duration
        currentMinutes = itemEnd

        val amount =
            when {
                service.subtotal > 0 -> service.subtotal
                service.cost > 0 -> service.cost
                else -> 0
            }

        ServiceTimelineUi(
            index = index + 1,
            name = service.name.ifBlank { "Servicio ${index + 1}" },
            description = service.description,
            durationLabel = formatMinutesLabel(duration),
            priceLabel = formatCurrency(amount, currency),
            startTimeLabel = formatMinutesAsTime(itemStart),
            endTimeLabel = formatMinutesAsTime(itemEnd),
        )
    }
}

private fun formatAppointmentDate(rawValue: String): String {
    val normalized = rawValue.trim().substringBefore(".")
    val datePart = normalized.substringBefore("T")
    val pieces = datePart.split("-")

    if (pieces.size != 3) return "Sin fecha"

    val year = pieces[0]
    val month = pieces[1]
    val day = pieces[2]

    return "$day/$month/$year"
}

private fun extractMinutesOfDay(rawValue: String): Int? {
    val normalized = rawValue.trim().substringBefore(".")
    val timePart = normalized.substringAfter("T", "")
    if (timePart.isBlank()) return null

    val pieces = timePart.split(":")
    if (pieces.size < 2) return null

    val hour = pieces[0].toIntOrNull() ?: return null
    val minute = pieces[1].toIntOrNull() ?: return null

    return (hour * 60) + minute
}

private fun formatMinutesAsTime(totalMinutes: Int): String {
    val normalized = ((totalMinutes % (24 * 60)) + (24 * 60)) % (24 * 60)
    val hour24 = normalized / 60
    val minute = normalized % 60

    val period = if (hour24 >= 12) "p. m." else "a. m."
    val hour12 =
        when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }

    return "$hour12:${minute.toString().padStart(2, '0')} $period"
}

private fun formatMinutesLabel(totalMinutes: Int): String {
    if (totalMinutes <= 0) return "No definido"

    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 && minutes > 0 -> "$hours h $minutes min"
        hours > 0 -> "$hours h"
        else -> "$minutes min"
    }
}

private fun formatCurrency(amount: Int, currency: String): String {
    return if (currency.uppercase() == "CRC") {
        "₡$amount"
    } else {
        "$currency $amount"
    }
}