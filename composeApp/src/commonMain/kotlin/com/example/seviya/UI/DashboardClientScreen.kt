package com.example.seviya.UI

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.theme.AppBackgroundAlt
import com.example.seviya.theme.BlueGrayText
import com.example.seviya.theme.BlueGrayTextDark
import com.example.seviya.theme.BrandBlue
import com.example.seviya.theme.BrandRed
import com.example.seviya.theme.ClientHeroCardBackground
import com.example.seviya.theme.ClientMutedDot
import com.example.seviya.theme.ClientQuickActionBorder
import com.example.seviya.theme.ClientQuickActionSurface
import com.example.seviya.theme.ClientSectionCardBorder
import com.example.seviya.theme.ClientSoftBlueRow
import com.example.seviya.theme.ClientSoftBlueRowBorder
import com.example.seviya.theme.DividerSoft
import com.example.seviya.theme.Info
import com.example.seviya.theme.Inactive
import com.example.seviya.theme.MetricGreen
import com.example.seviya.theme.RingTrack
import com.example.seviya.theme.StatusCancelledText
import com.example.seviya.theme.TextPrimaryAlt
import com.example.seviya.theme.TextSecondary
import com.example.seviya.theme.Warning
import com.example.seviya.theme.White
import com.example.shared.domain.entity.Booking
import com.example.shared.presentation.clientDashboard.ClientDashboardUiState
import com.example.shared.presentation.clientDashboard.ClientDashboardViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.ChartBar
import compose.icons.tablericons.Check
import compose.icons.tablericons.ChevronDown
import compose.icons.tablericons.ChevronUp
import compose.icons.tablericons.Clock
import compose.icons.tablericons.FileText
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Message
import compose.icons.tablericons.User
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.round
import kotlin.time.Clock

@Composable
fun ClientDashboardRoute(
    clientId: String,
    viewModel: ClientDashboardViewModel,
    avatarPainter: Painter? = null,
    onOpenAppointmentDetail: (String) -> Unit = {},
    onOpenAgenda: () -> Unit = {},
    onOpenMessages: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenLocations: () -> Unit = {},
    onOpenReports: () -> Unit = {},
    onOpenRequests: () -> Unit = {},
    onOpenCategories: () -> Unit = {},
    onOpenMenu: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(clientId) {
        viewModel.loadBookings(clientId)
    }

    ClientDashboardScreen(
        state = state,
        avatarPainter = avatarPainter,
        onOpenAppointmentDetail = onOpenAppointmentDetail,
        onOpenAgenda = onOpenAgenda,
        onOpenMessages = onOpenMessages,
        onOpenProfile = onOpenProfile,
        onOpenLocations = onOpenLocations,
        onOpenReports = onOpenReports,
        onOpenCategories = onOpenCategories,
        onOpenMenu = onOpenMenu,
        onOpenRequests = onOpenRequests
    )
}

