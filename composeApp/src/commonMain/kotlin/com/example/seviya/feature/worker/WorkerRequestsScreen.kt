package com.example.seviya.feature.worker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.BlueGrayText
import com.example.seviya.core.designsystem.theme.BorderBlueLight
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandBlueDark
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.ClientQuickActionSurface
import com.example.seviya.core.designsystem.theme.ClientSectionCardBorder
import com.example.seviya.core.designsystem.theme.DividerSoft
import com.example.seviya.core.designsystem.theme.ErrorRedSoft
import com.example.seviya.core.designsystem.theme.ErrorText
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.SoftSurface
import com.example.seviya.core.designsystem.theme.StatusPendingBackground
import com.example.seviya.core.designsystem.theme.StatusPendingText
import com.example.seviya.core.designsystem.theme.Success
import com.example.seviya.core.designsystem.theme.TextPrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.seviya.feature.requests.RequestLoadingCard
import com.example.seviya.feature.requests.RequestsScreenScaffold
import com.example.seviya.feature.requests.RequestsSectionHeader
import com.example.seviya.feature.requests.RequestsTopHeader
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.WorkerRequest.WorkerRequestsUiState
import com.example.shared.presentation.WorkerRequest.WorkerRequestsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerRequestsScreen(
    workerId: String,
    onOpenRequestDetail: (appointmentId: String) -> Unit = {},
    onOpenPaymentDetail: (appointmentId: String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val viewModel: WorkerRequestsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(workerId) {
        viewModel.loadRequests(workerId)
    }

    WorkerRequestsContent(
        uiState = uiState,
        onAccept = { appointment -> viewModel.acceptRequest(appointment) },
        onReject = { appointment -> viewModel.rejectRequest(appointment) },
        onConfirm = { appointment -> viewModel.confirmPayment(appointment) },
        onCancel = { appointment -> viewModel.cancelPayment(appointment) },
        onLoadPaymentPending = { viewModel.loadPaymentPending(workerId) },
        onOpenRequestDetail = onOpenRequestDetail,
        onOpenPaymentDetail = onOpenPaymentDetail,
        onBack = onBack
    )
}

enum class RequestFilter {
    PENDING,
    PAYMENT_PENDING
}

@Composable
private fun WorkerRequestsContent(
    uiState: WorkerRequestsUiState,
    onAccept: (Appointment) -> Unit,
    onReject: (Appointment) -> Unit,
    onConfirm: (Appointment) -> Unit,
    onCancel: (Appointment) -> Unit,
    onLoadPaymentPending: () -> Unit = {},
    onOpenRequestDetail: (appointmentId: String) -> Unit = {},
    onOpenPaymentDetail: (appointmentId: String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(RequestFilter.PENDING) }

    RequestsScreenScaffold(
        headerContent = {
            RequestsTopHeader(
                onBack = onBack
            )
        },
        filtersContent = {
            RequestsSectionHeader(
                title = if (selectedFilter == RequestFilter.PENDING) {
                    "Solicitudes pendientes"
                } else {
                    "Pendientes de pago"
                },
                subtitle = if (selectedFilter == RequestFilter.PENDING) {
                    "Gestiona las solicitudes enviadas por tus clientes"
                } else {
                    "Verifica los comprobantes enviados para continuar"
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            RequestSegmentedControl(
                selectedFilter = selectedFilter,
                pendingCount = uiState.requests.size,
                paymentPendingCount = uiState.paymentPendingAppointments.size,
                onPendingClick = {
                    selectedFilter = RequestFilter.PENDING
                },
                onPaymentPendingClick = {
                    selectedFilter = RequestFilter.PAYMENT_PENDING
                    onLoadPaymentPending()
                }
            )
        }
    ) {
        when {
            uiState.isLoading -> {
                item {
                    RequestLoadingCard()
                }
            }

            uiState.errorMessage != null -> {
                item {
                    PremiumMessageCard(
                        title = "No se pudieron cargar las solicitudes",
                        subtitle = uiState.errorMessage ?: "Ocurrió un error inesperado."
                    )
                }
            }

            selectedFilter == RequestFilter.PENDING -> {
                if (uiState.requests.isEmpty()) {
                        scaleY = rightBadgeScale
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(
                        width = 1.dp,
                        color = White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .graphicsLayer {
                            translationY = bubbleOffsetLarge
                            scaleX = bubbleScaleLarge
                            scaleY = bubbleScaleLarge
                        }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.08f))
                )

                Box(
                    modifier = Modifier
                        .size(46.dp)
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
private fun RequestSegmentedControl(
    selectedFilter: RequestFilter,
    pendingCount: Int,
    paymentPendingCount: Int,
    onPendingClick: () -> Unit,
    onPaymentPendingClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = White,
        border = BorderStroke(1.dp, ClientSectionCardBorder),
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RequestSegmentItem(
                modifier = Modifier.weight(1f),
                title = "Solicitudes",
                count = pendingCount,
                selected = selectedFilter == RequestFilter.PENDING,
                onClick = onPendingClick
            )

            RequestSegmentItem(
                modifier = Modifier.weight(1f),
                title = "Pagos pendientes",
                count = paymentPendingCount,
                selected = selectedFilter == RequestFilter.PAYMENT_PENDING,
                onClick = onPaymentPendingClick
            )
        }
    }
}

@Composable
private fun RequestSegmentItem(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = if (selected) BrandBlue else White,
        border = BorderStroke(
            1.dp,
            if (selected) BrandBlue else DividerSoft
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) White else TextPrimary
                )
            )

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (selected) White.copy(alpha = 0.18f) else ClientQuickActionSurface
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (selected) White else BrandBlue
                    )
                )
            }
        }
    }
}

