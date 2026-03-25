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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AppBackground
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
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.WorkerRequest.WorkerRequestsUiState
import com.example.shared.presentation.WorkerRequest.WorkerRequestsViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        item {
            WorkerRequestsTopHeader(
                onBack = onBack
            )
        }

        item {
            Spacer(modifier = Modifier.height(22.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                SectionHeader(
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
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        when {
            uiState.isLoading -> {
                item {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        color = CardSurface,
                        border = BorderStroke(1.dp, BorderUltraSoft),
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 34.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BrandBlue)
                        }
                    }
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
                    item {
                        PremiumMessageCard(
                            title = "No tienes solicitudes pendientes",
                            subtitle = "Cuando un cliente solicite una cita aparecerá aquí para que la revises."
                        )
                    }
                } else {
                    items(uiState.requests) { appointment ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            WorkerPendingRequestCard(
                                appointment = appointment,
                                onAccept = { onAccept(appointment) },
                                onReject = { onReject(appointment) },
                                onOpenDetail = { onOpenRequestDetail(appointment.id) }
                            )
                        }
                    }
                }
            }

            selectedFilter == RequestFilter.PAYMENT_PENDING -> {
                if (uiState.isLoadingPayments) {
                    item {
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            color = CardSurface,
                            border = BorderStroke(1.dp, BorderUltraSoft),
                            shadowElevation = 2.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 34.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = BrandBlue)
                            }
                        }
                    }
                } else if (uiState.paymentPendingAppointments.isEmpty()) {
                    item {
                        PremiumMessageCard(
                            title = "No tienes pagos pendientes",
                            subtitle = "Los comprobantes enviados por tus clientes aparecerán aquí para revisión."
                        )
                    }
                } else {
                    items(uiState.paymentPendingAppointments) { (appointment, _) ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            WorkerPaymentPendingCard(
                                appointment = appointment,
                                onConfirm = { onConfirm(appointment) },
                                onCancel = { onCancel(appointment) },
                                onOpenPaymentDetail = { onOpenPaymentDetail(appointment.id) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@Composable
private fun WorkerRequestsTopHeader(
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "worker_requests_header")

    val leftBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "left_badge_scale"
    )

    val rightBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "right_badge_scale"
    )

    val bubbleOffsetLarge by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_offset_large"
    )

    val bubbleOffsetSmall by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_offset_small"
    )

    val bubbleScaleLarge by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_scale_large"
    )

    val bubbleScaleSmall by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_scale_small"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -260f,
        targetValue = 620f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val arrowFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrow_float"
    )

    val entranceVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        entranceVisible.value = true
    }

    AnimatedVisibility(
        visible = entranceVisible.value,
        enter = fadeIn(
            animationSpec = tween(500)
        ) + slideInVertically(
            initialOffsetY = { -it / 3 },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                .background(BrandBlue)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(140.dp)
                        .offset(x = shimmerOffset.dp)
                        .graphicsLayer {
                            rotationZ = -18f
                            alpha = 0.16f
                        }
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    White.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
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
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer {
                            translationY = arrowFloat
                        }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.14f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.ArrowLeft,
                        contentDescription = "Volver",
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Servi",
                        color = White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Ya",
                        color = BrandRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Text(
                text = "SOLICITUDES",
                modifier = Modifier
                    .align(Alignment.TopEnd)
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
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            )
        )
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