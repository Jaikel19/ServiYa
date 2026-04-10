package com.example.shared.presentation.workerAvailability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Schedule.IScheduleRepository
import com.example.shared.domain.entity.Schedule
import com.example.shared.domain.entity.TimeBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
class WorkerAvailabilityViewModel(private val scheduleRepo: IScheduleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerAvailabilityUiState())
    val uiState: StateFlow<WorkerAvailabilityUiState> = _uiState.asStateFlow()

    fun loadData(workerId: String) {
        viewModelScope.launch {
            scheduleRepo.getScheduleByUser(workerId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar disponibilidad",
                    )
                }
                .collect { schedules ->
                    val dayKeys = WorkerAvailabilityUiState.defaultDays().map { it.dayKey }.toSet()
                    val weeklyMap = schedules
                        .filter { it.dayKey in dayKeys }
                        .associateBy { it.dayKey }
                    val exceptions = schedules.filter { it.dayKey !in dayKeys }

                    val updatedDays = WorkerAvailabilityUiState.defaultDays().map { day ->
                        val saved = weeklyMap[day.dayKey]
                        if (saved != null) {
                            val block = saved.timeBlocks.firstOrNull()
                            day.copy(
                                enabled = saved.enabled,
                                startTime = block?.start?.takeIf { it.isNotBlank() } ?: "08:00",
                                endTime = block?.end?.takeIf { it.isNotBlank() } ?: "17:00",
                            )
                        } else day
                    }

                    val updatedExceptions = exceptions.map { it.toExceptionUiItem() }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        days = updatedDays,
                        exceptions = updatedExceptions,
                    )
                }
        }
    }

    fun toggleDay(dayKey: String) {
        _uiState.value = _uiState.value.copy(
            days = _uiState.value.days.map { day ->
                if (day.dayKey == dayKey) day.copy(enabled = !day.enabled) else day
            }
        )
    }

    fun updateDayTime(dayKey: String, start: String, end: String) {
        _uiState.value = _uiState.value.copy(
            days = _uiState.value.days.map { day ->
                if (day.dayKey == dayKey) day.copy(startTime = start, endTime = end) else day
            }
        )
    }

    fun openExceptionDialog() {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.toString()
        _uiState.value = _uiState.value.copy(
            showExceptionDialog = true,
            pendingDate = today,
            pendingIsUnavailable = true,
            pendingStart = "09:00",
            pendingEnd = "12:00",
        )
    }

    fun closeExceptionDialog() {
        _uiState.value = _uiState.value.copy(showExceptionDialog = false)
    }

    fun updatePendingDate(date: String) {
        _uiState.value = _uiState.value.copy(pendingDate = date)
    }

    fun updatePendingType(isUnavailable: Boolean) {
        _uiState.value = _uiState.value.copy(pendingIsUnavailable = isUnavailable)
    }

    fun updatePendingStart(start: String) {
        _uiState.value = _uiState.value.copy(pendingStart = start)
    }

    fun updatePendingEnd(end: String) {
        _uiState.value = _uiState.value.copy(pendingEnd = end)
    }

    fun confirmException(workerId: String) {
        val state = _uiState.value
        if (state.pendingDate.isBlank()) return
        if (state.exceptions.any { it.date == state.pendingDate }) return

        viewModelScope.launch {
            val timeBlocks = if (state.pendingIsUnavailable) emptyList()
            else listOf(TimeBlock(start = state.pendingStart, end = state.pendingEnd))

            val schedule = Schedule(
                dayKey = state.pendingDate,
                dayNumber = -1,
                enabled = !state.pendingIsUnavailable,
                timeBlocks = timeBlocks,
                updatedAt = Clock.System.now().toEpochMilliseconds(),

            )
            scheduleRepo.createOrUpdateSchedule(workerId, schedule)
            closeExceptionDialog()
        }
    }

    fun deleteException(workerId: String, date: String) {
        viewModelScope.launch {
            scheduleRepo.deleteSchedule(workerId, date)
        }
    }

    fun save(workerId: String) {
        val days = _uiState.value.days
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            try {
                days.forEach { day ->
                    val timeBlocks = if (day.enabled) listOf(TimeBlock(start = day.startTime, end = day.endTime))
                    else emptyList()
                    val schedule = Schedule(
                        dayKey = day.dayKey,
                        dayNumber = day.dayNumber,
                        enabled = day.enabled,
                        timeBlocks = timeBlocks,
                        updatedAt = Clock.System.now().toEpochMilliseconds(),

                    )
                    scheduleRepo.createOrUpdateSchedule(workerId, schedule)
                }
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Error al guardar",
                )
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

private fun Schedule.toExceptionUiItem(): ExceptionUiItem {
    val block = timeBlocks.firstOrNull()
    return ExceptionUiItem(
        date = dayKey,
        displayDate = formatExceptionDate(dayKey),
        isUnavailable = !enabled,
        startTime = block?.start ?: "",
        endTime = block?.end ?: "",
    )
}

private fun formatExceptionDate(date: String): String {
    // date format: "YYYY-MM-DD"
    val parts = date.split("-")
    if (parts.size != 3) return date
    val monthNames = listOf(
        "ene", "feb", "mar", "abr", "may", "jun",
        "jul", "ago", "sep", "oct", "nov", "dic"
    )
    val month = parts[1].toIntOrNull()?.minus(1) ?: return date
    val monthName = monthNames.getOrElse(month) { parts[1] }
    return "${parts[2]} $monthName ${parts[0]}"
}
