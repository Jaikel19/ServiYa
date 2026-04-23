package com.example.shared.presentation.workersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.workersList.IWorkersListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkersListViewModel(
    private val repository: IWorkersListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkersListUiState())
    val uiState: StateFlow<WorkersListUiState> = _uiState.asStateFlow()

    fun loadWorkers() {
        viewModelScope.launch {
            _uiState.value = WorkersListUiState(isLoading = true)

            try {
                repository.getWorkers().collect { workers ->
                    _uiState.value = WorkersListUiState(
                        isLoading = false,
                        workers = workers,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = WorkersListUiState(
                    isLoading = false,
                    workers = emptyList(),
                    errorMessage = e.message ?: "Error cargando trabajadores"
                )
            }
        }
    }
}