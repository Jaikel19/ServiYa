package com.example.seviya.feature.client

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.components.ClientBottomBar
import com.example.seviya.core.designsystem.components.ClientTab
import com.example.seviya.core.designsystem.theme.AppBackground
import com.example.seviya.core.designsystem.theme.BlueGrayText
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.SoftSurface
import com.example.seviya.core.designsystem.theme.StatusCancelledBackground
import com.example.seviya.core.designsystem.theme.StatusCancelledText
import com.example.seviya.core.designsystem.theme.StatusCompletedBackground
import com.example.seviya.core.designsystem.theme.StatusCompletedText
import com.example.seviya.core.designsystem.theme.StatusConfirmedBackground
import com.example.seviya.core.designsystem.theme.StatusConfirmedText
import com.example.seviya.core.designsystem.theme.StatusInProgressBackground
import com.example.seviya.core.designsystem.theme.StatusInProgressText
import com.example.seviya.core.designsystem.theme.StatusPendingBackground
import com.example.seviya.core.designsystem.theme.StatusPendingText
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.seviya.feature.appointmentdetail.AppointmentCancellationDetailRow
import com.example.seviya.feature.appointmentdetail.AppointmentCancellationDialogShell
import com.example.seviya.feature.appointmentdetail.AppointmentCancellationWarningCard
import com.example.seviya.feature.appointmentdetail.AppointmentMessageBanner
import com.example.seviya.feature.appointmentdetail.AppointmentStatusChip
import com.example.seviya.feature.appointmentdetail.AppointmentStatusVisuals
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.presentation.cancellation.AppointmentCancellationPreview
import com.example.shared.presentation.clientAppointmentDetail.ClientAppointmentDetailUiState
import com.example.shared.presentation.clientAppointmentDetail.ClientAppointmentDetailViewModel
import com.example.shared.utils.DateTimeUtils
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.InfoCircle
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.ShieldLock
import compose.icons.tablericons.Star
import compose.icons.tablericons.User
import compose.icons.tablericons.X
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientAppointmentDetailScreen(
    appointmentId: String,
    onBack: () -> Unit = {},
    onReviewClick: () -> Unit = {},
    onGoServices: () -> Unit = {},
    onGoMap: () -> Unit = {},
    onGoDashboard: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {},
) {
    val viewModel: ClientAppointmentDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(appointmentId) {
        viewModel.loadAppointmentDetail(appointmentId)
    }

    ClientAppointmentDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRequestCancellationPreview = {
            viewModel.prepareCancellationPreview(currentDateTime = DateTimeUtils.nowIsoMinute())
        },
        onCancelAppointment = {
            viewModel.cancelAppointmentByClient(currentDateTime = DateTimeUtils.nowIsoMinute())
        },
        onDismissCancellationPreview = { viewModel.dismissCancellationPreview() },
        onReviewClick = onReviewClick,
        onGoServices = onGoServices,
        onGoMap = onGoMap,
        onGoDashboard = onGoDashboard,
        onGoAlerts = onGoAlerts,
        onGoMenu = onGoMenu,
    )
}

