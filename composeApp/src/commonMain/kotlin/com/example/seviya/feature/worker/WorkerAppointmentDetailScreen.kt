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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.SoftSurface
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.TextSecondaryAlt
import com.example.seviya.core.designsystem.theme.White
import com.example.seviya.feature.appointmentdetail.AppointmentCancellationDetailRow
import com.example.seviya.feature.appointmentdetail.AppointmentCancellationDialogShell
import com.example.seviya.feature.appointmentdetail.AppointmentCancellationWarningCard
import com.example.seviya.feature.appointmentdetail.AppointmentMessageBanner
import com.example.seviya.feature.appointmentdetail.AppointmentStatusChip
import com.example.seviya.feature.appointmentdetail.AppointmentStatusVisuals
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt
import com.example.shared.domain.entity.ReviewMeta
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.cancellation.AppointmentCancellationPreview
import com.example.shared.presentation.workerAppointmentDetail.WorkerAppointmentDetailUiState
import com.example.shared.presentation.workerAppointmentDetail.WorkerAppointmentDetailViewModel
import com.example.shared.utils.DateTimeUtils
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Tool
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerAppointmentDetailScreen(
    appointment: Appointment,
    monthlyCalendarViewModel: MonthlyCalendarViewModel,
    onBack: () -> Unit,
    onStartAppointment: (String) -> Unit,
    onOpenPaymentDetail: (String) -> Unit,
    onRateClient: (String) -> Unit,
    onGoServices: () -> Unit = {},
    onGoMap: () -> Unit = {},
    onGoSearch: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {},
) {
    val detailViewModel: WorkerAppointmentDetailViewModel = koinViewModel()
    val detailUiState by detailViewModel.uiState.collectAsState()

    LaunchedEffect(appointment.id) {
        detailViewModel.loadPaymentReceipt(appointment.id)
        detailViewModel.loadReviewMeta(appointment.id)
    }

    WorkerAppointmentDetailContent(
        appointment = appointment,
        paymentReceipt = detailUiState.paymentReceipt,
        reviewMeta = detailUiState.reviewMeta,
        uiState = detailUiState,
        onBack = {
            detailViewModel.clearState()
            monthlyCalendarViewModel.clearSelectedAppointment()
            onBack()
        },
        onOpenGoogleMaps = {},
        onOpenWaze = {},
        onOpenPaymentDetail = { onOpenPaymentDetail(appointment.id) },
        onStartAppointment = { onStartAppointment(appointment.id) },
        onFinishAppointment = { monthlyCalendarViewModel.completeAppointment(appointment.id) },
        onRateClient = { onRateClient(appointment.id) },
        onRequestCancellationPreview = { detailViewModel.prepareCancellationPreview(appointment) },
        onCancelAppointment = {
            detailViewModel.cancelAppointmentByWorker(
                appointment = appointment,
                currentDateTime = DateTimeUtils.nowIsoMinute(),
            )
        },
        onDismissCancellationPreview = { detailViewModel.dismissCancellationPreview() },
        onGoServices = onGoServices,
        onGoMap = onGoMap,
        onGoSearch = onGoSearch,
        onGoAlerts = onGoAlerts,
        onGoMenu = onGoMenu,
    )
}

