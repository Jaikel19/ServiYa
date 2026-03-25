package com.example.shared.presentation.requestAppointment

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.AppointmentLocation
import com.example.shared.domain.entity.AppointmentService
import com.example.shared.domain.entity.Service
import com.example.shared.domain.entity.WorkerSchedule
import kotlin.math.round
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

const val REQUEST_APPOINTMENT_MONTH_SELECTION_RANGE = 6

private val APPOINTMENT_TIME_ZONE = TimeZone.of("America/Costa_Rica")

data class RequestAppointmentDraft(
    val clientId: String,
    val clientName: String,
    val workerId: String,
    val workerName: String,
    val workerImageUrl: String = "",
    val workerProvince: String = "",
    val selectedServices: List<Service>,
    val schedule: List<WorkerSchedule>,
    val workerAppointments: List<Appointment> = emptyList(),
    val travelTimeMinutes: Int = 0,
    val currentTime: CurrentTimeSnapshot = CurrentTimeSnapshot()
)

data class CurrentTimeSnapshot(
    val epochMillis: Long = 0L,
    val currentDayKey: String = "",
    val currentMinutes: Int = 0,
    val todayYear: Int = 0,
    val todayMonth: Int = 0,
    val todayDay: Int = 0
)

data class AppointmentMonthOption(
    val monthStart: LocalDate,
    val shortLabel: String,
    val yearLabel: String,
    val fullLabel: String
)

data class AppointmentDayOption(
    val date: LocalDate,
    val topLabel: String,
    val dayNumber: String,
    val fullLabel: String
)

data class AppointmentTimeOption(
    val hour: Int,
    val minute: Int,
    val label: String,
    val enabled: Boolean = true
)

private data class BusyAppointmentRange(
    val startMinutes: Int,
    val endMinutes: Int
)

private data class ExistingAppointmentSlot(
    val serviceStartMinutes: Int,
    val serviceEndMinutes: Int,
    val travelBeforeMinutes: Int
)

fun isWorkerAvailable(schedule: List<WorkerSchedule>): Boolean {
    return schedule.any { it.enabled && it.timeBlocks.isNotEmpty() }
}

fun hasUsableCurrentTime(snapshot: CurrentTimeSnapshot): Boolean {
    if (snapshot.epochMillis > 0L) return true

    val hasExplicitDate =
        snapshot.todayYear in 1..9999 &&
            snapshot.todayMonth in 1..12 &&
            snapshot.todayDay in 1..31

    return hasExplicitDate
}

fun buildAvailableMonths(
    currentTime: CurrentTimeSnapshot,
    monthsAhead: Int = REQUEST_APPOINTMENT_MONTH_SELECTION_RANGE
): List<AppointmentMonthOption> {
    val today = resolveToday(currentTime) ?: return emptyList()
    val currentMonthStart = LocalDate(
        year = today.year,
        monthNumber = today.monthNumber,
        dayOfMonth = 1
    )

    return (0 until monthsAhead.coerceAtLeast(1)).map { index ->
        val monthDate = currentMonthStart.plus(DatePeriod(months = index))
        AppointmentMonthOption(
            monthStart = monthDate,
            shortLabel = shortMonthLabel(monthDate.monthNumber),
            yearLabel = monthDate.year.toString(),
            fullLabel = fullMonthLabel(monthDate.year, monthDate.monthNumber)
        )
    }
}