@Composable
private fun ClientAppointmentDetailContent(
    uiState: ClientAppointmentDetailUiState,
    onBack: () -> Unit = {},
    onRequestCancellationPreview: () -> Unit = {},
    onCancelAppointment: () -> Unit = {},
    onDismissCancellationPreview: () -> Unit = {},
    onReviewClick: () -> Unit = {},
    onGoServices: () -> Unit = {},
    onGoMap: () -> Unit = {},
    onGoDashboard: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {},
) {
    val appointment = uiState.appointment
    val worker = uiState.worker
    val errorMessageToShow = uiState.errorMessage
    val successMessageToShow = uiState.successMessage
    val cancellationPreviewToShow = uiState.cancellationPreview
    val showCancellationPreview = uiState.showCancellationPreview
    val isCancellingAppointment = uiState.isCancellingAppointment

    Scaffold(
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ClientBottomBar(
                currentTab = ClientTab.SERVICES,
                menuActive = false,
                onGoServices = onGoServices,
                onGoMap = onGoMap,
                onGoDashboard = onGoDashboard,
                onGoAlerts = onGoAlerts,
                onGoMenu = onGoMenu,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ClientHeader(onBack = onBack)
            ClientHeaderIntro()

            when {
                uiState.isLoading -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        color = White,
                        border = BorderStroke(1.dp, BorderSoft),
                        shadowElevation = 3.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFEFF5FF),
                                    border = BorderStroke(1.dp, Color(0xFFD8E8FF)),
                                ) {
                                    Box(
                                        modifier = Modifier.padding(9.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = TablerIcons.InfoCircle,
                                            contentDescription = null,
                                            tint = BrandBlue,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "POLÍTICAS DE CANCELACIÓN",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = BlueGrayText,
                                            fontWeight = FontWeight.ExtraBold,
                                        ),
                                    )
                                    Text(
                                        text = "A mayor anticipación, mayor reembolso.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary
                                        ),
                                    )
                                }
                            }

                            ClientCancellationPolicyContent(
                                cancellationPolicy = uiState.cancellationPolicy
                            )
                        }
                    }
                }

                errorMessageToShow != null -> {
                    AppointmentMessageBanner(
                        text = errorMessageToShow,
                        backgroundColor = Color(0xFFFFF3F3),
                        borderColor = Color(0xFFF4D0D0),
                        textColor = BrandRed,
                        modifier = Modifier.padding(16.dp),
                        paddingValue = 18.dp,
                    )
                }

                appointment != null -> {
                    if (successMessageToShow != null) {
                        AppointmentMessageBanner(
                            text = successMessageToShow,
                            backgroundColor = Color(0xFFEAF7EE),
                            borderColor = Color(0xFFCFE8D6),
                            textColor = Color(0xFF177245),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            paddingValue = 18.dp,
                        )
                    }

                    if (!uiState.canShowClientSummary && appointment.status != "cancelled") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(28.dp),
                            color = White,
                            border = BorderStroke(1.dp, BorderSoft),
                            shadowElevation = 2.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                ClientMiniTag(text = "RESUMEN")

                                Text(
                                    text = "Resumen no disponible",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = TextBluePrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                    ),
                                )

                                Text(
                                    text = "Esta vista solo está disponible cuando la cita está confirmada.",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = TextSecondary
                                    ),
                                )

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFF6F9FD),
                                    border = BorderStroke(1.dp, Color(0xFFE6ECF5)),
                                ) {
                                    Text(
                                        text = "Estado actual: ${appointment.status}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = BlueGrayText,
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        modifier = Modifier.padding(
                                            horizontal = 14.dp,
                                            vertical = 12.dp
                                        ),
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(30.dp),
                                color = White,
                                border = BorderStroke(1.dp, BorderSoft),
                                shadowElevation = 3.dp,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            ClientStatusChip(status = appointment.status)
                                            Spacer(modifier = Modifier.height(14.dp))

                                            Text(
                                                text = appointment.services.firstOrNull()?.name ?: "Servicio",
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    color = TextBluePrimary,
                                                    fontWeight = FontWeight.ExtraBold,
                                                ),
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = "Información principal de la cita",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = BlueGrayText
                                                ),
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        WorkerAvatar(
                                            imageUrl = worker?.profilePicture,
                                            fallbackName = worker?.name ?: appointment.workerName,
                                        )
                                    }

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(24.dp),
                                        color = Color(0xFFF8FBFF),
                                        border = BorderStroke(1.dp, Color(0xFFE3EDF9)),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            ClientInfoBlock(
                                                icon = TablerIcons.User,
                                                label = "TRABAJADOR",
                                                value = worker?.name ?: appointment.workerName,
                                            )

                                            ClientInfoBlock(
                                                icon = TablerIcons.CalendarEvent,
                                                label = "FECHA Y HORA",
                                                value = "${extractDateOnlyClient(appointment.serviceStartAt)} • ${
                                                    extractTimeFromDateTimeClient(appointment.serviceStartAt)
                                                }",
                                            )

                                            ClientInfoBlock(
                                                icon = TablerIcons.MapPin,
                                                label = "UBICACIÓN",
                                                value = formatFullLocation(appointment),
                                            )
                                        }
                                    }
                                }
                            }

                            if (uiState.canShowOtp) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(28.dp),
                                    color = White,
                                    border = BorderStroke(1.dp, BorderSoft),
                                    shadowElevation = 3.dp,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = Color(0xFFEFF5FF),
                                            ) {
                                                Box(
                                                    modifier = Modifier.padding(10.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = TablerIcons.ShieldLock,
                                                        contentDescription = null,
                                                        tint = BrandBlue,
                                                        modifier = Modifier.size(20.dp),
                                                    )
                                                }
                                            }

                                            Column {
                                                Text(
                                                    text = "Código de Verificación (OTP)",
                                                    style = MaterialTheme.typography.titleLarge.copy(
                                                        color = TextBluePrimary,
                                                        fontWeight = FontWeight.ExtraBold,
                                                    ),
                                                )

                                                Text(
                                                    text = "Entrégalo al trabajador al iniciar el servicio.",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        color = TextSecondary
                                                    ),
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(18.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    border = BorderStroke(2.dp, Color(0xFFBDD4F5)),
                                                    shape = RoundedCornerShape(24.dp),
                                                )
                                                .background(
                                                    color = Color(0xFFF7FBFF),
                                                    shape = RoundedCornerShape(24.dp),
                                                )
                                                .padding(horizontal = 18.dp, vertical = 18.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            ClientOtpRow(code = uiState.otp?.code.orEmpty())
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                ClientActionButton(
                                    text = "RESEÑA",
                                    icon = TablerIcons.Star,
                                    backgroundColor = if (uiState.canReview) Color(0xFFF2F8FF) else White,
                                    contentColor = if (uiState.canReview) TextBluePrimary else Color(0xFFC4CCD8),
                                    borderColor = if (uiState.canReview) Color(0xFFD7E7FF) else BorderSoft,
                                    modifier = Modifier.weight(1f),
                                    enabled = uiState.canReview,
                                    onClick = onReviewClick,
                                )

                                ClientActionButton(
                                    text = if (uiState.isPreparingCancellationPreview) "..." else "CANCELAR",
                                    icon = TablerIcons.X,
                                    backgroundColor = if (uiState.canCancel) Color(0xFFFFF3F3) else Color(0xFFFFF7F7),
                                    contentColor = if (uiState.canCancel) BrandRed else Color(0xFFE3A5A5),
                                    borderColor = Color(0xFFF4D0D0),
                                    modifier = Modifier.weight(1f),
                                    enabled = uiState.canCancel && !uiState.isPreparingCancellationPreview,
                                    onClick = onRequestCancellationPreview,
                                )
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                color = White,
                                border = BorderStroke(1.dp, BorderSoft),
                                shadowElevation = 2.dp,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color(0xFFEFF5FF),
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = TablerIcons.InfoCircle,
                                                    contentDescription = null,
                                                    tint = BrandBlue,
                                                    modifier = Modifier.size(16.dp),
                                                )
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = "POLÍTICAS DE CANCELACIÓN",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    color = BlueGrayText,
                                                    fontWeight = FontWeight.ExtraBold,
                                                ),
                                            )
                                            Text(
                                                text = "Porcentajes definidos por el trabajador.",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = TextSecondary
                                                ),
                                            )
                                        }
                                    }

                                    ClientCancellationPolicyContent(
                                        cancellationPolicy = uiState.cancellationPolicy
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showCancellationPreview && cancellationPreviewToShow != null) {
        CancellationPreviewDialog(
            preview = cancellationPreviewToShow,
            title = "Confirmar cancelación",
            confirmText = if (isCancellingAppointment) "Cancelando..." else "Confirmar cancelación",
            dismissText = "Volver",
            onDismiss = onDismissCancellationPreview,
            onConfirm = onCancelAppointment,
            confirmEnabled = !isCancellingAppointment,
        )
    }
}