@Composable
private fun WorkerAppointmentDetailContent(
    appointment: Appointment,
    uiState: WorkerAppointmentDetailUiState = WorkerAppointmentDetailUiState(),
    paymentReceipt: PaymentReceipt? = null,
    reviewMeta: ReviewMeta = ReviewMeta(),
    onBack: () -> Unit = {},
    onOpenGoogleMaps: () -> Unit = {},
    onOpenWaze: () -> Unit = {},
    onOpenPaymentDetail: () -> Unit = {},
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
    onGoMenu: () -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current
    val errorMessageToShow = uiState.errorMessage
    val successMessageToShow = uiState.successMessage
    val cancellationPreviewToShow = uiState.cancellationPreview
    val showCancellationPreview = uiState.showCancellationPreview
    val isCancellingAppointment = uiState.isCancellingAppointment

    val screenBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF4F7FB),
            Color(0xFFF8FAFD),
            Color(0xFFF2F5F9),
        )
    )

    Scaffold(
        containerColor = Color(0xFFF4F7FB),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            WorkerDetailHeader(onBack = onBack)
            WorkerHeaderIntro()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                if (errorMessageToShow != null) {
                    AppointmentMessageBanner(
                        text = errorMessageToShow,
                        backgroundColor = Color(0xFFFFF3F3),
                        borderColor = Color(0xFFF4D0D0),
                        textColor = Color(0xFFE54848),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                }

                if (successMessageToShow != null) {
                    AppointmentMessageBanner(
                        text = successMessageToShow,
                        backgroundColor = Color(0xFFEAF7EE),
                        borderColor = Color(0xFFCFE8D6),
                        textColor = Color(0xFF177245),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                }

                StepCard(
                    step = "1",
                    title = "REVISAR DATOS DE CITA",
                    subtitle = "Consulta la información principal del servicio y del cliente."
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = appointment.services.firstOrNull()?.name ?: "Servicio",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color(0xFF18233A),
                                    fontWeight = FontWeight.ExtraBold,
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Spacer(modifier = Modifier.size(12.dp))

                        StatusChip(status = appointment.status)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    DetailInfoRow(
                        label = "CLIENTE",
                        value = appointment.clientName,
                        icon = TablerIcons.Tool,
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DetailInfoRow(
                        label = "FECHA Y HORA",
                        value = "${extractDateOnlyDetail(appointment.serviceStartAt)} • ${extractTimeFromDateTimeDetail(appointment.serviceStartAt)}",
                        icon = TablerIcons.CalendarEvent,
                    )
                }

                StepCard(
                    step = "2",
                    title = "PLANIFICAR RUTA",
                    subtitle = "Visualiza la ubicación del servicio y abre la navegación externa."
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F2)),
                        shadowElevation = 1.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    BrandBlue,
                                                    Color(0xFF2E6FD1),
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = TablerIcons.MapPin,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }

                                Spacer(modifier = Modifier.size(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = appointment.location.alias,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = Color(0xFF18233A),
                                            fontWeight = FontWeight.ExtraBold,
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    Text(
                                        text = "${appointment.location.province}, ${appointment.location.district}",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = TextSecondary,
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFFF7FAFE),
                                border = BorderStroke(1.dp, Color(0xFFE8EDF5)),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = "Referencia",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = BrandBlue,
                                            fontWeight = FontWeight.ExtraBold,
                                        ),
                                    )

                                    Text(
                                        text = appointment.location.reference,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF5D6B82),
                                            fontWeight = FontWeight.Medium,
                                        ),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        RouteButton(
                            text = "Google Maps",
                            subtitle = "Abrir ubicación",
                            onClick = {
                                val lat = appointment.location.latitude
                                val lng = appointment.location.longitude
                                uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                            },
                            modifier = Modifier.weight(1f),
                        )

                        RouteButton(
                            text = "Abrir en Waze",
                            subtitle = "Ir a la ruta",
                            onClick = {
                                val lat = appointment.location.latitude
                                val lng = appointment.location.longitude
                                uriHandler.openUri("https://waze.com/ul?ll=$lat,$lng&navigate=yes")
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                if (canShowPaymentReceipt(paymentReceipt)) {
                    StepCard(
                        step = "3",
                        title = "VERIFICAR PAGO SINPE",
                        subtitle = "Revisa el comprobante enviado por el cliente."
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xFFF8FBFF),
                            border = BorderStroke(1.dp, Color(0xFFE1E8F2)),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Comprobante enviado",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFF18233A),
                                            fontWeight = FontWeight.ExtraBold,
                                        ),
                                    )
                                    Text(
                                        text = "Confirma si el pago fue recibido correctamente.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextSecondary,
                                        ),
                                    )
                                }

                                Spacer(modifier = Modifier.size(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isPaymentVerified(appointment, paymentReceipt)) {
                                        Color(0xFFE4F7EC)
                                    } else {
                                        Color(0xFFFFF2F2)
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        if (isPaymentVerified(appointment, paymentReceipt)) {
                                            Color(0xFFCBE7D5)
                                        } else {
                                            Color(0xFFF4D0D0)
                                        }
                                    ),
                                ) {
                                    Text(
                                        text = if (isPaymentVerified(appointment, paymentReceipt)) {
                                            "VERIFICADO"
                                        } else {
                                            "PENDIENTE"
                                        },
                                        color = if (isPaymentVerified(appointment, paymentReceipt)) {
                                            Color(0xFF18A55B)
                                        } else {
                                            Color(0xFFE54848)
                                        },
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = White,
                            border = BorderStroke(1.dp, Color(0xFFE4E8EF)),
                            shadowElevation = 1.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                KamelImage(
                                    resource = asyncPainterResource(data = paymentReceipt?.imageUrl.orEmpty()),
                                    contentDescription = "Comprobante SINPE",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop,
                                )

                                if (canVerifyPayment(appointment, paymentReceipt)) {
                                    Button(
                                        onClick = onOpenPaymentDetail,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BrandBlue,
                                            contentColor = White,
                                        ),
                                        shape = RoundedCornerShape(18.dp),
                                    ) {
                                        Text(
                                            text = "Confirmar pago",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (canStartAppointment(appointment)) {
                    PrimaryActionButton(
                        text = "Iniciar cita",
                        onClick = onStartAppointment,
                    )
                }

                if (canFinishAppointment(appointment)) {
                    PrimaryActionButton(
                        text = "Completar cita",
                        onClick = onFinishAppointment,
                    )
                }

                if (canRateClient(appointment, reviewMeta)) {
                    PrimaryActionButton(
                        text = "Calificar cliente",
                        onClick = onRateClient,
                    )
                }

                if (canCancelAppointment(appointment)) {
                    OutlinedButton(
                        onClick = onRequestCancellationPreview,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(18.dp),
                        enabled = !uiState.isPreparingCancellationPreview && !uiState.isCancellingAppointment,
                        border = BorderStroke(1.2.dp, Color(0xFFD9E2EF)),
                    ) {
                        Text(
                            text = if (uiState.isPreparingCancellationPreview) {
                                "Calculando reembolso..."
                            } else {
                                "Cancelar cita"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFF44536A),
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showCancellationPreview && cancellationPreviewToShow != null) {
        WorkerCancellationPreviewDialog(
            preview = cancellationPreviewToShow,
            confirmText = if (isCancellingAppointment) "Cancelando..." else "Confirmar cancelación",
            confirmEnabled = !isCancellingAppointment,
            onDismiss = onDismissCancellationPreview,
            onConfirm = onCancelAppointment,
        )
    }
}

@Composable
private fun WorkerDetailHeader(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "worker_detail_header")

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

    LaunchedEffect(Unit) {
        entranceVisible.value = true
    }

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
                .height(150.dp)
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BrandBlue,
                            Color(0xFF0A4DB3),
                            Color(0xFF083C8B),
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(width = 140.dp, height = 150.dp)
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

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = 12.dp)
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
                        .size(44.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-74).dp, y = (-18).dp)
                        .graphicsLayer {
                            translationY = bubbleOffsetSmall
                            scaleX = bubbleScaleSmall
                            scaleY = bubbleScaleSmall
                        }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.10f))
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 18.dp, top = 42.dp)
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
                        .size(30.dp)
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
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Ya",
                        color = BrandRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            Text(
                text = "DETALLE CITA",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 18.dp, top = 42.dp)
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun WorkerHeaderIntro() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Detalle de la cita",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = TextBluePrimary,
        )

        Text(
            text = "Consulta aquí la preparación, pago y ejecución del servicio.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}

@Composable
private fun StepCard(
    step: String,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Box {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = White,
            border = BorderStroke(1.dp, Color(0xFFE4E8EF)),
            shadowElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BrandBlue,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                    ),
                )

                Spacer(modifier = Modifier.height(2.dp))

                content()
            }
        }

        Surface(
            modifier = Modifier
                .padding(start = 10.dp, top = 8.dp)
                .size(38.dp),
            shape = CircleShape,
            color = BrandBlue,
            shadowElevation = 4.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = step,
                    color = White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val visuals: AppointmentStatusVisuals =
        when (status.lowercase()) {
            "payment_pending" -> AppointmentStatusVisuals(
                text = "PAGO PENDIENTE",
                backgroundColor = Color(0xFFFFF1E6),
                textColor = Color(0xFFFF8C00),
            )

            "approved" -> AppointmentStatusVisuals(
                text = "APROBADA",
                backgroundColor = Color(0xFFE8F0FF),
                textColor = Color(0xFF0A4DB3),
            )

            "confirmed" -> AppointmentStatusVisuals(
                text = "CONFIRMADA",
                backgroundColor = Color(0xFFE6F7EC),
                textColor = Color(0xFF1F9D55),
            )

            "in_progress" -> AppointmentStatusVisuals(
                text = "EN PROGRESO",
                backgroundColor = Color(0xFFFFF6D6),
                textColor = Color(0xFFD97706),
            )

            "completed" -> AppointmentStatusVisuals(
                text = "FINALIZADA",
                backgroundColor = Color(0xFFEAF2FF),
                textColor = Color(0xFF0A4DB3),
            )

            "cancelled" -> AppointmentStatusVisuals(
                text = "CANCELADA",
                backgroundColor = Color(0xFFFCE9E9),
                textColor = Color(0xFFE54848),
            )

            "rejected" -> AppointmentStatusVisuals(
                text = "RECHAZADA",
                backgroundColor = Color(0xFFFCE9E9),
                textColor = Color(0xFFE54848),
            )

            else -> AppointmentStatusVisuals(
                text = status.uppercase(),
                backgroundColor = Color(0xFFEDEFF3),
                textColor = TextSecondaryAlt,
            )
        }

    AppointmentStatusChip(
        visuals = visuals,
        cornerRadius = 14.dp,
        textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
        horizontalPadding = 16.dp,
        verticalPadding = 10.dp,
    )
}

