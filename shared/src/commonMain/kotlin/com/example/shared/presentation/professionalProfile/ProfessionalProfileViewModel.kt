package com.example.shared.presentation.professionalProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfessionalProfileViewModel(private val repository: IProfessionalProfileRepository) :
    ViewModel() {

  private val _uiState = MutableStateFlow(ProfessionalProfileUiState())
  val uiState: StateFlow<ProfessionalProfileUiState> = _uiState.asStateFlow()

  private var profileJob: Job? = null
  private var favoriteJob: Job? = null

  fun loadProfile(workerId: String) {
    profileJob?.cancel()

    profileJob =
        viewModelScope.launch {
          _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

          try {
            repository
                .getProfessionalProfile(workerId)
                .combine(repository.getWorkerAppointments(workerId)) { profile, appointments ->
                  profile to appointments
                }
                .collect { (profile, appointments) ->
                  _uiState.value =
                      _uiState.value.copy(
                          isLoading = false,
                          profile = profile,
                          workerAppointments = appointments,
                          errorMessage =
                              if (profile == null) {
                                "No se encontró un perfil válido para este worker."
                              } else null,
                      )
                }
          } catch (e: Exception) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error loading profile",
                )
          }
        }
  }

  fun loadFavoriteStatus(clientId: String, workerId: String) {
    favoriteJob?.cancel()

    favoriteJob =
        viewModelScope.launch {
          try {
            repository.getFavoriteWorkerIds(clientId).collect { favoriteIds ->
              _uiState.value =
                  _uiState.value.copy(
                      isFavorite = favoriteIds.contains(workerId),
                      errorMessage = null,
                  )
            }
          } catch (e: Exception) {
            _uiState.value =
                _uiState.value.copy(errorMessage = e.message ?: "Error cargando favorito")
          }
        }
  }

  fun toggleFavorite(clientId: String, workerId: String) {
    viewModelScope.launch {
      val currentValue = _uiState.value.isFavorite
      val optimisticValue = !currentValue

      _uiState.value = _uiState.value.copy(isFavorite = optimisticValue, errorMessage = null)

      try {
        if (currentValue) {
          repository.removeFavorite(clientId, workerId)
        } else {
          repository.addFavorite(clientId, workerId)
        }
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isFavorite = currentValue,
                errorMessage = e.message ?: "Error actualizando favorito",
            )
      }
    }
  }
}
