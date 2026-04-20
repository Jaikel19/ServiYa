package com.example.shared.presentation.workerLocationCatalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.User.IUserRepository
import com.example.shared.domain.entity.WorkZone
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkerLocationCatalogViewModel(
    private val repository: IUserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerLocationCatalogUiState())
    val uiState: StateFlow<WorkerLocationCatalogUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var currentWorkerId: String = ""

    fun loadLocations(workerId: String) {
        currentWorkerId = workerId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                repository.getWorkZonesByUser(workerId).collect { zones ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        locations = zones
                            .filter { it.alias.isNotBlank() && !it.blocked }
                            .sortedWith(
                                compareByDescending<WorkZone> { it.isDefault }
                                    .thenBy { it.alias.lowercase() }
                            ),
                        blockedZones = zones
                            .filter { it.blocked }
                            .sortedBy { it.canton.lowercase() },
                        errorMessage = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando ubicaciones",
                )
            }
        }
    }

    fun saveLocation(zone: WorkZone) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            try {
                if (zone.id.isBlank()) {
                    val newId = "loc_${currentWorkerId}_${System.currentTimeMillis()}"
                    // Si es la primera ubicación no bloqueada, marcarla como principal automáticamente
                    val isFirst = !zone.blocked && _uiState.value.locations.isEmpty()
                    repository.createWorkZone(currentWorkerId, zone.copy(id = newId, isDefault = isFirst))
                } else {
                    repository.updateWorkZone(currentWorkerId, zone)
                }
                _uiState.value = _uiState.value.copy(isSaving = false)
                reload()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Error guardando ubicación",
                )
            }
        }
    }

    fun deleteLocation(zoneId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, errorMessage = null)
            try {
                repository.deleteWorkZone(currentWorkerId, zoneId)
                _uiState.value = _uiState.value.copy(isDeleting = false)
                reload()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = e.message ?: "Error eliminando ubicación",
                )
            }
        }
    }

    fun setDefaultLocation(zoneId: String) {
        viewModelScope.launch {
            _uiState.value.locations.forEach { zone ->
                val shouldBeDefault = zone.id == zoneId
                if (zone.isDefault != shouldBeDefault) {
                    repository.updateWorkZone(currentWorkerId, zone.copy(isDefault = shouldBeDefault))
                }
            }
            reload()
        }
    }

    private suspend fun reload() {
        try {
            repository.getWorkZonesByUser(currentWorkerId).collect { zones ->
                _uiState.value = _uiState.value.copy(
                    locations = zones
                        .filter { it.alias.isNotBlank() && !it.blocked }
                        .sortedWith(
                            compareByDescending<WorkZone> { it.isDefault }
                                .thenBy { it.alias.lowercase() }
                        ),
                    blockedZones = zones
                        .filter { it.blocked }
                        .sortedBy { it.canton.lowercase() },
                )
            }
        } catch (_: Exception) {}
    }
}
