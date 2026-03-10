package com.example.seviya.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shared.domain.entity.Booking
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Apps
import compose.icons.tablericons.Bell
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.ChevronLeft
import compose.icons.tablericons.ChevronRight
import compose.icons.tablericons.Globe
import compose.icons.tablericons.Menu2
import compose.icons.tablericons.Search
import compose.icons.tablericons.Settings

private object AgendaColors {
    val Blue = Color(0xFF004AAD)
    val BlueDark = Color(0xFF033B8A)
    val BlueSoft = Color(0xFFEFF5FF)
    val BlueChip = Color(0xFFF3F7FF)
    val BlueLine = Color(0xFF1E5CC6)
    val BlueText = Color(0xFF0C4AAA)

    val Background = Color(0xFFF4F5F8)
    val Surface = Color(0xFFFFFFFF)
    val Border = Color(0xFFDCE2EA)
    val Muted = Color(0xFF97A3B6)
    val Text = Color(0xFF1D2433)
    val Red = Color(0xFFE53935)
    val White = Color(0xFFFFFFFF)
}

@Composable
fun MonthlyCalendarScreen(
    viewModel: MonthlyCalendarViewModel,
    onBack: () -> Unit = {},
    onGoServices: () -> Unit = {},
    onGoMap: () -> Unit = {},
    onGoSearch: () -> Unit = {},
    onGoAlerts: () -> Unit = {},
    onGoMenu: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val isMonthMode = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadBookings("worker_123")
    }

    val daysOfWeek = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
    val monthName = getMonthName(state.currentMonth)

    val totalDays = getDaysInMonth(state.currentMonth, state.currentYear)
    val firstDayOffset = getFirstDayOffset(state.currentMonth, state.currentYear)

    val selectedDay = state.selectedBooking?.let { extractDayFromDate(it.date) }

    val referenceDay = selectedDay ?: 1

    val cells = mutableListOf<Int?>()
    repeat(firstDayOffset) { cells.add(null) }
    for (day in 1..totalDays) cells.add(day)
    while (cells.size % 7 != 0) {
        cells.add(null)
    }

    val weeks = cells.chunked(7)

    val visibleWeeks = if (isMonthMode.value) {
        weeks
    } else {
        listOf(
            getWeekForDay(
                day = referenceDay,
                month = state.currentMonth,
                year = state.currentYear
            )
        )
    }

    Scaffold(
        containerColor = AgendaColors.Background,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            AgendaBottomBar(
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
                .background(AgendaColors.Background)
                .padding(padding)
        ) {
            AgendaHeader(
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$monthName ${state.currentYear}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AgendaColors.Text
                            ),
                            modifier = Modifier.weight(1f),
                            maxLines = 2
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MonthArrow(
                                icon = TablerIcons.ChevronLeft,
                                onClick = { viewModel.goToPreviousMonth() }
                            )

                            MonthArrow(
                                icon = TablerIcons.ChevronRight,
                                onClick = { viewModel.goToNextMonth() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    MonthWeekToggle(
                        isMonthMode = isMonthMode.value,
                        onMonthClick = { isMonthMode.value = true },
                        onWeekClick = { isMonthMode.value = false }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (state.isLoading) {
                    Text(
                        text = "Cargando citas...",
                        color = AgendaColors.Muted,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                state.errorMessage?.let { error ->
                    Text(
                        text = "Error: $error",
                        color = AgendaColors.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = AgendaColors.Surface),
                    border = BorderStroke(1.dp, AgendaColors.Border),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9FBFD))
                        ) {
                            daysOfWeek.forEachIndexed { index, day ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 0.5.dp,
                                            color = AgendaColors.Border
                                        )
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day,
                                        color = if (index == 6) AgendaColors.Red else AgendaColors.Muted,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.height(if (isMonthMode.value) 460.dp else 92.dp)
                        ) {
                            visibleWeeks.forEach { week ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    week.forEach { day ->
                                        val dayBookings = if (day != null) {
                                            state.bookings.filter {
                                                extractDayFromDate(it.date) == day &&
                                                        extractMonthFromDate(it.date) == state.currentMonth &&
                                                        extractYearFromDate(it.date) == state.currentYear
                                            }
                                        } else {
                                            emptyList()
                                        }

                                        AgendaDayCell(
                                            day = day,
                                            bookings = dayBookings,
                                            isSelected = selectedDay == day,
                                            onBookingClick = { booking ->
                                                viewModel.selectBooking(booking)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(color = AgendaColors.BlueLine)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OCUPADO",
                        color = Color(0xFF667085),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.width(28.dp))

                    LegendDot(color = Color(0xFFD9E0EA))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DISPONIBLE",
                        color = Color(0xFF667085),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }

    state.selectedBooking?.let { booking ->
        AlertDialog(
            onDismissRequest = { viewModel.clearSelectedBooking() },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearSelectedBooking() },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Cerrar")
                }
            },
            title = {
                Text(
                    text = "Detalle de cita",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Servicio: ${booking.serviceName}")
                    Text("Fecha: ${booking.date}")
                    Text("Hora: ${booking.time}")
                    Text("Estado: ${booking.status}")
                    Text("Notas: ${booking.notes}")
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color(0xFFF8F4FB)
        )
    }
}

@Composable
private fun AgendaHeader(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .background(AgendaColors.Blue)
    ) {
        Surface(
            modifier = Modifier
                .padding(start = 20.dp, top = 24.dp)
                .clip(RoundedCornerShape(999.dp))
                .clickable { onBack() },
            shape = RoundedCornerShape(999.dp),
            color = Color.White.copy(alpha = 0.13f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Servi",
                        color = AgendaColors.White,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "Ya",
                        color = Color(0xFFEF4444),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 20.dp, top = 28.dp),
            shape = RoundedCornerShape(999.dp),
            color = Color.White.copy(alpha = 0.13f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = TablerIcons.CalendarEvent,
                    contentDescription = null,
                    tint = AgendaColors.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AGENDA",
                    color = AgendaColors.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        }
    }
}

@Composable
private fun MonthArrow(
    icon: ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(28.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AgendaColors.Muted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun MonthWeekToggle(
    isMonthMode: Boolean,
    onMonthClick: () -> Unit,
    onWeekClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFE8EDF3))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ToggleChip(
            selected = isMonthMode,
            text = "Mes",
            icon = TablerIcons.Apps,
            onClick = onMonthClick,
            modifier = Modifier.width(84.dp)
        )

        ToggleChip(
            selected = !isMonthMode,
            text = "Sem",
            icon = TablerIcons.Bell,
            onClick = onWeekClick,
            modifier = Modifier.width(84.dp)
        )
    }
}

@Composable
private fun ToggleChip(
    selected: Boolean,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) AgendaColors.Surface else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) AgendaColors.BlueText else AgendaColors.Muted,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            color = if (selected) AgendaColors.BlueText else AgendaColors.Muted,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun RowScope.AgendaDayCell(
    day: Int?,
    bookings: List<Booking>,
    isSelected: Boolean,
    onBookingClick: (Booking) -> Unit
) {
    val borderColor = if (isSelected) AgendaColors.BlueLine else AgendaColors.Border
    val borderWidth = if (isSelected) 2.dp else 0.5.dp

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .then(
                if (day == null) {
                    Modifier.background(AgendaColors.Surface)
                } else {
                    Modifier
                        .border(borderWidth, borderColor)
                        .background(AgendaColors.Surface)
                }
            )
            .padding(horizontal = 3.dp, vertical = 4.dp)
    ) {
        if (day != null) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = day.toString(),
                        color = AgendaColors.Muted,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                bookings.take(4).forEach { booking ->
                    AgendaBookingChip(
                        text = booking.time,
                        onClick = { onBookingClick(booking) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (bookings.size > 4) {
                    Text(
                        text = "+${bookings.size - 4} más",
                        color = AgendaColors.BlueText,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AgendaBookingChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        color = AgendaColors.BlueChip
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(14.dp)
                    .background(AgendaColors.BlueLine)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = text,
                color = AgendaColors.BlueText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun AgendaBottomBar(
    onGoServices: () -> Unit,
    onGoMap: () -> Unit,
    onGoSearch: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoMenu: () -> Unit
) {
    NavigationBar(
        containerColor = AgendaColors.Surface,
        tonalElevation = 10.dp,
        modifier = Modifier
            .border(1.dp, Color(0xFFE9EDF2))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        BottomItem("Servicios", TablerIcons.Apps, false, onGoServices)
        BottomItem("Mapa", TablerIcons.Globe, false, onGoMap)
        BottomItem("Buscar", TablerIcons.Search, false, onGoSearch)
        BottomItem("ALERTAS", TablerIcons.Bell, false, onGoAlerts)
        BottomItem("Menú", TablerIcons.Menu2, true, onGoMenu)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.BottomItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val color = if (active) AgendaColors.Blue else Color(0xFF98A2B3)

    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (active) Color(0xFFE7EDF5) else Color.Transparent)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            }
        },
        label = {
            Text(
                text = label,
                color = color,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium
                )
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}

private fun getMonthName(month: Int): String {
    return when (month) {
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
}

private fun getDaysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

/**
 * Semana empezando en lunes.
 */
private fun getFirstDayOffset(month: Int, year: Int): Int {
    val q = 1
    var m = month
    var y = year

    if (m < 3) {
        m += 12
        y -= 1
    }

    val k = y % 100
    val j = y / 100
    val h = (q + (13 * (m + 1)) / 5 + k + (k / 4) + (j / 4) + (5 * j)) % 7
    return ((h + 5) % 7)
}

private fun extractDayFromDate(date: String): Int {
    return date.split("-").getOrNull(2)?.toIntOrNull() ?: 0
}

private fun extractMonthFromDate(date: String): Int {
    return date.split("-").getOrNull(1)?.toIntOrNull() ?: 0
}

private fun extractYearFromDate(date: String): Int {
    return date.split("-").getOrNull(0)?.toIntOrNull() ?: 0
}

private fun getWeekForDay(
    day: Int,
    month: Int,
    year: Int
): List<Int?> {
    val totalDays = getDaysInMonth(month, year)
    val firstDayOffset = getFirstDayOffset(month, year)

    val cells = mutableListOf<Int?>()
    repeat(firstDayOffset) { cells.add(null) }
    for (d in 1..totalDays) cells.add(d)
    while (cells.size % 7 != 0) {
        cells.add(null)
    }

    val weeks = cells.chunked(7)
    return weeks.firstOrNull { week -> week.contains(day) } ?: weeks.first()
}