fun buildAvailableDays(
    schedule: List<WorkerSchedule>,
    existingAppointments: List<Appointment>,
    currentTime: CurrentTimeSnapshot,
    selectedMonth: AppointmentMonthOption?,
    requestedServiceDurationMinutes: Int,
    requestedTravelTimeMinutes: Int
): List<AppointmentDayOption> {
    val today = resolveToday(currentTime) ?: return emptyList()
    val month = selectedMonth ?: return emptyList()

    val monthStart = month.monthStart
    val monthEnd = lastDayOfMonth(
        year = monthStart.year,
        monthNumber = monthStart.monthNumber
    )

    val startDate = if (
        monthStart.year == today.year &&
            monthStart.monthNumber == today.monthNumber
    ) {
        today
    } else {
        monthStart
    }

    if (startDate > monthEnd) return emptyList()

    val result = mutableListOf<AppointmentDayOption>()
    var cursor = startDate

    while (cursor <= monthEnd) {
        val hasWorkerSchedule = workerHasScheduleForDate(cursor, schedule)

        val slotsForDay = if (hasWorkerSchedule) {
            buildAvailableTimes(
                date = cursor,
                schedule = schedule,
                existingAppointments = existingAppointments,
                currentTime = currentTime,
                requestedServiceDurationMinutes = requestedServiceDurationMinutes,
                requestedTravelTimeMinutes = requestedTravelTimeMinutes
            )
        } else {
            emptyList()
        }

        val shouldShowDay = hasWorkerSchedule && slotsForDay.any { it.enabled }

        if (shouldShowDay) {
            result.add(
                AppointmentDayOption(
                    date = cursor,
                    topLabel = topDayLabel(cursor, today),
                    dayNumber = cursor.dayOfMonth.toString(),
                    fullLabel = fullDateLabel(cursor)
                )
            )
        }

        cursor = cursor.plus(DatePeriod(days = 1))
    }

    return result
}

fun buildAvailableTimes(
    date: LocalDate?,
    schedule: List<WorkerSchedule>,
    existingAppointments: List<Appointment>,
    currentTime: CurrentTimeSnapshot,
    requestedServiceDurationMinutes: Int,
    requestedTravelTimeMinutes: Int
): List<AppointmentTimeOption> {
    if (date == null) return emptyList()

    val scheduleForDay = findScheduleForDate(date, schedule) ?: return emptyList()
    if (!scheduleForDay.enabled || scheduleForDay.timeBlocks.isEmpty()) return emptyList()

    val validBlocks = scheduleForDay.timeBlocks.filter {
        it.start.isNotBlank() && it.end.isNotBlank()
    }
    if (validBlocks.isEmpty()) return emptyList()

    val today = resolveToday(currentTime)
    val isToday = today != null && date == today
    val currentMinutes = resolveCurrentMinutes(currentTime)

    val busyRanges = buildBusyRangesForDate(
        date = date,
        appointments = existingAppointments
    )

    val existingServiceStarts = existingAppointments
        .filter { appointment ->
            appointmentBlocksSchedule(appointment.status) &&
                appointmentBelongsToDate(appointment, date)
        }
        .mapNotNull { resolveAppointmentServiceStartMinutes(it) }
        .sorted()

    val serviceDuration = requestedServiceDurationMinutes.coerceAtLeast(30)
    val requestedTravel = requestedTravelTimeMinutes.coerceAtLeast(0)

    val slots = mutableListOf<AppointmentTimeOption>()

    validBlocks.forEach { block ->
        val blockStart = parseMinutes(block.start) ?: return@forEach
        val blockEnd = parseMinutes(block.end) ?: return@forEach
        if (blockEnd <= blockStart) return@forEach

        var cursor = blockStart

        while (cursor < blockEnd) {
            val serviceStart = cursor
            val serviceEnd = serviceStart + serviceDuration

            val isFutureTime = if (isToday) {
                serviceStart > currentMinutes
            } else {
                true
            }

            val fitsInsideWorkerSchedule =
                serviceStart >= blockStart && serviceEnd <= blockEnd

            val hasEarlierAppointment = existingServiceStarts.any { it < serviceStart }

            val effectiveBufferBefore = if (hasEarlierAppointment) {
                requestedTravel
            } else {
                0
            }

            val candidateBusyStart = (serviceStart - effectiveBufferBefore).coerceAtLeast(0)
            val candidateBusyEnd = serviceEnd

            val overlapsExistingAppointment = busyRanges.any { busy ->
                rangesOverlap(
                    startA = candidateBusyStart,
                    endA = candidateBusyEnd,
                    startB = busy.startMinutes,
                    endB = busy.endMinutes
                )
            }

            if (isFutureTime && fitsInsideWorkerSchedule && !overlapsExistingAppointment) {
                slots.add(
                    AppointmentTimeOption(
                        hour = serviceStart / 60,
                        minute = serviceStart % 60,
                        label = formatAmPm(serviceStart / 60, serviceStart % 60),
                        enabled = true
                    )
                )
            }

            cursor += 30
        }
    }

    return slots
        .distinctBy { "${it.hour}:${it.minute}" }
        .sortedWith(compareBy<AppointmentTimeOption> { it.hour }.thenBy { it.minute })
}

