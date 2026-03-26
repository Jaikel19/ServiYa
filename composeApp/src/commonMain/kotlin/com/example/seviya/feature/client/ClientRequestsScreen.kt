package com.example.seviya.feature.client

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.seviya.core.designsystem.theme.BorderUltraSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandBlueDark
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.CardSurface
import com.example.seviya.core.designsystem.theme.ClientQuickActionSurface
import com.example.seviya.core.designsystem.theme.ClientSectionCardBorder
import com.example.seviya.core.designsystem.theme.DividerSoft
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
import com.example.shared.presentation.clientRequests.ClientRequestsUiState
import com.example.shared.presentation.clientRequests.ClientRequestsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientRequestsScreen(
    clientId: String,
    onBack: () -> Unit = {},
    onOpenRequestDetail: (appointmentId: String) -> Unit = {},
    onOpenPaymentUpload: (appointmentId: String) -> Unit = {}
) {
    val viewModel: ClientRequestsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(clientId) {
        viewModel.loadRequests(clientId)
    }

    ClientRequestsContent(
        uiState = uiState,
        onBack = onBack,
        onOpenRequestDetail = onOpenRequestDetail,
        onOpenPaymentUpload = onOpenPaymentUpload
    )
}

enum class ClientRequestFilter {
    PENDING,
    APPROVED
}

@Composable
private fun ClientRequestsContent(
    uiState: ClientRequestsUiState,
    onBack: () -> Unit = {},
    onOpenRequestDetail: (appointmentId: String) -> Unit = {},
    onOpenPaymentUpload: (appointmentId: String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(ClientRequestFilter.PENDING) }

    RequestsScreenScaffold(
        headerContent = {
            RequestsTopHeader(
                onBack = onBack
            )
        },
        filtersContent = {
            RequestsSectionHeader(
                title = if (selectedFilter == ClientRequestFilter.PENDING) {
                    "Solicitudes pendientes"
                } else {
                    "Pendientes de pago"
                },
                subtitle = if (selectedFilter == ClientRequestFilter.PENDING) {
                    "Revisa las solicitudes enviadas"
                } else {
                    "Citas aprobadas listas para subir comprobante"
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            RequestSegmentedControl(
                selectedFilter = selectedFilter,
                pendingCount = uiState.pendingAppointments.size,
                approvedCount = uiState.approvedAppointments.size,
                onPendingClick = { selectedFilter = ClientRequestFilter.PENDING },
                onApprovedClick = { selectedFilter = ClientRequestFilter.APPROVED }
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

            selectedFilter == ClientRequestFilter.PENDING -> {
                if (uiState.pendingAppointments.isEmpty()) {
                    item {
                        PremiumMessageCard(
                            title = "No tienes solicitudes pendientes",
                            subtitle = "Cuando envíes nuevas solicitudes aparecerán aquí."
                        )
                    }
                } else {
                    items(uiState.pendingAppointments) { appointment ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            ClientPendingRequestCard(
                                appointment = appointment,
                                onOpenDetail = { onOpenRequestDetail(appointment.id) }
                            )
                        }
                    }
                }
            }

            selectedFilter == ClientRequestFilter.APPROVED -> {
                if (uiState.approvedAppointments.isEmpty()) {
                    item {
                        PremiumMessageCard(
                            title = "No tienes pagos pendientes",
                            subtitle = "Las citas aprobadas que requieran comprobante aparecerán aquí."
                        )
                    }
                } else {
                    items(uiState.approvedAppointments) { appointment ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            ClientApprovedRequestCard(
                                appointment = appointment,
                                onOpenPaymentUpload = { onOpenPaymentUpload(appointment.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestSegmentedControl(
    selectedFilter: ClientRequestFilter,
    pendingCount: Int,
    approvedCount: Int,
    onPendingClick: () -> Unit,
    onApprovedClick: () -> Unit
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
                selected = selectedFilter == ClientRequestFilter.PENDING,
                onClick = onPendingClick
            )

            RequestSegmentItem(
                modifier = Modifier.weight(1f),
                title = "Pagos pendientes",
                count = approvedCount,
                selected = selectedFilter == ClientRequestFilter.APPROVED,
                onClick = onApprovedClick
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
private fun ClientPendingRequestCard(
    appointment: Appointment,
    onOpenDetail: () -> Unit
) {
    PremiumAppointmentCard(
        appointment = appointment,
        statusText = "Pendiente",
        statusBackground = StatusPendingBackground,
        statusColor = StatusPendingText,
        accentColor = BrandBlue,
        helperText = "Tu solicitud fue enviada al trabajador y está en revisión.",
        onCardClick = onOpenDetail,
        footerButton = null
    )
}

@Composable
private fun ClientApprovedRequestCard(
    appointment: Appointment,
    onOpenPaymentUpload: () -> Unit
) {
    PremiumAppointmentCard(
        appointment = appointment,
        statusText = "Aprobada",
        statusBackground = Success.copy(alpha = 0.12f),
        statusColor = Success,
        accentColor = Success,
        helperText = "La cita fue aprobada. Ahora debes subir el comprobante para continuar.",
        onCardClick = null,
        footerButton = {
            Button(
                onClick = onOpenPaymentUpload,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlueDark,
                    contentColor = White
                )
            ) {
                Text(
                    text = "Subir comprobante",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    )
}

@Composable
private fun PremiumAppointmentCard(
    appointment: Appointment,
    statusText: String,
    statusBackground: Color,
    statusColor: Color,
    accentColor: Color,
    helperText: String,
    onCardClick: (() -> Unit)?,
    footerButton: (@Composable () -> Unit)?
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
                text = appointment.workerName.ifBlank { "Trabajador" },
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