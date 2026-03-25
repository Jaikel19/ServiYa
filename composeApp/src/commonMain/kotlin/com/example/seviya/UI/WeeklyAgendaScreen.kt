package com.example.seviya.UI

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.White
import compose.icons.tablericons.ArrowLeft

@OptIn(ExperimentalTime::class)
@Composable
fun WeeklyAgendaScreen(
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

    var selectedWeekStart by remember(today) {
        mutableStateOf(startOfWeek(today))
    }

    val weekDays = remember(selectedWeekStart) {
        (0..6).map { selectedWeekStart.plus(DatePeriod(days = it)) }
    }

    val titleText = buildCompactWeekTitle(
        start = weekDays.first(),
        end = weekDays.last()
    )

    val appointmentsByDay = remember(state.appointments, weekDays) {
        weekDays.associateWith { day ->
            state.appointments
                .filter { appointment ->
                    extractDateOnly(appointment.serviceStartAt) == day.toString()
                }
                .filter { appointment ->
                    !appointment.status.equals("cancelled", ignoreCase = true) &&
                            !appointment.status.equals("rejected", ignoreCase = true)
                }
                .sortedBy { appointment ->
                    extractSortableDateTime(appointment.serviceStartAt)
                }
        }
    }

    LaunchedEffect(userId, role) {
        viewModel.loadAppointments(userId, role)
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
            WeeklyAgendaHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF182033)
                    )
                )

                Text(
                    text = "CRONOGRAMA DE CITAS",
                    color = Color(0xFF9AA7BA),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIconButton(
                        icon = TablerIcons.ChevronLeft,
                        onClick = {
                            selectedWeekStart = selectedWeekStart.minus(DatePeriod(days = 7))
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    CircleIconButton(
                        icon = TablerIcons.ChevronRight,
                        onClick = {
                            selectedWeekStart = selectedWeekStart.plus(DatePeriod(days = 7))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    state.isLoading -> {
                        Text(
                            text = "Cargando...",
                            color = Color(0xFF7B8798)
                        )
                    }

                    state.errorMessage != null -> {
                        Text(
                            text = "Error: ${state.errorMessage}",
                            color = Color(0xFFE53935)
                        )
                    }

                    else -> {
                        weekDays.forEachIndexed { index, day ->
                            val dayAppointments = appointmentsByDay[day].orEmpty()
                            TimelineDaySection(
                                day = day,
                                appointments = dayAppointments,
                                isToday = day == today,
                                isLast = index == weekDays.lastIndex,
                                onOpenDetail = onOpenDetail
                            )
                        }

                        Spacer(modifier = Modifier.height(96.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyAgendaHeader(
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weekly_agenda_header")

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
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(46.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = White,
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6E7A90),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TimelineDaySection(
    day: LocalDate,
    appointments: List<Appointment>,
    isToday: Boolean,
    isLast: Boolean,
    onOpenDetail: (Appointment) -> Unit
) {
    val lineColor = Color(0xFFDCE3ED)
    val indicatorBorder = if (appointments.isEmpty()) Color(0xFFD8DFEA) else BrandBlue
    val indicatorFill = if (appointments.isEmpty()) Color(0xFFF4F6F8) else White
    val titleColor = if (appointments.isEmpty()) Color(0xFFB4BDCB) else Color(0xFF182033)

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.width(38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(indicatorFill)
                    .border(
                        width = 4.dp,
                        color = indicatorBorder,
                        shape = CircleShape
                    )
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (appointments.isEmpty()) 92.dp else (appointments.size * 110).dp.coerceAtLeast(110.dp))
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp)
        ) {
            if (isToday) {
                Text(
                    text = "HOY",
                    color = BrandBlue,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${capitalizedDayName(day.dayOfWeek)} ${day.dayOfMonth}",
                    color = titleColor,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                if (appointments.isEmpty()) {
                    Text(
                        text = "Sin citas",
                        color = Color(0xFFCAD2DE),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            appointments.forEach { appointment ->
                WeeklyTimelineCard(
                    appointment = appointment,
                    onClick = { onOpenDetail(appointment) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun WeeklyTimelineCard(
    appointment: Appointment,
    onClick: () -> Unit
) {
    val isAlert = appointment.status.equals("cancelled", true) ||
            appointment.status.equals("rejected", true)

    val accent = if (isAlert) Color(0xFFEF4444) else BrandBlue
    val secondary = if (isAlert) Color(0xFFEF4444) else Color(0xFF6B7A90)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(86.dp)
                    .background(accent)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = BrandBlue.copy(alpha = 0.10f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = TablerIcons.User,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appointment.clientName.ifBlank { appointment.workerName },
                        color = Color(0xFF151C2C),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${appointment.services.firstOrNull()?.name ?: "Servicio"} • ${extractTime(appointment.serviceStartAt)}",
                        color = secondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Icon(
                    imageVector = TablerIcons.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFC9D0DB),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

private fun startOfWeek(date: LocalDate): LocalDate {
    return date.minus(DatePeriod(days = date.dayOfWeek.ordinal))
}

private fun buildCompactWeekTitle(start: LocalDate, end: LocalDate): String {
    return if (start.monthNumber == end.monthNumber && start.year == end.year) {
        "${monthName(start.monthNumber)} ${start.year}"
    } else if (start.year == end.year) {
        "${start.dayOfMonth} ${shortMonthName(start.monthNumber)} - ${end.dayOfMonth} ${shortMonthName(end.monthNumber)} ${start.year}"
    } else {
        "${start.dayOfMonth} ${shortMonthName(start.monthNumber)} ${start.year} - ${end.dayOfMonth} ${shortMonthName(end.monthNumber)} ${end.year}"
    }
}

private fun extractDateOnly(dateTime: String): String {
    return when {
        dateTime.contains("T") -> dateTime.substringBefore("T")
        dateTime.contains(" ") -> dateTime.substringBefore(" ")
        else -> dateTime
    }
}

private fun extractTime(dateTime: String): String {
    return when {
        dateTime.contains("T") -> dateTime.substringAfter("T").take(5)
        dateTime.contains(" ") -> dateTime.substringAfter(" ").take(5)
        else -> ""
    }
}

private fun extractSortableDateTime(dateTime: String): String {
    return when {
        dateTime.contains("T") -> dateTime
        dateTime.contains(" ") -> dateTime.replace(" ", "T")
        else -> dateTime
    }
}

private fun capitalizedDayName(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "Lunes"
    DayOfWeek.TUESDAY -> "Martes"
    DayOfWeek.WEDNESDAY -> "Miércoles"
    DayOfWeek.THURSDAY -> "Jueves"
    DayOfWeek.FRIDAY -> "Viernes"
    DayOfWeek.SATURDAY -> "Sábado"
    DayOfWeek.SUNDAY -> "Domingo"
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

private fun shortMonthName(month: Int): String = when (month) {
    1 -> "Ene"
    2 -> "Feb"
    3 -> "Mar"
    4 -> "Abr"
    5 -> "May"
    6 -> "Jun"
    7 -> "Jul"
    8 -> "Ago"
    9 -> "Sep"
    10 -> "Oct"
    11 -> "Nov"
    12 -> "Dic"
    else -> "Mes"
}