@Composable
private fun ClientHeader(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "client_detail_header")

    val leftBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "left_badge_scale",
    )

    val rightBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "right_badge_scale",
    )

    val bubbleOffsetLarge by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bubble_offset_large",
    )

    val bubbleOffsetSmall by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bubble_offset_small",
    )

    val bubbleScaleLarge by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bubble_scale_large",
    )

    val bubbleScaleSmall by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bubble_scale_small",
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -260f,
        targetValue = 620f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_offset",
    )

    val entranceVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { entranceVisible.value = true }

    AnimatedVisibility(
        visible = entranceVisible.value,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    initialOffsetY = { -it / 3 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing),
                ),
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
                                    Color.Transparent,
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
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val arrowFloat by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = -2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "arrow_float",
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
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
                text = "DETALLE CITA",
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
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
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
private fun ClientHeaderIntro() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 22.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Resumen de Cita",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = TextBluePrimary,
        )

        Text(
            text = "Consulta aquí el detalle completo del servicio agendado.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}

@Composable
private fun ClientMiniTag(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFFEFF5FF),
        border = BorderStroke(1.dp, Color(0xFFD8E8FF)),
    ) {
        Text(
            text = text,
            color = BrandBlue,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
}

@Composable
private fun WorkerAvatar(
    imageUrl: String?,
    fallbackName: String,
) {
    if (!imageUrl.isNullOrBlank()) {
        Surface(
            modifier = Modifier.size(68.dp),
            shape = RoundedCornerShape(20.dp),
            color = White,
            border = BorderStroke(1.dp, BorderSoft),
            shadowElevation = 1.dp,
        ) {
            KamelImage(
                resource = asyncPainterResource(data = imageUrl),
                contentDescription = "Foto del trabajador",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
            )
        }
    } else {
        Surface(
            modifier = Modifier.size(68.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFEFF5FF),
            border = BorderStroke(1.dp, Color(0xFFD8E8FF)),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = fallbackName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = BrandBlue,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ClientInfoBlock(icon: ImageVector, label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = White,
        border = BorderStroke(1.dp, Color(0xFFE7EEF8)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEFF5FF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = BlueGrayText,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextBluePrimary,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ClientActionButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (backgroundColor == BrandBlue && enabled) 4.dp else 0.dp,
    ) {
        Box(
            modifier = Modifier
                .clickable(enabled = enabled, onClick = onClick)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) contentColor else contentColor.copy(alpha = 0.65f),
                    modifier = Modifier.size(22.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = text,
                    color = if (enabled) contentColor else contentColor.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                )
            }
        }
    }
}

@Composable
private fun ClientOtpRow(code: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        code.forEach { digit ->
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(16.dp),
                color = White,
                border = BorderStroke(1.dp, Color(0xFFE1EBF8)),
                shadowElevation = 1.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = digit.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = BrandBlue,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientStatusChip(status: String) {
    val visuals: AppointmentStatusVisuals =
        when (status.lowercase()) {
            "payment_pending" -> {
                AppointmentStatusVisuals(
                    text = "PAGO PENDIENTE",
                    backgroundColor = StatusPendingBackground,
                    textColor = StatusPendingText,
                )
            }

            "confirmed" -> {
                AppointmentStatusVisuals(
                    text = "CONFIRMADA",
                    backgroundColor = StatusConfirmedBackground,
                    textColor = StatusConfirmedText,
                )
            }

            "in_progress" -> {
                AppointmentStatusVisuals(
                    text = "EN PROGRESO",
                    backgroundColor = StatusInProgressBackground,
                    textColor = StatusInProgressText,
                )
            }

            "completed" -> {
                AppointmentStatusVisuals(
                    text = "FINALIZADA",
                    backgroundColor = StatusCompletedBackground,
                    textColor = StatusCompletedText,
                )
            }

            "cancelled" -> {
                AppointmentStatusVisuals(
                    text = "CANCELADA",
                    backgroundColor = StatusCancelledBackground,
                    textColor = StatusCancelledText,
                )
            }

            else -> {
                AppointmentStatusVisuals(
                    text = status.uppercase(),
                    backgroundColor = SoftSurface,
                    textColor = TextSecondary,
                )
            }
        }

    AppointmentStatusChip(
        visuals = visuals,
        cornerRadius = 16.dp,
        textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
        horizontalPadding = 16.dp,
        verticalPadding = 10.dp,
    )
}

@Composable
private fun ClientCancellationPolicyContent(cancellationPolicy: CancellationPolicy?) {
    if (cancellationPolicy == null) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF8FAFD),
            border = BorderStroke(1.dp, Color(0xFFE7EDF5)),
        ) {
            Text(
                text = "No hay políticas de cancelación disponibles.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                modifier = Modifier.padding(16.dp),
            )
        }
        return
    }

    val policyItems = listOf(
        "7 días o más antes" to cancellationPolicy.before7DaysOrMore,
        "Entre 3 y 6 días" to cancellationPolicy.between3And6Days,
        "48 horas antes" to cancellationPolicy.before48h,
        "24 horas antes" to cancellationPolicy.before24h,
        "Mismo día o menos de 24h" to cancellationPolicy.sameDayOrLess24h,
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        policyItems.forEach { (label, percentage) ->
            ClientCancellationPolicyItem(
                label = label,
                percentage = percentage,
            )
        }
    }
}

@Composable
private fun ClientCancellationPolicyItem(
    label: String,
    percentage: Int,
) {
    val clampedPercentage = percentage.coerceIn(0, 100)
    val progress = clampedPercentage / 100f
    val isNoRefund = clampedPercentage == 0

    val accentColor = when {
        clampedPercentage >= 100 -> BrandBlue
        clampedPercentage >= 75 -> BrandBlue.copy(alpha = 0.92f)
        clampedPercentage >= 50 -> BrandBlue.copy(alpha = 0.82f)
        clampedPercentage > 0 -> BrandBlue.copy(alpha = 0.72f)
        else -> BrandRed.copy(alpha = 0.78f)
    }

    val containerColor = if (isNoRefund) {
        Color(0xFFFFF7F7)
    } else {
        White
    }

    val borderColor = if (isNoRefund) {
        Color(0xFFF2DADA)
    } else {
        Color(0xFFE3ECF8)
    }

    val tagBackground = if (isNoRefund) {
        BrandRed.copy(alpha = 0.10f)
    } else {
        BrandBlue.copy(alpha = 0.10f)
    }

    val tagTextColor = if (isNoRefund) BrandRed else BrandBlue

    val badgeBackground = if (isNoRefund) {
        BrandRed.copy(alpha = 0.10f)
    } else {
        Color(0xFFEFF5FF)
    }

    val subtitle = when {
        clampedPercentage == 100 -> "Reembolso completo"
        clampedPercentage == 0 -> "Sin reembolso"
        else -> "Reembolso parcial"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tagBackground,
                ) {
                    Text(
                        text = shortPolicyTag(label),
                        color = tagTextColor,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextBluePrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary
                        ),
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = badgeBackground,
                    border = BorderStroke(
                        1.dp,
                        if (isNoRefund) BrandRed.copy(alpha = 0.12f) else Color(0xFFD7E7FF)
                    ),
                ) {
                    Text(
                        text = "$clampedPercentage%",
                        color = if (isNoRefund) BrandRed else TextBluePrimary,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isNoRefund) {
                            BrandRed.copy(alpha = 0.08f)
                        } else {
                            BrandBlue.copy(alpha = 0.08f)
                        }
                    )
            ) {
                if (progress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(999.dp))
                            .background(accentColor)
                    )
                }
            }
        }
    }
}

