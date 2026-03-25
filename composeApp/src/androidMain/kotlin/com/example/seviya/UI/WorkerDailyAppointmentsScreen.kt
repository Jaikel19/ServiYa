package com.example.seviya.UI

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.workerDailyAppointments.DailyView
import com.example.shared.presentation.workerDailyAppointments.WorkerDailyAppointmentsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import compose.icons.TablerIcons
import compose.icons.tablericons.AlertCircle
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.Check

@Composable
actual fun WorkerDailyAppointmentsScreen(
    workerId: String,
    viewModel: WorkerDailyAppointmentsViewModel,
    onBack: () -> Unit,
    onOpenMaps: (latitude: Double, longitude: Double, clientName: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

    LaunchedEffect(workerId) {
        viewModel.loadAppointments(workerId)
    }

    val defaultLatLng = LatLng(9.9281, -84.0907)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 11f)
    }

    LaunchedEffect(uiState.appointments) {
        val first = uiState.appointments.firstOrNull()
        first?.let {
            if (it.location.latitude != 0.0 && it.location.longitude != 0.0) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    LatLng(it.location.latitude, it.location.longitude),
                    12f
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        when (uiState.currentView) {
            DailyView.MAP -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = false),
                        uiSettings = MapUiSettings(zoomControlsEnabled = false)
                    ) {
                        uiState.appointments.forEach { appointment ->
                            if (appointment.location.latitude != 0.0 && appointment.location.longitude != 0.0) {
                                Marker(
                                    state = MarkerState(
                                        LatLng(
                                            appointment.location.latitude,
                                            appointment.location.longitude
                                        )
                                    ),
                                    title = appointment.clientName,
                                    snippet = appointment.services.firstOrNull()?.name ?: "",
                                    onClick = {
                                        selectedAppointment = appointment
                                        false
                                    }
                                )
                            }
                        }
                    }

                    WorkerDailyAppointmentsHeader(
                        appointmentsCount = uiState.appointments.size,
                        currentView = uiState.currentView,
                        onBack = onBack,
                        onChangeView = viewModel::onViewChanged,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                val first = uiState.appointments.firstOrNull()
                                first?.let {
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                        LatLng(it.location.latitude, it.location.longitude),
                                        14f
                                    )
                                }
                            },
                        color = White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "📍", fontSize = 20.sp)
                        }
                    }
                }
            }

            DailyView.LIST -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF4F6F8))
                ) {
                    WorkerDailyAppointmentsHeader(
                        appointmentsCount = uiState.appointments.size,
                        currentView = uiState.currentView,
                        onBack = onBack,
                        onChangeView = viewModel::onViewChanged
                    )

                    WorkerDailyAgendaListContent(
                        appointments = uiState.appointments,
                        onAppointmentClick = { appointment ->
                            selectedAppointment = appointment
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = White)
            }
        }

        selectedAppointment?.let { appointment ->
            AppointmentPopup(
                appointment = appointment,
                onDismiss = { selectedAppointment = null },
                onOpenGoogleMaps = {
                    val uri = Uri.parse(
                        "https://www.google.com/maps/dir/?api=1&destination=${appointment.location.latitude},${appointment.location.longitude}"
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                onOpenWaze = {
                    val uri = Uri.parse(
                        "https://waze.com/ul?ll=${appointment.location.latitude},${appointment.location.longitude}&navigate=yes"
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun WorkerDailyAgendaListContent(
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedAppointments = remember(appointments) {
        appointments.sortedBy { it.serviceStartAt }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Agenda del día",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF182033)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${appointments.size} citas programadas",
                    color = Color(0xFF7B8798),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = White,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = TablerIcons.CalendarEvent,
                        contentDescription = null,
                        tint = Color(0xFF6E7A90),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFD9E0E8)
            )
            Text(
                text = "HOY",
                modifier = Modifier.padding(horizontal = 12.dp),
                color = Color(0xFF97A3B6),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFD9E0E8)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (sortedAppointments.isEmpty()) {
            EmptyDailyAgendaState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(sortedAppointments, key = { it.id }) { appointment ->
                    WorkerDailyListAppointmentCard(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDailyAgendaState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay citas programadas para hoy",
                color = Color(0xFF7B8798),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Cuando tengas citas confirmadas aparecerán aquí en formato de agenda.",
                color = Color(0xFF97A3B6),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun WorkerDailyListAppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit
) {
    val isAlert = appointment.status.equals("cancelled", true) ||
            appointment.status.equals("rejected", true)

    val accent = if (isAlert) Color(0xFFFF4A4A) else BrandBlue
    val iconTint = if (isAlert) Color(0xFFFF8A8A) else Color(0xFFA8C0E8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(104.dp)
                    .background(accent)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val time = appointmentTimeText(appointment.serviceStartAt)
                val hour = time.substringBefore(":").toIntOrNull() ?: 0
                val amPm = if (hour < 12) "AM" else "PM"

                Column(
                    modifier = Modifier.width(70.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = time,
                        color = accent,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = amPm,
                        color = Color(0xFFA0AAB9),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(52.dp)
                        .background(Color(0xFFE7ECF2))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appointment.clientName.ifBlank { "Cliente" },
                        color = Color(0xFF161D2D),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = appointment.services.firstOrNull()?.name ?: "Servicio",
                        color = Color(0xFF77849A),
                        style = MaterialTheme.typography.titleMedium
                    )

                    val district = appointment.location.district
                    if (district.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = district,
                            color = Color(0xFF97A3B6),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Icon(
                    imageVector = if (isAlert) TablerIcons.AlertCircle else TablerIcons.Check,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

private fun appointmentTimeText(value: String): String {
    val raw = when {
        value.contains("T") -> value.substringAfter("T")
        value.contains(" ") -> value.substringAfter(" ")
        else -> value
    }

    return raw.take(5).takeIf { it.length == 5 } ?: "--:--"
}

@Composable
private fun WorkerDailyAppointmentsHeader(
    appointmentsCount: Int,
    currentView: DailyView,
    onBack: () -> Unit,
    onChangeView: (DailyView) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "worker_daily_header")

    val leftBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "left_badge_scale"
    )

    val rightBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
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
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    initialOffsetY = { -it / 3 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                        .graphicsLayer {
                            translationX = shimmerOffset
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.graphicsLayer {
                            scaleX = leftBadgeScale
                            scaleY = leftBadgeScale
                        },
                        shape = RoundedCornerShape(999.dp),
                        color = White.copy(alpha = 0.13f),
                        border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .graphicsLayer { translationY = arrowFloat }
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
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Ya",
                                    color = BrandRed,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.graphicsLayer {
                            scaleX = rightBadgeScale
                            scaleY = rightBadgeScale
                        },
                        shape = RoundedCornerShape(999.dp),
                        color = White.copy(alpha = 0.13f),
                        border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable { onChangeView(DailyView.LIST) },
                                shape = RoundedCornerShape(999.dp),
                                color = if (currentView == DailyView.LIST) White else Color.Transparent
                            ) {
                                Text(
                                    text = "Lista",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentView == DailyView.LIST) BrandBlue else White
                                    )
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable { onChangeView(DailyView.MAP) },
                                shape = RoundedCornerShape(999.dp),
                                color = if (currentView == DailyView.MAP) White else Color.Transparent
                            ) {
                                Text(
                                    text = "Mapa",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentView == DailyView.MAP) BrandBlue else White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.14f))
                ) {
                    Text(
                        text = "$appointmentsCount citas hoy",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = White.copy(alpha = 0.92f),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentPopup(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onOpenGoogleMaps: () -> Unit,
    onOpenWaze: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.clientName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = appointment.services.firstOrNull()?.name ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandBlue.copy(alpha = 0.10f)
                    ) {
                        Text(
                            text = "📍 ${appointment.location.district}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BrandBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF8E1)
                ) {
                    Text(
                        text = appointmentTimeText(appointment.serviceStartAt),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onOpenGoogleMaps,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3367D6)
                    )
                ) {
                    Text(
                        text = "Google Maps",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onOpenWaze,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF33CCFF)
                    )
                ) {
                    Text(
                        text = "Waze",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    text = "Cerrar",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}