fun firstEnabledAppointmentTime(times: List<AppointmentTimeOption>): AppointmentTimeOption? {
    return times.firstOrNull { it.enabled }
}

fun fullDateTimeLabel(
    date: LocalDate,
    time: AppointmentTimeOption
): String {
    return "${fullDateLabel(date)} • ${time.label}"
}

fun calculateTotalServiceDurationMinutes(services: List<Service>): Int {
    return services.sumOf { parseDurationToMinutes(it.duration) }
}

fun parseDurationToMinutes(duration: String): Int {
    val text = duration.trim().lowercase()

    if (text.isBlank()) return 0

    val hourRegex = Regex("""(\d+)\s*(h|hora|horas)""")
    val minuteRegex = Regex("""(\d+)\s*(m|min|mins|minuto|minutos)""")

    val hours = hourRegex.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
    val minutes = minuteRegex.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0

    if (hours > 0 || minutes > 0) {
        return (hours * 60) + minutes
    }

    return text.toIntOrNull() ?: 0
}

fun buildPendingAppointment(
    draft: RequestAppointmentDraft,
    selectedDate: LocalDate,
    selectedTime: AppointmentTimeOption,
    selectedAddress: Address
): Appointment {
    val totalDurationMinutes = calculateTotalServiceDurationMinutes(draft.selectedServices)
    val totalCost = draft.selectedServices.sumOf { it.cost.toDouble() }

    val serviceStartEpochMillis = toAppointmentEpochMillis(
        date = selectedDate,
        time = selectedTime
    )

    val serviceStartMinutes = (selectedTime.hour * 60) + selectedTime.minute

    val existingServiceStarts = draft.workerAppointments
        .filter { appointment ->
            appointmentBlocksSchedule(appointment.status) &&
                appointmentBelongsToDate(appointment, selectedDate)
        }
        .mapNotNull { resolveAppointmentServiceStartMinutes(it) }

    val hasEarlierAppointment = existingServiceStarts.any { it < serviceStartMinutes }

    val effectiveBufferBefore = if (hasEarlierAppointment) {
        draft.travelTimeMinutes.coerceAtLeast(0)
    } else {
        0
    }

    val serviceEndEpochMillis =
        serviceStartEpochMillis + (totalDurationMinutes.coerceAtLeast(0) * 60_000L)

    val blockedStartEpochMillis =
        serviceStartEpochMillis - (effectiveBufferBefore * 60_000L)

    val blockedEndEpochMillis = serviceEndEpochMillis

    val serviceStartDateTime = Instant
        .fromEpochMilliseconds(serviceStartEpochMillis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)

    val serviceEndDateTime = Instant
        .fromEpochMilliseconds(serviceEndEpochMillis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)

    val blockedStartDateTime = Instant
        .fromEpochMilliseconds(blockedStartEpochMillis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)

    val blockedEndDateTime = Instant
        .fromEpochMilliseconds(blockedEndEpochMillis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)

    val now = safeEpochMillis(
        snapshot = draft.currentTime,
        fallback = serviceStartEpochMillis
    )

    val nowIso = epochMillisToIsoLocalDateTime(now)

    return Appointment(
        clientId = draft.clientId,
        clientName = draft.clientName,
        workerId = draft.workerId,
        workerName = draft.workerName,
        status = "pending",
        timeZone = "America/Costa_Rica",
        dateKey = formatDateKey(selectedDate),
        monthKey = formatMonthKey(selectedDate),
        dayKey = dayKeyFromWeekday(selectedDate.dayOfWeek),
        serviceStartAt = formatIsoLocalDateTime(serviceStartDateTime),
        serviceEndAt = formatIsoLocalDateTime(serviceEndDateTime),
        serviceDurationMinutes = totalDurationMinutes,
        travelTimeMinutesSnapshot = draft.travelTimeMinutes,
        bufferBeforeMinutes = effectiveBufferBefore,
        bufferAfterMinutes = 0,
        blockedStartAt = formatIsoLocalDateTime(blockedStartDateTime),
        blockedEndAt = formatIsoLocalDateTime(blockedEndDateTime),
        blockedTotalMinutes = totalDurationMinutes + effectiveBufferBefore,
        services = draft.selectedServices.map {
            AppointmentService(
                id = it.id,
                name = it.name,
                description = "",
                cost = moneyToInt(it.cost.toDouble()),
                durationMinutes = parseDurationToMinutes(it.duration),
                subtotal = moneyToInt(it.cost.toDouble())
            )
        },
        totalCost = moneyToInt(totalCost),
        currency = "CRC",
        clientAddressId = selectedAddress.id,
        location = AppointmentLocation(
            id = selectedAddress.id,
            alias = selectedAddress.alias,
            province = selectedAddress.province,
            canton = selectedAddress.canton,
            district = selectedAddress.district,
            latitude = selectedAddress.latitude,
            longitude = selectedAddress.longitude,
            reference = selectedAddress.reference
        ),
        clientToWorkerReviewDone = false,
        workerToClientReviewDone = false,
        cancellationBy = null,
        cancellationReason = null,
        cancelledAt = null,
        createdAt = nowIso,
        updatedAt = nowIso
    )
}

