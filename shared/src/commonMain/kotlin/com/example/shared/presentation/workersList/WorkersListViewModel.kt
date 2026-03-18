package com.example.shared.presentation.workersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.workersList.IWorkersListRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkersListViewModel(
    private val repository: IWorkersListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkersListUiState())
    val uiState: StateFlow<WorkersListUiState> = _uiState.asStateFlow()

    private var workersJob: Job? = null
    private var favoritesJob: Job? = null

    fun loadWorkers() {
        workersJob?.cancel()

        workersJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                repository.getWorkers().collect { workers ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        workers = workers,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workers = emptyList(),
                    errorMessage = e.message ?: "Error cargando trabajadores"
                )
            }
        }
    }

    fun loadFavoriteWorkerIds(clientId: String) {
        favoritesJob?.cancel()

        favoritesJob = viewModelScope.launch {
            try {
                repository.getFavoriteWorkerIds(clientId).collect { favoriteIds ->
                    _uiState.value = _uiState.value.copy(
                        favoriteWorkerIds = favoriteIds,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error cargando favoritos"
                )
            }
        }
    }

    fun toggleFavorite(clientId: String, workerId: String) {
        viewModelScope.launch {
            val currentFavorites = _uiState.value.favoriteWorkerIds
            val isCurrentlyFavorite = currentFavorites.contains(workerId)

            val optimisticFavorites = if (isCurrentlyFavorite) {
                currentFavorites - workerId
            } else {
                currentFavorites + workerId
            }

            _uiState.value = _uiState.value.copy(
                favoriteWorkerIds = optimisticFavorites,
                errorMessage = null
            )

            try {
                if (isCurrentlyFavorite) {
                    repository.removeFavorite(clientId, workerId)
                } else {
                    repository.addFavorite(clientId, workerId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    favoriteWorkerIds = currentFavorites,
                    errorMessage = e.message ?: "Error actualizando favorito"
                )
            }
        }
    }
}