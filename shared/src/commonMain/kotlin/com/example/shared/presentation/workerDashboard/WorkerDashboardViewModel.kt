package com.example.shared.presentation.workerDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.IBookingRepository
import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WorkerDashboardViewModel(
    private val bookingRepository: IBookingRepository,
    private val professionalProfileRepository: IProfessionalProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerDashboardUiState(isLoading = true))
    val uiState: StateFlow<WorkerDashboardUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var loadedWorkerId: String? = null

    fun load(workerId: String) {
        if (loadedWorkerId == workerId && observeJob?.isActive == true) return

        loadedWorkerId = workerId
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            _uiState.value = WorkerDashboardUiState(isLoading = true)

            combine(
                professionalProfileRepository.getProfessionalProfile(workerId),
                bookingRepository.getBookingsByWorker(workerId)
            ) { profile, bookings ->
                WorkerDashboardUiState(
                    isLoading = false,
                    profile = profile,
                    bookings = bookings
                )
            }
                .catch { e ->
                    _uiState.value = WorkerDashboardUiState(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar el dashboard del trabajador."
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}