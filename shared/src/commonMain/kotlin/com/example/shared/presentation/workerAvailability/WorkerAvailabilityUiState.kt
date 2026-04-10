package com.example.shared.presentation.workerAvailability

data class DayUiItem(
    val dayKey: String,
    val label: String,
    val dayNumber: Int,
    val enabled: Boolean = false,
    val startTime: String = "08:00",
    val endTime: String = "17:00",
)

data class ExceptionUiItem(
    val date: String,
    val displayDate: String,
    val isUnavailable: Boolean,
    val startTime: String = "",
    val endTime: String = "",
)

data class WorkerAvailabilityUiState(
    val isLoading: Boolean = true,
    val days: List<DayUiItem> = defaultDays(),
    val exceptions: List<ExceptionUiItem> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showExceptionDialog: Boolean = false,
    val pendingDate: String = "",
    val pendingIsUnavailable: Boolean = true,
    val pendingStart: String = "09:00",
    val pendingEnd: String = "12:00",
) {
    companion object {
        fun defaultDays() = listOf(
            DayUiItem("monday",    "Lunes",      1),
            DayUiItem("tuesday",   "Martes",     2),
            DayUiItem("wednesday", "Miércoles",  3),
            DayUiItem("thursday",  "Jueves",     4),
            DayUiItem("friday",    "Viernes",    5),
            DayUiItem("saturday",  "Sábado",     6),
            DayUiItem("sunday",    "Domingo",    7),
        )
    }
}
