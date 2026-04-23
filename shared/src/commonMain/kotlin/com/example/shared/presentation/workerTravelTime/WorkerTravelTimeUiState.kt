package com.example.shared.presentation.workerTravelTime

data class WorkerTravelTimeUiState(
    val isLoading: Boolean = false,
    val minutesText: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
) {
  val minutesValue: Int?
    get() = minutesText.toIntOrNull()

  val isValid: Boolean
    get() = minutesValue != null && minutesValue in 0..600
}
