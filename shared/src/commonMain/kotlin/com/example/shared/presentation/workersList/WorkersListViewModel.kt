package com.example.shared.presentation.workersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Address.IAddressRepository
import com.example.shared.data.repository.workersList.IWorkersListRepository
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

  fun loadWorkers() {
    workersJob?.cancel()

    workersJob =
        viewModelScope.launch {
          _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

          try {
            repository.getWorkers().collect { workers ->
              _uiState.value =
                  _uiState.value.copy(isLoading = false, workers = workers, errorMessage = null)
            }
          } catch (e: Exception) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    workers = emptyList(),
                    errorMessage = e.message ?: "Error cargando trabajadores",
                )
          }
        }
  }

  fun loadClientAddresses(clientId: String) {
    addressesJob?.cancel()

    addressesJob =
        viewModelScope.launch {
          _uiState.value = _uiState.value.copy(isLoadingAddresses = true, errorMessage = null)

          try {
            addressRepository.getAddressesByUser(clientId).collect { addresses ->
              val currentSelected = _uiState.value.selectedAddressId

              val selectedId =
                  when {
                    currentSelected != null && addresses.any { it.id == currentSelected } ->
                        currentSelected
                    else -> null
                  }

              _uiState.value =
                  _uiState.value.copy(
                      isLoadingAddresses = false,
                      savedAddresses = addresses,
                      selectedAddressId = selectedId,
                      errorMessage = null,
                  )
            }
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
          _uiState.value = _uiState.value.copy(isLoadingFavorites = true, errorMessage = null)

          try {
            repository.getFavoriteWorkerIds(clientId).collect { favoriteIds ->
              _uiState.value =
                  _uiState.value.copy(
                      isLoadingFavorites = false,
                      favoriteWorkerIds = favoriteIds,
                      errorMessage = null,
                  )
            }
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
          _uiState.value.copy(favoriteWorkerIds = optimisticFavorites, errorMessage = null)

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
}
