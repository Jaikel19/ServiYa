package com.example.seviya.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.domain.entity.Booking
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel

@Composable
fun MonthlyCalendarScreen(
    viewModel: MonthlyCalendarViewModel,
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    val daysOfWeek = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
    val monthName = getMonthName(state.currentMonth)

    val totalDays = getDaysInMonth(state.currentMonth, state.currentYear)
    val firstDayOffset = getFirstDayOffset(state.currentMonth, state.currentYear)

    val calendarCells = mutableListOf<Int?>()

    repeat(firstDayOffset) { calendarCells.add(null) }
    for (day in 1..totalDays) {
        calendarCells.add(day)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Volver")
            }

            Text(
                text = "$monthName ${state.currentYear}",
                style = MaterialTheme.typography.headlineSmall
            )

            Row {
                TextButton(onClick = { viewModel.goToPreviousMonth() }) {
                    Text("<")
                }
                TextButton(onClick = { viewModel.goToNextMonth() }) {
                    Text(">")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(day, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f)
        ) {
            items(calendarCells) { day ->
                CalendarDayCell(
                    day = day,
                    bookings = if (day != null) {
                        state.bookings.filter {
                            extractDayFromDate(it.date) == day &&
                                    extractMonthFromDate(it.date) == state.currentMonth &&
                                    extractYearFromDate(it.date) == state.currentYear
                        }
                    } else {
                        emptyList()
                    },
                    onBookingClick = { booking ->
                        viewModel.selectBooking(booking)
                    }
                )
            }
        }
    }

    state.selectedBooking?.let { booking ->
        AlertDialog(
            onDismissRequest = { viewModel.clearSelectedBooking() },
            confirmButton = {
                Button(onClick = { viewModel.clearSelectedBooking() }) {
                    Text("Cerrar")
                }
            },
            title = {
                Text("Detalle de cita")
            },
            text = {
                Column {
                    Text("Servicio: ${booking.serviceName}")
                    Text("Fecha: ${booking.date}")
                    Text("Hora: ${booking.time}")
                    Text("Estado: ${booking.status}")
                    Text("Notas: ${booking.notes}")
                }
            }
        )
    }
}

@Composable
private fun CalendarDayCell(
    day: Int?,
    bookings: List<Booking>,
    onBookingClick: (Booking) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors()
    ) {
        if (day == null) {
            Box(modifier = Modifier.fillMaxSize())
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                bookings.take(3).forEach { booking ->
                    Text(
                        text = booking.time,
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .clickable { onBookingClick(booking) },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (bookings.size > 3) {
                    Text(
                        text = "+${bookings.size - 3} más",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
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
 * Devuelve cuántos espacios vacíos van antes del día 1
 * basado en que la semana empieza en lunes.
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
    val dayOfWeek = ((h + 5) % 7) // lunes = 0

    return dayOfWeek
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