private fun moneyToInt(value: Double): Int {
    return round(value).toInt()
}

private fun snapshotLocalDateTime(snapshot: CurrentTimeSnapshot): LocalDateTime? {
    val millis = snapshot.epochMillis.takeIf { it > 0L } ?: return null
    return Instant
        .fromEpochMilliseconds(millis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)
}

private fun resolveToday(snapshot: CurrentTimeSnapshot): LocalDate? {
    snapshotLocalDateTime(snapshot)?.let { localDateTime ->
        return localDateTime.date
    }

    val hasExplicitDate =
        snapshot.todayYear in 1..9999 &&
            snapshot.todayMonth in 1..12 &&
            snapshot.todayDay in 1..31

    if (hasExplicitDate) {
        return LocalDate(
            year = snapshot.todayYear,
            monthNumber = snapshot.todayMonth,
            dayOfMonth = snapshot.todayDay
        )
    }

    return null
}

private fun resolveCurrentMinutes(snapshot: CurrentTimeSnapshot): Int {
    snapshotLocalDateTime(snapshot)?.let { localDateTime ->
        return (localDateTime.hour * 60) + localDateTime.minute
    }

    return snapshot.currentMinutes.coerceAtLeast(0)
}

private fun workerHasScheduleForDate(
    date: LocalDate,
    schedule: List<WorkerSchedule>
): Boolean {
    val scheduleForDay = findScheduleForDate(date, schedule) ?: return false
    return scheduleForDay.enabled && scheduleForDay.timeBlocks.any {
        it.start.isNotBlank() && it.end.isNotBlank()
    }
}

private fun safeEpochMillis(
    snapshot: CurrentTimeSnapshot,
    fallback: Long
): Long {
    return snapshot.epochMillis.takeIf { it > 0L } ?: fallback
}

private fun buildBusyRangesForDate(
    date: LocalDate,
    appointments: List<Appointment>
): List<BusyAppointmentRange> {
    val serviceAppointments = appointments
        .filter { appointment ->
            appointmentBlocksSchedule(appointment.status) &&
                appointmentBelongsToDate(appointment, date)
        }
        .mapNotNull { appointment ->
            val serviceStart = resolveAppointmentServiceStartMinutes(appointment) ?: return@mapNotNull null
            val serviceEnd = resolveAppointmentServiceEndMinutes(appointment, serviceStart) ?: return@mapNotNull null

            ExistingAppointmentSlot(
                serviceStartMinutes = serviceStart,
                serviceEndMinutes = serviceEnd,
                travelBeforeMinutes = appointment.travelTimeMinutesSnapshot.coerceAtLeast(0)
            )
        }
        .sortedBy { it.serviceStartMinutes }

    return serviceAppointments.mapIndexed { index, item ->
        val effectiveBufferBefore = if (index == 0) 0 else item.travelBeforeMinutes

        BusyAppointmentRange(
            startMinutes = (item.serviceStartMinutes - effectiveBufferBefore).coerceAtLeast(0),
            endMinutes = item.serviceEndMinutes.coerceAtMost(24 * 60)
        )
    }
}