@Composable
private fun WorkerPendingRequestCard(
    appointment: Appointment,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onOpenDetail: () -> Unit
) {
    PremiumWorkerAppointmentCard(
        appointment = appointment,
        personName = appointment.clientName.ifBlank { "Cliente" },
        statusText = "Pendiente",
        statusBackground = StatusPendingBackground,
        statusColor = StatusPendingText,
        accentColor = BrandBlue,
        helperText = "El cliente envió una solicitud de cita. Revísala y decide si deseas aceptarla o rechazarla.",
        onCardClick = onOpenDetail,
        footerButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRedSoft,
                        contentColor = ErrorText
                    )
                ) {
                    Text(
                        text = "Rechazar",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandBlueDark,
                        contentColor = White
                    )
                ) {
                    Text(
                        text = "Aceptar",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    )
}

@Composable
private fun WorkerPaymentPendingCard(
    appointment: Appointment,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onOpenPaymentDetail: () -> Unit
) {
    PremiumWorkerAppointmentCard(
        appointment = appointment,
        personName = appointment.clientName.ifBlank { "Cliente" },
        statusText = "Pago pendiente",
        statusBackground = Success.copy(alpha = 0.12f),
        statusColor = Success,
        accentColor = Success,
        helperText = "El cliente ya envió el comprobante. Revisa el detalle y confirma el pago o marca un problema.",
        onCardClick = onOpenPaymentDetail,
        footerButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRedSoft,
                        contentColor = ErrorText
                    )
                ) {
                    Text(
                        text = "Problema",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandBlueDark,
                        contentColor = White
                    )
                ) {
                    Text(
                        text = "Verificar pago",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    )
}

@Composable
private fun PremiumWorkerAppointmentCard(
    appointment: Appointment,
    personName: String,
    statusText: String,
    statusBackground: Color,
    statusColor: Color,
    accentColor: Color,
    helperText: String,
    onCardClick: (() -> Unit)?,
    footerButton: @Composable (() -> Unit)?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onCardClick != null) Modifier.clickable { onCardClick() } else Modifier
            ),
        shape = RoundedCornerShape(30.dp),
        color = CardSurface,
        border = BorderStroke(1.dp, ClientSectionCardBorder),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(
                    text = statusText,
                    backgroundColor = statusBackground,
                    textColor = statusColor
                )

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = SoftBlueSurface,
                    border = BorderStroke(1.dp, BorderBlueLight.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = buildAmountText(appointment.totalCost, appointment.currency),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = BrandBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = personName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = buildServicesText(appointment),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BlueGrayText,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppointmentInfoPill(
                    modifier = Modifier.weight(1f),
                    title = "Fecha",
                    value = formatDate(appointment.dateKey),
                    accentColor = accentColor
                )

                AppointmentInfoPill(
                    modifier = Modifier.weight(1f),
                    title = "Hora",
                    value = formatTime(appointment.serviceStartAt),
                    accentColor = accentColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            AppointmentWideInfo(
                title = "Ubicación",
                value = buildLocationText(appointment)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SoftSurface,
                border = BorderStroke(1.dp, BorderUltraSoft)
            ) {
                Text(
                    text = helperText,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            if (footerButton != null) {
                Spacer(modifier = Modifier.height(16.dp))
                footerButton()
            }
        }
    }
}

@Composable
private fun AppointmentInfoPill(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = White,
        border = BorderStroke(1.dp, DividerSoft)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = CircleShape,
                    color = accentColor
                ) {}

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value.ifBlank { "-" },
                style = MaterialTheme.typography.titleSmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun AppointmentWideInfo(
    title: String,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = White,
        border = BorderStroke(1.dp, DividerSoft)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value.ifBlank { "-" },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Composable
private fun PremiumMessageCard(
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = CardSurface,
        border = BorderStroke(1.dp, ClientSectionCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

private fun buildServicesText(appointment: Appointment): String {
    if (appointment.services.isEmpty()) return "Sin servicios"
    return appointment.services.joinToString(", ") { it.name }
}

private fun buildLocationText(appointment: Appointment): String {
    val alias = appointment.location.alias
    val district = appointment.location.district
    val province = appointment.location.province

    val parts = listOf(alias, district, province).filter { it.isNotBlank() }
    return if (parts.isEmpty()) "Sin ubicación" else parts.joinToString(", ")
}

private fun buildAmountText(totalCost: Int, currency: String): String {
    val safeCurrency = currency.ifBlank { "CRC" }
    return "$safeCurrency $totalCost"
}

private fun formatDate(dateKey: String): String {
    if (dateKey.isBlank()) return "-"
    val parts = dateKey.split("-")
    return if (parts.size == 3) {
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } else {
        dateKey
    }
}

private fun formatTime(dateTime: String): String {
    if (dateTime.isBlank()) return "-"
    val parts = dateTime.split("T")
    return if (parts.size == 2) parts[1] else dateTime
}