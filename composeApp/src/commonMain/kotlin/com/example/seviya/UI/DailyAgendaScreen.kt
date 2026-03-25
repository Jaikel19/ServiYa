package com.example.seviya.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seviya.theme.*
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.calendar.CalendarUserRole
import com.example.shared.presentation.dailyAgenda.DailyAgendaViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import compose.icons.tablericons.ArrowLeft

@OptIn(ExperimentalTime::class)
@Composable
fun DailyAgendaScreen(
    viewModel: DailyAgendaViewModel,
    userId: String,
    role: CalendarUserRole,
    onBack: () -> Unit,
    onOpenDetail: (Appointment) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    var selectedDate by remember { mutableStateOf(today) }

    val visibleDays = remember(selectedDate) { buildDaysAround(selectedDate) }
    val selectedIndex = visibleDays.indexOf(selectedDate).coerceAtLeast(0)
    val weekListState = rememberLazyListState()

    val selectedAppointments = remember(state.appointments, selectedDate) {
        state.appointments.filter { appointment ->
            extractDateOnly(appointment.serviceStartAt) == selectedDate.toString()
        }
    }

    LaunchedEffect(userId, role) {
        viewModel.loadAppointments(userId, role)
    }

    LaunchedEffect(selectedIndex) {
        weekListState.scrollToItem(selectedIndex)
    }

    Scaffold(
        containerColor = Color(0xFFF4F6F8),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F8))
                .padding(padding)
        ) {
            DailyAgendaHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${monthName(selectedDate.monthNumber)}, ${selectedDate.year}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF182033)
                        )
                    )

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

                LazyRow(
                    state = weekListState,
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(visibleDays) { _, date ->
                        DayPill(
                            date = date,
                            selected = date == selectedDate,
                            onClick = { selectedDate = date }
                        )
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
                        text = buildSelectedDayLabel(today, selectedDate),
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

                when {
                    state.isLoading -> {
                        Text("Cargando...", color = Color(0xFF7B8798))
                    }

                    state.errorMessage != null -> {
                        Text(
                            text = "Error: ${state.errorMessage}",
                            color = Color(0xFFE53935)
                        )
                    }

                    selectedAppointments.isEmpty() -> {
                        Text(
                            text = "No hay citas programadas para este día",
                            color = Color(0xFF7B8798),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            selectedAppointments.forEach { appointment ->
                                DailyAppointmentCard(
                                    appointment = appointment,
                                    onClick = { onOpenDetail(appointment) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyAgendaHeader(
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "daily_agenda_header")

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
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "Ya",
                        color = BrandRed,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 42.dp)
                    .graphicsLayer {
                        scaleX = rightBadgeScale
                        scaleY = rightBadgeScale
                    },
                shape = RoundedCornerShape(999.dp),
                color = White.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, White.copy(alpha = 0.16f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = TablerIcons.CalendarEvent,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AGENDA",
                        color = White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
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
        }
    }
}

@Composable
private fun DayPill(
    date: LocalDate,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) BrandBlue else White
    val textColor = if (selected) White else Color(0xFF95A2B5)

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = bg,
        shadowElevation = if (selected) 10.dp else 1.dp,
        border = if (selected) null
        else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7ECF2))
    ) {
        Column(
            modifier = Modifier
                .width(84.dp)
                .height(92.dp)
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = shortDayName(date.dayOfWeek),
                color = textColor,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

@Composable
private fun DailyAppointmentCard(
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
                val time = appointment.serviceStartAt.substringAfter("T").take(5)
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
                        text = appointment.clientName.ifBlank { appointment.workerName },
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

private fun buildDaysAround(centerDate: LocalDate): List<LocalDate> {
    return (-3..3).map { offset ->
        centerDate.plus(DatePeriod(days = offset))
    }
}

private fun buildSelectedDayLabel(today: LocalDate, selectedDate: LocalDate): String {
    val prefix = if (selectedDate == today) "HOY" else dayName(selectedDate.dayOfWeek)
    return "$prefix • ${dayName(selectedDate.dayOfWeek)} ${selectedDate.dayOfMonth}"
}

private fun extractDateOnly(dateTime: String): String {
    return when {
        dateTime.contains("T") -> dateTime.substringBefore("T")
        dateTime.contains(" ") -> dateTime.substringBefore(" ")
        else -> dateTime
    }
}

private fun shortDayName(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "LUN"
    DayOfWeek.TUESDAY -> "MAR"
    DayOfWeek.WEDNESDAY -> "MIÉ"
    DayOfWeek.THURSDAY -> "JUE"
    DayOfWeek.FRIDAY -> "VIE"
    DayOfWeek.SATURDAY -> "SÁB"
    DayOfWeek.SUNDAY -> "DOM"
}

private fun dayName(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "LUNES"
    DayOfWeek.TUESDAY -> "MARTES"
    DayOfWeek.WEDNESDAY -> "MIÉRCOLES"
    DayOfWeek.THURSDAY -> "JUEVES"
    DayOfWeek.FRIDAY -> "VIERNES"
    DayOfWeek.SATURDAY -> "SÁBADO"
    DayOfWeek.SUNDAY -> "DOMINGO"
}

private fun monthName(month: Int): String = when (month) {
    1 -> "Enero"
    2 -> "Febrero"
    3 -> "Marzo"
    4 -> "Abril"
    5 -> "Mayo"
    6 -> "Junio"
    7 -> "Julio"
    8 -> "Agosto"
    9 -> "Septiembre"
    10 -> "Octubre"
    11 -> "Noviembre"
    12 -> "Diciembre"
    else -> "Mes"
}