private fun resolveAppointmentServiceStartMinutes(
    appointment: Appointment
): Int? {
    return when {
        appointment.serviceStartAt.isNotBlank() -> parseMinutes(appointment.serviceStartAt)
        else -> null
    }
}

private fun resolveAppointmentServiceEndMinutes(
    appointment: Appointment,
    startMinutes: Int
): Int? {
    if (appointment.serviceEndAt.isNotBlank()) {
        return parseMinutes(appointment.serviceEndAt)
    }

    if (appointment.serviceDurationMinutes > 0) {
        return startMinutes + appointment.serviceDurationMinutes
    }

    return startMinutes + 30
}

private fun appointmentBelongsToDate(
    appointment: Appointment,
    selectedDate: LocalDate
): Boolean {
    if (appointment.dateKey.isNotBlank()) {
        return appointment.dateKey == formatDateKey(selectedDate)
    }

    appointment.serviceStartAt
        .takeIf { it.isNotBlank() }
        ?.let { startText ->
            extractLocalDateFromText(startText)?.let { parsedDate ->
                return parsedDate == selectedDate
            }
        }

    appointment.blockedStartAt
        .takeIf { it.isNotBlank() }
        ?.let { startText ->
            extractLocalDateFromText(startText)?.let { parsedDate ->
                return parsedDate == selectedDate
            }
        }

    return false
}

private fun extractLocalDateFromText(value: String): LocalDate? {
    val text = value.trim()
    val datePart = text.substringBefore("T").substringBefore(" ")

    val parts = datePart.split("-")
    if (parts.size != 3) return null

    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].toIntOrNull() ?: return null

    return try {
        LocalDate(
            year = year,
            monthNumber = month,
            dayOfMonth = day
        )
    } catch (_: Exception) {
        null
    }
}

private fun appointmentBlocksSchedule(status: String): Boolean {
    return when (status.trim().lowercase()) {
        "cancelled", "canceled", "rejected", "completed", "complete" -> false
        else -> true
    }
}

private fun rangesOverlap(
    startA: Int,
    endA: Int,
    startB: Int,
    endB: Int
): Boolean {
    return startA < endB && endA > startB
}

private fun findScheduleForDate(
    date: LocalDate,
    schedule: List<WorkerSchedule>
): WorkerSchedule? {
    val expectedDayKey = dayKeyFromWeekday(date.dayOfWeek)
    val expectedDayNumber = dayNumberFromWeekday(date.dayOfWeek)

    return schedule.firstOrNull {
        it.enabled && (
            it.dayKey.trim().lowercase() == expectedDayKey ||
                it.dayNumber == expectedDayNumber
            )
    }
}

private fun dayNumberFromWeekday(dayOfWeek: DayOfWeek): Int {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 7
    }
}

private fun parseMinutes(value: String): Int? {
    val normalized = value
        .trim()
        .substringAfterLast("T")
        .substringAfterLast(" ")

    val match = Regex("""^(\d{1,2}):(\d{2})$""").find(normalized) ?: return null

    val hour = match.groupValues[1].toIntOrNull() ?: return null
    val minute = match.groupValues[2].toIntOrNull() ?: return null

    if (hour !in 0..23 || minute !in 0..59) return null

    return hour * 60 + minute
}

private fun formatAmPm(hour24: Int, minute: Int): String {
    val suffix = if (hour24 >= 12) "PM" else "AM"
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val minuteText = minute.toString().padStart(2, '0')
    return "${hour12.toString().padStart(2, '0')}:$minuteText $suffix"
}

private fun dayKeyFromWeekday(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "monday"
        DayOfWeek.TUESDAY -> "tuesday"
        DayOfWeek.WEDNESDAY -> "wednesday"
        DayOfWeek.THURSDAY -> "thursday"
        DayOfWeek.FRIDAY -> "friday"
        DayOfWeek.SATURDAY -> "saturday"
        DayOfWeek.SUNDAY -> "sunday"
    }
}