@Composable
private fun DetailInfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(58.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFF5F8FC),
            border = BorderStroke(1.dp, Color(0xFFE5EBF3)),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(modifier = Modifier.size(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF7B889D),
                    fontWeight = FontWeight.ExtraBold,
                ),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF18233A),
                    fontWeight = FontWeight.ExtraBold,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RouteButton(
    text: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(118.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = White,
        border = BorderStroke(1.dp, Color(0xFFDCE6F5)),
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SoftBlueSurface,
                            Color(0xFFF7FAFF),
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandBlue),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TablerIcons.MapPin,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BrandBlue,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandBlue,
            contentColor = White,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold
            ),
        )
    }
}

@Composable
private fun WorkerCancellationPreviewDialog(
    preview: AppointmentCancellationPreview,
    confirmText: String,
    confirmEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val hasRefund = preview.refundAmount > 0

    AppointmentCancellationDialogShell(
        title = "Confirmar cancelación",
        titleColor = Color(0xFF18233A),
        badgeText = "Reembolso ${preview.refundPercentage}%",
        badgeBackgroundColor = Color(0xFFFFE9E9),
        badgeTextColor = Color(0xFFE54848),
        confirmText = confirmText,
        dismissText = "Volver",
        confirmEnabled = confirmEnabled,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFFF4F4),
            border = BorderStroke(1.dp, Color(0xFFF4D0D0)),
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
                        color = Color(0xFFE54848),
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )

                Text(
                    text = if (hasRefund) {
                        formatWorkerCurrency(preview.refundAmount)
                    } else {
                        "No aplica reembolso"
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFFE54848),
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )

                Text(
                    text = "El cliente recibirá el resultado según esta cancelación.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF8A97AB),
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFF8FAFD),
            border = BorderStroke(1.dp, Color(0xFFE4E8EF)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppointmentCancellationDetailRow(
                    label = "Cancela",
                    value = "Trabajador",
                    labelColor = Color(0xFF8A97AB),
                    valueColor = Color(0xFF18233A),
                )

                AppointmentCancellationDetailRow(
                    label = "Ventana aplicada",
                    value = preview.policyLabel,
                    labelColor = Color(0xFF8A97AB),
                    valueColor = Color(0xFF18233A),
                )

                AppointmentCancellationDetailRow(
                    label = "Total de la cita",
                    value = formatWorkerCurrency(preview.appointmentTotal),
                    labelColor = Color(0xFF8A97AB),
                    valueColor = Color(0xFF18233A),
                )

                AppointmentCancellationDetailRow(
                    label = "Monto no reembolsable",
                    value = formatWorkerCurrency(preview.nonRefundableAmount),
                    labelColor = Color(0xFF8A97AB),
                    valueColor = if (preview.nonRefundableAmount > 0) {
                        Color(0xFFE54848)
                    } else {
                        Color(0xFF18233A)
                    },
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

private fun canShowPaymentReceipt(paymentReceipt: PaymentReceipt?): Boolean {
    return paymentReceipt?.imageUrl?.isNotBlank() == true
}

private fun canVerifyPayment(appointment: Appointment, paymentReceipt: PaymentReceipt?): Boolean {
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

private fun canRateClient(appointment: Appointment, reviewMeta: ReviewMeta): Boolean {
    return appointment.status.equals("completed", ignoreCase = true) &&
            !reviewMeta.workerToClientCreated
}

private fun canCancelAppointment(appointment: Appointment): Boolean {
    return appointment.status.equals("confirmed", ignoreCase = true)
}

private fun isPaymentVerified(appointment: Appointment, paymentReceipt: PaymentReceipt?): Boolean {
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