package com.example.shared.presentation.favoriteWorkers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.favoriteWorkers.IFavoriteWorkersRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteWorkersViewModel(
    private val repository: IFavoriteWorkersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteWorkersUiState())
    val uiState: StateFlow<FavoriteWorkersUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadFavoriteWorkers(clientId: String) {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.value = FavoriteWorkersUiState(
                isLoading = true,
                debugMessage = "loadFavoriteWorkers iniciado. clientId=$clientId"
            )

            try {
                repository.getFavoriteWorkersByClient(clientId).collect { workers ->
                    _uiState.value = FavoriteWorkersUiState(
                        isLoading = false,
                        workers = workers,
                        errorMessage = null,
                        debugMessage = "collect ejecutado. clientId=$clientId, workers=${workers.size}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FavoriteWorkersUiState(
                    isLoading = false,
                    workers = emptyList(),
                    errorMessage = e.message ?: "Error cargando favoritos",
                    debugMessage = "catch ejecutado. clientId=$clientId, error=${e.message}"
                )
            }
        }
    }

    fun removeFavoriteByWorkerId(clientId: String, workerId: String) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(clientId, workerId)

                _uiState.value = _uiState.value.copy(
                    debugMessage = "favorito eliminado. clientId=$clientId, workerId=$workerId"
                )

                loadFavoriteWorkers(clientId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error eliminando favorito",
                    debugMessage = "error eliminando favorito. clientId=$clientId, workerId=$workerId, error=${e.message}"
                )
            }
        }
    }
}