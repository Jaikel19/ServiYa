package com.example.seviya.feature.client

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.example.seviya.core.designsystem.theme.TextOnBlueSubtitle
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.presentation.cancellation.AppointmentCancellationPreview
import com.example.shared.presentation.clientAppointmentDetail.ClientAppointmentDetailUiState
import com.example.shared.presentation.clientAppointmentDetail.ClientAppointmentDetailViewModel
import com.example.shared.utils.DateTimeUtils
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.DotsVertical
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.MessageCircle
import compose.icons.tablericons.ShieldLock
import compose.icons.tablericons.Star
import compose.icons.tablericons.User
import compose.icons.tablericons.X
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientAppointmentDetailRoute(
    bookingId: String,
    onBack: () -> Unit = {},
    onReviewClick: () -> Unit = {},
    onGoServices: () -> Unit = {},
    onGoMap: () -> Unit = {},
    onGoSearch: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {}
) {
    val viewModel: ClientAppointmentDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.loadAppointmentDetail(bookingId)
    }

    ClientAppointmentDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRequestCancellationPreview = {
            viewModel.prepareCancellationPreview(
                currentDateTime = DateTimeUtils.nowIsoMinute()
            )
        },
        onCancelAppointment = {
            viewModel.cancelAppointmentByClient(
                currentDateTime = DateTimeUtils.nowIsoMinute()
            )
        },
        onDismissCancellationPreview = {
            viewModel.dismissCancellationPreview()
        },
        onReviewClick = onReviewClick,
        onGoServices = onGoServices,
        onGoMap = onGoMap,
        onGoSearch = onGoSearch,
        onGoAlerts = onGoAlerts,
        onGoMenu = onGoMenu
    )
}

