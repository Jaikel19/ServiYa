package com.example.shared.presentation.workerAvailability

data class DayUiItem(
    val dayKey: String,
    val label: String,
    val dayNumber: Int,
    val enabled: Boolean = false,
    val timeBlocks: List<TimeBlockUi> = listOf(TimeBlockUi()),
)

data class TimeBlockUi(
    val start: String = "08:00",
    val end: String = "17:00",
)

data class WorkerAvailabilityUiState(
    val isLoading: Boolean = true,
    val days: List<DayUiItem> = defaultDays(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
) {
    companion object {
        fun defaultDays() = listOf(
            DayUiItem("monday",    "Lunes",     1),
            DayUiItem("tuesday",   "Martes",    2),
            DayUiItem("wednesday", "Miércoles", 3),
            DayUiItem("thursday",  "Jueves",    4),
            DayUiItem("friday",    "Viernes",   5),
            DayUiItem("saturday",  "Sábado",    6),
            DayUiItem("sunday",    "Domingo",   7),
        )
    }
}