private fun topDayLabel(
    date: LocalDate,
    today: LocalDate
): String {
    return when {
        date == today -> "HOY"
        else -> when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "LUN"
            DayOfWeek.TUESDAY -> "MAR"
            DayOfWeek.WEDNESDAY -> "MIÉ"
            DayOfWeek.THURSDAY -> "JUE"
            DayOfWeek.FRIDAY -> "VIE"
            DayOfWeek.SATURDAY -> "SÁB"
            DayOfWeek.SUNDAY -> "DOM"
        }
    }
}

private fun fullDateLabel(date: LocalDate): String {
    val weekday = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "Lunes"
        DayOfWeek.TUESDAY -> "Martes"
        DayOfWeek.WEDNESDAY -> "Miércoles"
        DayOfWeek.THURSDAY -> "Jueves"
        DayOfWeek.FRIDAY -> "Viernes"
        DayOfWeek.SATURDAY -> "Sábado"
        DayOfWeek.SUNDAY -> "Domingo"
    }

    val month = when (date.monthNumber) {
        1 -> "enero"
        2 -> "febrero"
        3 -> "marzo"
        4 -> "abril"
        5 -> "mayo"
        6 -> "junio"
        7 -> "julio"
        8 -> "agosto"
        9 -> "septiembre"
        10 -> "octubre"
        11 -> "noviembre"
        12 -> "diciembre"
        else -> ""
    }

    return "$weekday, ${date.dayOfMonth} $month"
}

private fun toAppointmentEpochMillis(
    date: LocalDate,
    time: AppointmentTimeOption
): Long {
    val localDateTime = LocalDateTime(
        year = date.year,
        monthNumber = date.monthNumber,
        dayOfMonth = date.dayOfMonth,
        hour = time.hour,
        minute = time.minute,
        second = 0,
        nanosecond = 0
    )

    return localDateTime
        .toInstant(APPOINTMENT_TIME_ZONE)
        .toEpochMilliseconds()
}

private fun formatIsoLocalDateTime(dateTime: LocalDateTime): String {
    val year = dateTime.year.toString().padStart(4, '0')
    val month = dateTime.monthNumber.toString().padStart(2, '0')
    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "${year}-${month}-${day}T${hour}:${minute}"
}

private fun epochMillisToIsoLocalDateTime(epochMillis: Long): String {
    if (epochMillis <= 0L) return ""

    val localDateTime = Instant
        .fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)

    return formatIsoLocalDateTime(localDateTime)
}

private fun formatDateKey(date: LocalDate): String {
    val year = date.year.toString().padStart(4, '0')
    val month = date.monthNumber.toString().padStart(2, '0')
    val day = date.dayOfMonth.toString().padStart(2, '0')
    return "$year-$month-$day"
}

private fun formatMonthKey(date: LocalDate): String {
    val year = date.year.toString().padStart(4, '0')
    val month = date.monthNumber.toString().padStart(2, '0')
    return "$year-$month"
}

private fun lastDayOfMonth(
    year: Int,
    monthNumber: Int
): LocalDate {
    val firstDay = LocalDate(
        year = year,
        monthNumber = monthNumber,
        dayOfMonth = 1
    )

    return firstDay.plus(DatePeriod(months = 1, days = -1))
}

private fun shortMonthLabel(monthNumber: Int): String {
    return when (monthNumber) {
        1 -> "ENE"
        2 -> "FEB"
        3 -> "MAR"
        4 -> "ABR"
        5 -> "MAY"
        6 -> "JUN"
        7 -> "JUL"
        8 -> "AGO"
        9 -> "SEP"
        10 -> "OCT"
        11 -> "NOV"
        12 -> "DIC"
        else -> ""
    }
}

private fun fullMonthLabel(
    year: Int,
    monthNumber: Int
): String {
    val month = when (monthNumber) {
        1 -> "enero"
        2 -> "febrero"
        3 -> "marzo"
        4 -> "abril"
        5 -> "mayo"
        6 -> "junio"
        7 -> "julio"
        8 -> "agosto"
        9 -> "septiembre"
        10 -> "octubre"
        11 -> "noviembre"
        12 -> "diciembre"
        else -> ""
    }

    return "$month $year"
}