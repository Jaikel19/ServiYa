package com.example.shared.presentation.professionalProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfessionalProfileViewModel(
    private val repository: IProfessionalProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfessionalProfileUiState())
    val uiState: StateFlow<ProfessionalProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(workerId: String) {
        viewModelScope.launch {
            _uiState.value = ProfessionalProfileUiState(isLoading = true)

            try {
                repository.getProfessionalProfile(workerId)
                    .combine(repository.getWorkerAppointments(workerId)) { profile, appointments ->
                        ProfessionalProfileUiState(
                            isLoading = false,
                            profile = profile,
                            workerAppointments = appointments,
                            errorMessage = if (profile == null) {
                                "No se encontró un perfil válido para este worker."
                            } else null
                        )
                    }
                    .collect { newState ->
                        _uiState.value = newState
                    }
            } catch (e: Exception) {
                _uiState.value = ProfessionalProfileUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Error loading profile"
                )
            }
        }
    }
}