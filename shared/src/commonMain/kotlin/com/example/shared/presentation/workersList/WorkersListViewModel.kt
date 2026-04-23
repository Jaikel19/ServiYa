package com.example.shared.presentation.workersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Address.IAddressRepository
import com.example.shared.data.repository.workersList.IWorkersListRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkersListViewModel(
    private val repository: IWorkersListRepository,
    private val addressRepository: IAddressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkersListUiState())
    val uiState: StateFlow<WorkersListUiState> = _uiState.asStateFlow()

    private var workersJob: Job? = null
    private var favoritesJob: Job? = null
    private var addressesJob: Job? = null
    private var lastRequestedFilters: WorkersListFilters? = null

    fun loadWorkers(
        filters: WorkersListFilters = _uiState.value.appliedFilters,
        force: Boolean = false,
    ) {
        val normalizedFilters = normalizeFilters(filters)

        if (!force && lastRequestedFilters == normalizedFilters) {
            _uiState.value = _uiState.value.copy(appliedFilters = normalizedFilters)
            return
        }

        workersJob?.cancel()
        lastRequestedFilters = normalizedFilters

        _uiState.value =
            _uiState.value.copy(
                isLoading = true,
                appliedFilters = normalizedFilters,
                errorMessage = null,
            )

        workersJob =
            viewModelScope.launch {
                try {
                    repository.getWorkers(normalizedFilters).collect { workers ->
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                workers = workers,
                                appliedFilters = normalizedFilters,
                                errorMessage = null,
                            )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    lastRequestedFilters = null
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            workers = emptyList(),
                            appliedFilters = normalizedFilters,
                            errorMessage = e.message ?: "Error cargando trabajadores",
                        )
                }
            }
    }

    fun reloadWorkers() {
        loadWorkers(_uiState.value.appliedFilters, force = true)
    }

    fun updateWorkersFilters(filters: WorkersListFilters) {
        loadWorkers(filters)
    }

    fun loadClientAddresses(clientId: String) {
        addressesJob?.cancel()

        addressesJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoadingAddresses = true,
                        errorMessage = null,
                    )

                try {
                    addressRepository.getAddressesByUser(clientId).collect { addresses ->
                        val currentSelected = _uiState.value.selectedAddressId

                        val selectedId =
                            when {
                                currentSelected != null &&
                                        addresses.any { it.id == currentSelected } -> currentSelected
                                else -> addresses.firstOrNull { it.isDefault }?.id
                            }

                        _uiState.value =
                            _uiState.value.copy(
                                isLoadingAddresses = false,
                                savedAddresses = addresses,
                                selectedAddressId = selectedId,
                                errorMessage = null,
                            )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoadingAddresses = false,
                            savedAddresses = emptyList(),
                            selectedAddressId = null,
                            errorMessage = e.message ?: "Error cargando direcciones",
                        )
                }
            }
    }

    fun selectAddress(addressId: String?) {
        _uiState.value = _uiState.value.copy(selectedAddressId = addressId)
    }

    fun loadFavoriteWorkerIds(clientId: String) {
        favoritesJob?.cancel()

        favoritesJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoadingFavorites = true,
                        errorMessage = null,
                    )

                try {
                    repository.getFavoriteWorkerIds(clientId).collect { favoriteIds ->
                        _uiState.value =
                            _uiState.value.copy(
                                isLoadingFavorites = false,
                                favoriteWorkerIds = favoriteIds,
                                errorMessage = null,
                            )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoadingFavorites = false,
                            errorMessage = e.message ?: "Error cargando favoritos",
                        )
                }
            }
    }

    fun toggleFavorite(clientId: String, workerId: String) {
        viewModelScope.launch {
            val currentFavorites = _uiState.value.favoriteWorkerIds
            val isCurrentlyFavorite = currentFavorites.contains(workerId)

            val optimisticFavorites =
                if (isCurrentlyFavorite) {
                    currentFavorites - workerId
                } else {
                    currentFavorites + workerId
                }

            _uiState.value =
                _uiState.value.copy(
                    favoriteWorkerIds = optimisticFavorites,
                    errorMessage = null,
                )

            try {
                if (isCurrentlyFavorite) {
                    repository.removeFavorite(clientId, workerId)
                } else {
                    repository.addFavorite(clientId, workerId)
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        favoriteWorkerIds = currentFavorites,
                        errorMessage = e.message ?: "Error actualizando favorito",
                    )
            }
        }
    }

    private fun normalizeFilters(filters: WorkersListFilters): WorkersListFilters {
        return filters.copy(
            searchQuery = filters.searchQuery.trim(),
            selectedProvince = filters.selectedProvince.trim(),
            selectedCanton = filters.selectedCanton.trim(),
            selectedDistrict = filters.selectedDistrict.trim(),
            selectedDayKey = filters.selectedDayKey.trim().lowercase(),
            selectedCategoryName = filters.selectedCategoryName?.trim(),
        )
    }
}