private fun shortPolicyTag(label: String): String {
    return when {
        label.contains("7 días") -> "7+ días"
        label.contains("3 y 6") -> "3-6 días"
        label.contains("48 horas") -> "48h"
        label.contains("24 horas") -> "24h"
        else -> "Hoy"
    }
}

@Composable
private fun ClientPolicyLine(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF8FAFD),
        border = BorderStroke(1.dp, Color(0xFFE7EDF5)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFFEFF5FF),
            ) {
                Text(
                    text = value,
                    color = TextBluePrimary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun CancellationPreviewDialog(
    preview: AppointmentCancellationPreview,
    title: String,
    confirmText: String,
    dismissText: String,
    confirmEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val hasRefund = preview.refundAmount > 0

    val summaryBackground = if (hasRefund) Color(0xFFEFF6FF) else Color(0xFFFFF4F4)
    val summaryBorder = if (hasRefund) Color(0xFFD6E6FF) else Color(0xFFF4D0D0)
    val summaryTitleColor = if (hasRefund) BrandBlue else BrandRed
    val summaryAmountColor = if (hasRefund) TextBluePrimary else BrandRed
    val chipBackground = if (hasRefund) Color(0xFFDCEBFF) else Color(0xFFFFE1E1)
    val chipTextColor = if (hasRefund) BrandBlue else BrandRed
    val statusText = if (hasRefund) "Reembolso disponible" else "Sin reembolso"

    AppointmentCancellationDialogShell(
        title = title,
        titleColor = TextBluePrimary,
        badgeText = statusText,
        badgeBackgroundColor = chipBackground,
        badgeTextColor = chipTextColor,
        confirmText = confirmText,
        dismissText = dismissText,
        confirmEnabled = confirmEnabled,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = summaryBackground,
            border = BorderStroke(1.dp, summaryBorder),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = if (hasRefund) "Monto estimado a reembolsar" else "Resultado de la cancelación",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = summaryTitleColor,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )

                Text(
                    text = if (hasRefund) {
                        formatCurrency(preview.refundAmount)
                    } else {
                        "No aplica reembolso"
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = summaryAmountColor,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(shape = RoundedCornerShape(14.dp), color = White.copy(alpha = 0.9f)) {
                        Text(
                            text = "${preview.refundPercentage}%",
                            color = chipTextColor,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }

                    Text(
                        text = if (hasRefund) "Porcentaje aplicable" else "Política sin devolución",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BlueGrayText,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFF8FAFD),
            border = BorderStroke(1.dp, Color(0xFFE7EDF5)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppointmentCancellationDetailRow(
                    label = "Cancela",
                    value = capitalizeWord(preview.cancelledBy),
                    labelColor = BlueGrayText,
                    valueColor = TextBluePrimary,
                )

                AppointmentCancellationDetailRow(
                    label = "Ventana aplicada",
                    value = preview.policyLabel,
                    labelColor = BlueGrayText,
                    valueColor = TextBluePrimary,
                )

                AppointmentCancellationDetailRow(
                    label = "Total de la cita",
                    value = formatCurrency(preview.appointmentTotal),
                    labelColor = BlueGrayText,
                    valueColor = TextBluePrimary,
                )

                AppointmentCancellationDetailRow(
                    label = "Monto no reembolsable",
                    value = formatCurrency(preview.nonRefundableAmount),
                    labelColor = BlueGrayText,
                    valueColor = if (preview.nonRefundableAmount > 0) BrandRed else TextBluePrimary,
                )
            }
        }

        if (hasRefund && preview.warningMessage.isNotBlank()) {
            AppointmentCancellationWarningCard(
                message = preview.warningMessage,
                title = "Importante",
                backgroundColor = Color(0xFFFFF8E8),
                borderColor = Color(0xFFF2D693),
                titleColor = Color(0xFF8C6500),
                textColor = Color(0xFF8C6500),
            )
        }
    }
}

private fun formatFullLocation(appointment: Appointment): String {
    return listOf(
        appointment.location.alias,
        appointment.location.district,
        appointment.location.province,
        appointment.location.reference,
    ).filter { it.isNotBlank() }
        .joinToString(", ")
}

private fun extractDateOnlyClient(dateTime: String): String {
    return when {
        dateTime.contains("T") -> dateTime.substringBefore("T")
        dateTime.contains(" ") -> dateTime.substringBefore(" ")
        else -> dateTime
    }
}

private fun extractTimeFromDateTimeClient(dateTime: String): String {
    return when {
        dateTime.contains("T") -> dateTime.substringAfter("T").take(5)
        dateTime.contains(" ") -> dateTime.substringAfter(" ").take(5)
        else -> ""
    }
}

private fun formatCurrency(amount: Int): String {
    val raw = amount.coerceAtLeast(0).toString()
    val reversed = raw.reversed()
    val grouped = reversed.chunked(3).joinToString(".")
    return "₡ ${grouped.reversed()}"
}

private fun capitalizeWord(value: String): String {
    if (value.isBlank()) return value
    val first = value.first().uppercaseChar()
    return first + value.drop(1)
}