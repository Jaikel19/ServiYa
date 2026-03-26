package com.example.shared.presentation.workerTravelTime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WorkerTravelTimeViewModel(private val profileRepository: IProfessionalProfileRepository) :
    ViewModel() {

  private val _uiState = MutableStateFlow(WorkerTravelTimeUiState(isLoading = true))
  val uiState: StateFlow<WorkerTravelTimeUiState> = _uiState.asStateFlow()

  fun loadData(workerId: String) {
    viewModelScope.launch {
      profileRepository
          .getWorkerTravelTime(workerId)
          .catch { e ->
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar datos",
                )
          }
          .collect { minutes ->
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    minutesText = if (minutes > 0) minutes.toString() else "",
                )
          }
    }
  }

  fun updateMinutesText(text: String) {
    _uiState.value = _uiState.value.copy(minutesText = text.filter { it.isDigit() }.take(3))
  }

  fun save(workerId: String) {
    val minutes = _uiState.value.minutesValue ?: return
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
      try {
        profileRepository.updateTravelTime(workerId, minutes)
        _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(isSaving = false, errorMessage = e.message ?: "Error al guardar")
      }
    }
  }

  fun clearSaveSuccess() {
    _uiState.value = _uiState.value.copy(saveSuccess = false)
  }
}
