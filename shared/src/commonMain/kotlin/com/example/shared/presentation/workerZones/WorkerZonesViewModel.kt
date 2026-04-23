package com.example.shared.presentation.workerZones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.User.IUserRepository
import com.example.shared.domain.entity.WorkZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WorkerZonesViewModel(
    private val userRepository: IUserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerZonesUiState(isLoading = true))
    val uiState: StateFlow<WorkerZonesUiState> = _uiState.asStateFlow()

    private var currentWorkerId: String = ""

    fun loadData(workerId: String) {
        currentWorkerId = workerId
        viewModelScope.launch {
            userRepository.getWorkZonesByUser(workerId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar zonas",
                    )
                }
                .collect { zones ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        workerZones = zones,
                    )
                }
        }
    }

    // Agrega o elimina una zona de la subcolección
    fun toggleZone(zoneItem: ZoneItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            try {
                val isServed = zoneItem.id in _uiState.value.servedZoneIds
                if (isServed) {
                    userRepository.deleteWorkZone(currentWorkerId, zoneItem.id)
                } else {
                    val zone = WorkZone(
                        id = zoneItem.id,
                        province = zoneItem.province,
                        canton = zoneItem.canton,
                        district = zoneItem.district,
                        locationCode = zoneItem.locationCode,
                        blocked = false,
                        latitude = zoneItem.latitude,
                        longitude = zoneItem.longitude,
                    )
                    userRepository.createWorkZone(currentWorkerId, zone)
                }
                // Reload
                reloadZones()
                _uiState.value = _uiState.value.copy(isSaving = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Error al actualizar zona",
                )
            }
        }
    }

    // Bloquea o desbloquea una zona que ya existe en la subcolección
    fun toggleBlocked(zoneId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            try {
                val currentlyBlocked = zoneId in _uiState.value.blockedZoneIds
                userRepository.updateWorkZoneBlocked(currentWorkerId, zoneId, !currentlyBlocked)
                reloadZones()
                _uiState.value = _uiState.value.copy(isSaving = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Error al bloquear zona",
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private suspend fun reloadZones() {
        userRepository.getWorkZonesByUser(currentWorkerId)
            .catch { }
            .collect { zones ->
                _uiState.value = _uiState.value.copy(workerZones = zones)
            }
    }
}
