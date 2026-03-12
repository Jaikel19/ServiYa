package com.example.shared.presentation.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Service.IServiceRepository
import com.example.shared.domain.entity.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ServicesViewModel(
    private val serviceRepository: IServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    fun loadServices(workerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                services = emptyList(),
                errorMessage = null
            )
            serviceRepository.getServicesByWorker(workerId)
                .onEach { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        services = list,
                        errorMessage = null
                    )
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        services = emptyList(),
                        errorMessage = e.message ?: "Error fetching services"
                    )
                }
                .collect()
        }
    }

    fun createService(workerId: String, service: Service) {
        viewModelScope.launch {
            try {
                serviceRepository.createService(workerId, service)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error creating service"
                )
            }
        }
    }

    fun updateService(workerId: String, service: Service) {
        viewModelScope.launch {
            try {
                serviceRepository.updateService(workerId, service)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error updating service"
                )
            }
        }
    }
}