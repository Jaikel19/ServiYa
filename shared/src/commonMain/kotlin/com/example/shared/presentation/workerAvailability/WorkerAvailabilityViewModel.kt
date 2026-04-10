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
                        errorMessage = e.message ?: "Error loading availability",
                    )
                }
                .collect { schedules ->
                    val dayKeys = WorkerAvailabilityUiState.defaultDays().map { it.dayKey }.toSet()
                    val weeklyMap = schedules
                        .filter { it.dayKey in dayKeys }
                        .associateBy { it.dayKey }

                    val updatedDays = WorkerAvailabilityUiState.defaultDays().map { day ->
                        val saved = weeklyMap[day.dayKey]
                        if (saved != null) {
                            val blocks = if (saved.timeBlocks.isNotEmpty()) {
                                saved.timeBlocks.map { TimeBlockUi(start = it.start, end = it.end) }
                            } else {
                                listOf(TimeBlockUi())
                            }
                            day.copy(enabled = saved.enabled, timeBlocks = blocks)
                        } else day
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        days = updatedDays,
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

    fun addTimeBlock(dayKey: String) {
        _uiState.value = _uiState.value.copy(
            days = _uiState.value.days.map { day ->
                if (day.dayKey == dayKey)
                    day.copy(timeBlocks = day.timeBlocks + TimeBlockUi())
                else day
            }
        )
    }

    fun removeTimeBlock(dayKey: String, index: Int) {
        _uiState.value = _uiState.value.copy(
            days = _uiState.value.days.map { day ->
                if (day.dayKey == dayKey && day.timeBlocks.size > 1)
                    day.copy(timeBlocks = day.timeBlocks.toMutableList().also { it.removeAt(index) })
                else day
            }
        )
    }

    fun updateTimeBlock(dayKey: String, index: Int, start: String, end: String) {
        _uiState.value = _uiState.value.copy(
            days = _uiState.value.days.map { day ->
                if (day.dayKey == dayKey) {
                    val updated = day.timeBlocks.toMutableList()
                    if (index in updated.indices) updated[index] = TimeBlockUi(start, end)
                    day.copy(timeBlocks = updated)
                } else day
            }
        )
    }

    fun save(workerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            try {
                _uiState.value.days.forEach { day ->
                    val timeBlocks = if (day.enabled)
                        day.timeBlocks.map { TimeBlock(start = it.start, end = it.end) }
                    else emptyList()

                    scheduleRepo.createOrUpdateSchedule(
                        workerId,
                        Schedule(
                            dayKey = day.dayKey,
                            dayNumber = day.dayNumber,
                            enabled = day.enabled,
                            timeBlocks = timeBlocks,
                            updatedAt = Clock.System.now().toEpochMilliseconds(),
                        )
                    )
                }
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Error saving availability",
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