@Composable
fun ClientAppointmentDetailScreen(
    uiState: ClientAppointmentDetailUiState,
    onBack: () -> Unit = {},
    onRequestCancellationPreview: () -> Unit = {},
    onCancelAppointment: () -> Unit = {},
    onDismissCancellationPreview: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onReviewClick: () -> Unit = {},
    onGoServices: () -> Unit = {},
    onGoMap: () -> Unit = {},
    onGoSearch: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {}
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
                onGoSearch = onGoSearch,
                onGoAlerts = onGoAlerts,
                onGoMenu = onGoMenu
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ClientHeader(onBack = onBack)

            when {
                uiState.isLoading -> {
                    Text(
                        text = "Cargando detalle de cita...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                errorMessageToShow != null -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFFFFF3F3),
                        border = BorderStroke(1.dp, Color(0xFFF4D0D0))
                    ) {
                        Text(
                            text = errorMessageToShow,
                            style = MaterialTheme.typography.bodyLarge,
                            color = BrandRed,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }

                appointment != null -> {
                    if (successMessageToShow != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xFFEAF7EE),
                            border = BorderStroke(1.dp, Color(0xFFCFE8D6))
                        ) {
                            Text(
                                text = successMessageToShow,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF177245),
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(18.dp)
                            )
                        }
                    }

                    if (!uiState.canShowClientSummary && appointment.status != "cancelled") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = White,
                            border = BorderStroke(1.dp, BorderSoft)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Resumen no disponible",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = TextBluePrimary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )

                                Text(
                                    text = "Esta vista solo está disponible cuando la cita está confirmada.",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = TextSecondary
                                    )
                                )

                                Text(
                                    text = "Estado actual: ${appointment.status}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = BlueGrayText,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(30.dp),
                                color = White,
                                border = BorderStroke(1.dp, BorderSoft),
                                shadowElevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            ClientStatusChip(status = appointment.status)

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Text(
                                                text = appointment.services.firstOrNull()?.name ?: "Servicio",
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    color = TextBluePrimary,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        if (!worker?.profilePicture.isNullOrBlank()) {
                                            Surface(
                                                modifier = Modifier.size(64.dp),
                                                shape = RoundedCornerShape(18.dp),
                                                color = White,
                                                border = BorderStroke(1.dp, BorderSoft)
                                            ) {
                                                KamelImage(
                                                    resource = asyncPainterResource(data = worker?.profilePicture ?: ""),
                                                    contentDescription = "Foto del trabajador",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(18.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    ClientInfoBlock(
                                        icon = TablerIcons.User,
                                        label = "TRABAJADOR",
                                        value = worker?.name ?: appointment.workerName
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    ClientInfoBlock(
                                        icon = TablerIcons.CalendarEvent,
                                        label = "FECHA Y HORA",
                                        value = "${extractDateOnlyClient(appointment.serviceStartAt)} • ${extractTimeFromDateTimeClient(appointment.serviceStartAt)}"
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    ClientInfoBlock(
                                        icon = TablerIcons.MapPin,
                                        label = "UBICACIÓN",
                                        value = formatFullLocation(appointment)
                                    )
                                }
                            }

                            if (uiState.canShowOtp) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(28.dp),
                                    color = White,
                                    border = BorderStroke(1.dp, BorderSoft),
                                    shadowElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = TablerIcons.ShieldLock,
                                                contentDescription = null,
                                                tint = BrandBlue,
                                                modifier = Modifier.size(20.dp)
                                            )

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Text(
                                                text = "Código de Verificación (OTP)",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    color = TextBluePrimary,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "Muestra este código al trabajador cuando llegue para iniciar el servicio.",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextSecondary
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    border = BorderStroke(2.dp, Color(0xFFBDD4F5)),
                                                    shape = RoundedCornerShape(24.dp)
                                                )
                                                .background(
                                                    color = Color(0xFFF7FBFF),
                                                    shape = RoundedCornerShape(24.dp)
                                                )
                                                .padding(horizontal = 18.dp, vertical = 18.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ClientOtpRow(code = uiState.otp?.code.orEmpty())
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {


                                if (uiState.canChat && !uiState.appointment?.status.equals("completed", true)) {
                                    ClientActionButton(
                                        text = "CHATEAR",
                                        icon = TablerIcons.MessageCircle,
                                        backgroundColor = BrandBlue,
                                        contentColor = White,
                                        borderColor = BrandBlue,
                                        modifier = Modifier.weight(1f),
                                        enabled = true,
                                        onClick = onChatClick
                                    )
                                }
                                
                                ClientActionButton(
                                    text = "RESEÑA",
                                    icon = TablerIcons.Star,
                                    backgroundColor = White,
                                    contentColor = if (uiState.canReview) Color(0xFF9AA5B5) else Color(0xFFC4CCD8),
                                    borderColor = BorderSoft,
                                    modifier = Modifier.weight(1f),
                                    enabled = uiState.canReview,
                                    onClick = onReviewClick
                                )

                                ClientActionButton(
                                    text = if (uiState.isPreparingCancellationPreview) "..." else "CANCELAR",
                                    icon = TablerIcons.X,
                                    backgroundColor = if (uiState.canCancel) Color(0xFFFFF3F3) else Color(0xFFFFF7F7),
                                    contentColor = if (uiState.canCancel) BrandRed else Color(0xFFE3A5A5),
                                    borderColor = Color(0xFFF4D0D0),
                                    modifier = Modifier.weight(1f),
                                    enabled = uiState.canCancel && !uiState.isPreparingCancellationPreview,
                                    onClick = onRequestCancellationPreview
                                )
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                color = White,
                                border = BorderStroke(1.dp, BorderSoft)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 16.dp)
                                ) {
                                    Text(
                                        text = "POLÍTICAS DE CANCELACIÓN",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = BlueGrayText,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

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
            confirmEnabled = !isCancellingAppointment
        )
    }
}

@Composable
private fun ClientHeader(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(BrandBlue)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            HeaderCircleButton(
                icon = TablerIcons.ArrowLeft,
                onClick = onBack
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(
                    text = "Resumen de Cita",
                    color = White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = "DETALLE DEL SERVICIO",
                    color = TextOnBlueSubtitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            HeaderCircleButton(
                icon = TablerIcons.DotsVertical,
                onClick = {}
            )
        }
    }
}

@Composable
private fun HeaderCircleButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .background(
                color = White.copy(alpha = 0.16f),
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
private fun ClientInfoBlock(
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFF5F7FA)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(18.dp),
                color = White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = BlueGrayText,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextBluePrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
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
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.75f),
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (backgroundColor == BrandBlue && enabled) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    enabled = enabled,
                    onClick = onClick
                )
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) contentColor else contentColor.copy(alpha = 0.65f),
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = text,
                    color = if (enabled) contentColor else contentColor.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
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
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = White,
                border = BorderStroke(1.dp, Color(0xFFE4EDF8))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = digit.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = BrandBlue,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientStatusChip(status: String) {
    val text: String
    val background: Color
    val textColor: Color

    when (status.lowercase()) {
        "payment_pending" -> {
            text = "PAGO PENDIENTE"
            background = StatusPendingBackground
            textColor = StatusPendingText
        }

        "confirmed" -> {
            text = "CONFIRMADA"
            background = StatusConfirmedBackground
            textColor = StatusConfirmedText
        }

        "in_progress" -> {
            text = "EN PROGRESO"
            background = StatusInProgressBackground
            textColor = StatusInProgressText
        }

        "completed" -> {
            text = "FINALIZADA"
            background = StatusCompletedBackground
            textColor = StatusCompletedText
        }

        "cancelled" -> {
            text = "CANCELADA"
            background = StatusCancelledBackground
            textColor = StatusCancelledText
        }

        else -> {
            text = status.uppercase()
            background = SoftSurface
            textColor = TextSecondary
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = background
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun ClientCancellationPolicyContent(
    cancellationPolicy: CancellationPolicy?
) {
    if (cancellationPolicy == null) {
        Text(
            text = "No hay políticas de cancelación disponibles.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ClientPolicyLine("7 días o más antes", "${cancellationPolicy.before7DaysOrMore}%")
        ClientPolicyLine("Entre 3 y 6 días", "${cancellationPolicy.between3And6Days}%")
        ClientPolicyLine("48 horas antes", "${cancellationPolicy.before48h}%")
        ClientPolicyLine("24 horas antes", "${cancellationPolicy.before24h}%")
        ClientPolicyLine("Mismo día o menos de 24h", "${cancellationPolicy.sameDayOrLess24h}%")
    }
}

@Composable
private fun ClientPolicyLine(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            color = TextBluePrimary,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
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
    onConfirm: () -> Unit
) {
    val hasRefund = preview.refundAmount > 0

    val summaryBackground = if (hasRefund) Color(0xFFEFF6FF) else Color(0xFFFFF4F4)
    val summaryBorder = if (hasRefund) Color(0xFFD6E6FF) else Color(0xFFF4D0D0)
    val summaryTitleColor = if (hasRefund) BrandBlue else BrandRed
    val summaryAmountColor = if (hasRefund) TextBluePrimary else BrandRed
    val chipBackground = if (hasRefund) Color(0xFFDCEBFF) else Color(0xFFFFE1E1)
    val chipTextColor = if (hasRefund) BrandBlue else BrandRed
    val statusText = if (hasRefund) "Reembolso disponible" else "Sin reembolso"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = RoundedCornerShape(30.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextBluePrimary
                    )
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = chipBackground
                ) {
                    Text(
                        text = statusText,
                        color = chipTextColor,
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
                    color = summaryBackground,
                    border = BorderStroke(1.dp, summaryBorder)
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
                                color = summaryTitleColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )

                        Text(
                            text = if (hasRefund) {
                                formatCurrency(preview.refundAmount)
                            } else {
                                "No aplica reembolso"
                            },
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = summaryAmountColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = White.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "${preview.refundPercentage}%",
                                    color = chipTextColor,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }

                            Text(
                                text = if (hasRefund) "Porcentaje aplicable" else "Política sin devolución",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = BlueGrayText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFFF8FAFD),
                    border = BorderStroke(1.dp, Color(0xFFE7EDF5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CancellationDetailRow(
                            label = "Cancela",
                            value = capitalizeWord(preview.cancelledBy)
                        )

                        CancellationDetailRow(
                            label = "Ventana aplicada",
                            value = preview.policyLabel
                        )

                        CancellationDetailRow(
                            label = "Total de la cita",
                            value = formatCurrency(preview.appointmentTotal)
                        )

                        CancellationDetailRow(
                            label = "Monto no reembolsable",
                            value = formatCurrency(preview.nonRefundableAmount),
                            valueColor = if (preview.nonRefundableAmount > 0) BrandRed else TextBluePrimary
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
                    color = if (confirmEnabled) BrandRed else Color(0xFFE3A5A5),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    )
}

@Composable
private fun CancellationDetailRow(
    label: String,
    value: String,
    valueColor: Color = TextBluePrimary
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = BlueGrayText,
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
private fun PreviewLine(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = BlueGrayText,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextBluePrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

private fun formatFullLocation(appointment: Appointment): String {
    return listOf(
        appointment.location.alias,
        appointment.location.district,
        appointment.location.province,
        appointment.location.reference
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