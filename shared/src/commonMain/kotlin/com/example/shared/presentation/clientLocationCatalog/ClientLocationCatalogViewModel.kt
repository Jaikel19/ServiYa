package com.example.shared.presentation.clientLocationCatalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Address.IAddressRepository
import com.example.shared.domain.entity.Address
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClientLocationCatalogViewModel(private val repository: IAddressRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(ClientLocationCatalogUiState())
  val uiState: StateFlow<ClientLocationCatalogUiState> = _uiState.asStateFlow()

  private var loadJob: Job? = null

  fun loadAddresses(userId: String) {
    loadJob?.cancel()
    loadJob =
        viewModelScope.launch {
          _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
          try {
            repository.getAddressesByUser(userId).collect { addresses ->
              _uiState.value =
                  _uiState.value.copy(
                      isLoading = false,
                      addresses =
                          addresses.sortedWith(
                              compareByDescending<Address> { it.isDefault }
                                  .thenBy { it.alias.lowercase() }
                          ),
                      errorMessage = null,
                  )
            }
          } catch (e: Exception) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando ubicaciones",
                )
          }
        }
  }

  fun saveAddress(userId: String, address: Address) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
      try {
        if (address.id.isBlank()) {
          repository.createAddress(userId, address)
        } else {
          repository.updateAddress(userId, address)
        }
        _uiState.value = _uiState.value.copy(isSaving = false)
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isSaving = false,
                errorMessage = e.message ?: "Error guardando ubicación",
            )
      }
    }
  }

  fun deleteAddress(userId: String, addressId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isDeleting = true, errorMessage = null)
      try {
        repository.deleteAddress(userId, addressId)
        _uiState.value = _uiState.value.copy(isDeleting = false)
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isDeleting = false,
                errorMessage = e.message ?: "Error eliminando ubicación",
            )
      }
    }
  }
}