@Composable
fun ClientDashboardScreen(
    state: ClientDashboardUiState,
    avatarPainter: Painter? = null,
    clientPhotoUrl: String? = null,
    onOpenAppointmentDetail: (String) -> Unit = {},
    onOpenAgenda: () -> Unit = {},
    onOpenMessages: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenLocations: () -> Unit = {},
    onOpenReports: () -> Unit = {},
    onOpenRequests: () -> Unit = {},
    onOpenCategories: () -> Unit = {},
    onOpenMenu: () -> Unit = {}
) {
    var upcomingExpanded by rememberSaveable { mutableStateOf(true) }
    var completedExpanded by rememberSaveable { mutableStateOf(true) }

    val upcomingBookings = remember(state.bookings) {
        state.bookings
            .filter { it.status.clientStatusKey() in setOf("payment_pending", "confirmed", "in_progress") }
            .sortedBy { it.date }
    }

    val completedBookings = remember(state.bookings) {
        state.bookings
            .filter { it.status.clientStatusKey() == "completed" }
            .sortedByDescending { it.date }
    }

    val cancelledBookings = remember(state.bookings) {
        state.bookings.count { it.status.clientStatusKey() == "cancelled" }
    }

    val nextBooking = upcomingBookings.firstOrNull()
    val errorMessage = state.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBlue)
    ) {
        ClientDashboardHeader(
            clientName = state.clientName.ifBlank { "Cliente" },
            clientPhotoUrl = state.clientPhotoUrl,
            avatarPainter = avatarPainter,
            nextBooking = nextBooking,
            onOpenNextBooking = {
                nextBooking?.let { onOpenAppointmentDetail(it.id) }
            }
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-18).dp),
            color = AppBackgroundAlt,
            shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cargando dashboard...",
                            color = TextSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                !errorMessage.isNullOrBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = BrandRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 18.dp,
                            end = 18.dp,
                            top = 18.dp,
                            bottom = 110.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        item {
                            QuickActionsRow(
                                onOpenAgenda = onOpenAgenda,
                                onOpenMessages = onOpenMessages,
                                onOpenProfile = onOpenProfile,
                                onOpenLocations = onOpenLocations,
                                onOpenReports = onOpenReports,
                                onOpenRequests = onOpenRequests
                            )
                        }

                        item {
                            SummaryRow(
                                upcomingCount = upcomingBookings.size,
                                completedCount = completedBookings.size,
                                cancelledCount = cancelledBookings
                            )
                        }

                        item {
                            SectionCard(
                                title = "Próximas citas",
                                count = upcomingBookings.size,
                                dotColor = BrandRed,
                                expanded = upcomingExpanded,
                                onToggle = { upcomingExpanded = !upcomingExpanded }
                            ) {
                                if (upcomingBookings.isEmpty()) {
                                    EmptySectionText("No tienes próximas citas en este momento.")
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        upcomingBookings.forEach { booking ->
                                            UpcomingBookingRow(
                                                booking = booking,
                                                onClick = { onOpenAppointmentDetail(booking.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            SectionCard(
                                title = "Citas finalizadas",
                                count = completedBookings.size,
                                dotColor = ClientMutedDot,
                                expanded = completedExpanded,
                                onToggle = { completedExpanded = !completedExpanded }
                            ) {
                                if (completedBookings.isEmpty()) {
                                    EmptySectionText("Aún no tienes citas finalizadas.")
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        completedBookings.take(8).forEach { booking ->
                                            CompletedBookingRow(
                                                booking = booking,
                                                onClick = { onOpenAppointmentDetail(booking.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            SecondaryActionsRow(
                                onOpenCategories = onOpenCategories,
                                onOpenMenu = onOpenMenu
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientDashboardHeader(
    clientName: String,
    clientPhotoUrl: String,
    avatarPainter: Painter?,
    nextBooking: Booking?,
    onOpenNextBooking: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 28.dp, bottom = 28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HOLA DE NUEVO,",
                    color = White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = clientName,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            ClientProfileAvatar(
                name = clientName,
                photoUrl = clientPhotoUrl,
                avatarPainter = avatarPainter
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        NextBookingHeroCard(
            booking = nextBooking,
            onOpenNextBooking = onOpenNextBooking
        )
    }
}

@Composable
private fun ClientProfileAvatar(
    name: String,
    photoUrl: String,
    avatarPainter: Painter?
) {
    Box(
        modifier = Modifier.size(74.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.22f))
                .border(
                    width = 2.dp,
                    color = White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.14f))
                    .border(
                        width = 1.5.dp,
                        color = White.copy(alpha = 0.30f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    photoUrl.isNotBlank() -> {
                        val painterResource = asyncPainterResource(data = photoUrl)

                        KamelImage(
                            resource = painterResource,
                            contentDescription = "Foto de perfil del cliente",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            onFailure = {
                                if (avatarPainter != null) {
                                    Image(
                                        painter = avatarPainter,
                                        contentDescription = "Foto de perfil del cliente",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    ClientAvatarFallback(name = name)
                                }
                            }
                        )
                    }

                    avatarPainter != null -> {
                        Image(
                            painter = avatarPainter,
                            contentDescription = "Foto de perfil del cliente",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    else -> {
                        ClientAvatarFallback(name = name)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientAvatarFallback(
    name: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.toInitials(),
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun NextBookingHeroCard(
    booking: Booking?,
    onOpenNextBooking: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = ClientHeroCardBackground,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                ProgressZeroRing(size = 82.dp)

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(BrandBlue.copy(alpha = 0.08f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "PRÓXIMA",
                                color = BrandBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = booking?.primaryServiceName() ?: "Sin citas próximas",
                        color = TextPrimaryAlt,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = booking?.let {
                            "${it.friendlyDateLabel()} • ${it.workerName.ifBlank { "Trabajador asignado" }}"
                        } ?: "Cuando confirmes una cita aparecerá aquí.",
                        color = BlueGrayText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                onClick = onOpenNextBooking,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = BrandBlue,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ver detalles",
                        color = White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressZeroRing(
    size: Dp
) {
    val trackColor = RingTrack
    val progressColor = BrandBlue

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = size.toPx() * 0.10f
            val radius = (this.size.minDimension / 2f) - stroke / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            drawCircle(
                color = trackColor,
                radius = radius,
                center = center,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 0f,
                useCenter = false,
                topLeft = Offset(stroke / 2f, stroke / 2f),
                size = androidx.compose.ui.geometry.Size(
                    width = this.size.width - stroke,
                    height = this.size.height - stroke
                ),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "0%",
                color = TextPrimaryAlt,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "inicio",
                color = BlueGrayText,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onOpenAgenda: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLocations: () -> Unit,
    onOpenReports: () -> Unit,
    onOpenRequests: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Accesos rápidos",
            color = TextPrimaryAlt,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionItem(
                label = "AGENDA",
                icon = TablerIcons.CalendarEvent,
                onClick = onOpenAgenda
            )

            QuickActionItem(
                label = "MENSAJES",
                icon = TablerIcons.Message,
                onClick = onOpenMessages
            )

            QuickActionItem(
                label = "SOLICITUDES",
                icon = TablerIcons.FileText,
                onClick = onOpenRequests
            )

            QuickActionItem(
                label = "PERFIL",
                icon = TablerIcons.User,
                onClick = onOpenProfile
            )

            QuickActionItem(
                label = "UBICACIONES",
                icon = TablerIcons.MapPin,
                onClick = onOpenLocations
            )

            QuickActionItem(
                label = "REPORTES",
                icon = TablerIcons.ChartBar,
                onClick = onOpenReports
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(88.dp)
    ) {
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(ClientQuickActionSurface)
                .border(1.dp, ClientQuickActionBorder, RoundedCornerShape(24.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = BrandBlue,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = label,
            color = BlueGrayTextDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SummaryRow(
    upcomingCount: Int,
    completedCount: Int,
    cancelledCount: Int
) {
    Column {
        Text(
            text = "Resumen general",
            color = TextPrimaryAlt,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Próximas",
                value = upcomingCount.toString(),
                accent = BrandBlue
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Finalizadas",
                value = completedCount.toString(),
                accent = MetricGreen
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Canceladas",
                value = cancelledCount.toString(),
                accent = StatusCancelledText
            )
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accent: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = White,
        border = androidx.compose.foundation.BorderStroke(1.dp, ClientSectionCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accent)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = BlueGrayText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = TextPrimaryAlt,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    count: Int,
    dotColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        color = White,
        border = androidx.compose.foundation.BorderStroke(1.dp, ClientSectionCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    color = TextPrimaryAlt,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFF1F4F8))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = count.toString(),
                        color = BlueGrayTextDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = if (expanded) TablerIcons.ChevronUp else TablerIcons.ChevronDown,
                    contentDescription = "Expandir sección",
                    tint = Inactive,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 18.dp, bottom = 18.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun UpcomingBookingRow(
    booking: Booking,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ClientSoftBlueRow,
        border = androidx.compose.foundation.BorderStroke(1.dp, ClientSoftBlueRowBorder),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(
                text = booking.workerName.toInitials(),
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.primaryServiceName(),
                    color = TextPrimaryAlt,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${booking.status.clientStatusLabel()} • ${booking.friendlyDateLabel()}",
                    color = BlueGrayText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = statusEndIcon(booking.status),
                contentDescription = "Estado",
                tint = statusAccentColor(booking.status),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun CompletedBookingRow(
    booking: Booking,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF4F6FA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.Check,
                contentDescription = "Finalizada",
                tint = MetricGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = booking.primaryServiceName(),
                color = TextPrimaryAlt,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Finalizada • ${formatPrice(booking.totalCost)}",
                color = BlueGrayText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = booking.friendlyDateLabel(),
            color = Inactive,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SecondaryActionsRow(
    onOpenCategories: () -> Unit,
    onOpenMenu: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            onClick = onOpenCategories,
            shape = RoundedCornerShape(18.dp),
            color = White,
            border = androidx.compose.foundation.BorderStroke(1.dp, DividerSoft)
        ) {
            Text(
                text = "Ir a categorías",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                color = BrandBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Surface(
            modifier = Modifier.weight(1f),
            onClick = onOpenMenu,
            shape = RoundedCornerShape(18.dp),
            color = BrandBlue
        ) {
            Text(
                text = "Abrir menú",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun EmptySectionText(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF8FAFD))
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InitialsAvatar(
    text: String,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(BrandBlue.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = BrandBlue,
            fontSize = if (size.value >= 44f) 14.sp else 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun Booking.primaryServiceName(): String {
    return services.firstOrNull()?.name?.takeIf { it.isNotBlank() } ?: "Servicio profesional"
}

private fun String.clientStatusKey(): String {
    return trim()
        .lowercase()
        .replace("-", "_")
        .replace(" ", "_")
}

private fun String.clientStatusLabel(): String {
    return when (clientStatusKey()) {
        "payment_pending" -> "Pago pendiente"
        "confirmed" -> "Confirmada"
        "in_progress" -> "En proceso"
        "completed" -> "Finalizada"
        "cancelled" -> "Cancelada"
        else -> "Sin estado"
    }
}

private fun statusAccentColor(status: String): Color {
    return when (status.clientStatusKey()) {
        "payment_pending" -> Warning
        "confirmed" -> BrandBlue
        "in_progress" -> Info
        "completed" -> MetricGreen
        "cancelled" -> StatusCancelledText
        else -> Inactive
    }
}

private fun statusEndIcon(status: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status.clientStatusKey()) {
        "payment_pending" -> TablerIcons.Clock
        "confirmed" -> TablerIcons.CalendarEvent
        "in_progress" -> TablerIcons.MapPin
        "completed" -> TablerIcons.Check
        "cancelled" -> TablerIcons.ChevronDown
        else -> TablerIcons.CalendarEvent
    }
}

private fun Booking.friendlyDateLabel(): String {
    val raw = date.trim()
    if (raw.isBlank()) return "Fecha por confirmar"

    val normalized = raw.replace("T", " ")
    val datePart = normalized.substringBefore(" ")
    val timePart = normalized.substringAfter(" ", "").takeIf { it.isNotBlank() }?.take(5)

    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .toString()

    return when {
        datePart == today && !timePart.isNullOrBlank() -> "Hoy a las $timePart"
        datePart == today -> "Hoy"
        !timePart.isNullOrBlank() -> "$datePart • $timePart"
        else -> datePart
    }
}

private fun String.toInitials(): String {
    val parts = trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "SY"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}

private fun formatPrice(value: Double): String {
    val rounded = round(value * 100) / 100.0
    return if (rounded % 1.0 == 0.0) {
        "₡${rounded.toInt()}"
    } else {
        "₡$rounded"
    }
}