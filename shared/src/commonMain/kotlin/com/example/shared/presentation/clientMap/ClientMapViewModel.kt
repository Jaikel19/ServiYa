package com.example.shared.presentation.clientMap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.User.IUserRepository
import com.example.shared.domain.entity.Address
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClientMapViewModel(private val userRepository: IUserRepository) : ViewModel() {

  private val _state = MutableStateFlow(ClientMapUiState())
  val uiState: StateFlow<ClientMapUiState> = _state.asStateFlow()

  fun loadMap(clientId: String) {
    viewModelScope.launch {
      _state.value = _state.value.copy(isLoading = true, errorMessage = null)

      try {
        // Cargar addresses del cliente
        val addresses = userRepository.getAddressesByUser(clientId).first()

        val defaultAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()

        _state.value = _state.value.copy(addresses = addresses, clientAddress = defaultAddress)

        defaultAddress?.let { address -> loadMarkersForAddress(address) }
            ?: run { _state.value = _state.value.copy(isLoading = false) }
      } catch (e: Exception) {
        _state.value =
            _state.value.copy(isLoading = false, errorMessage = e.message ?: "Error cargando mapa")
      }
    }
  }

  fun selectAddress(address: Address) {
    _state.value = _state.value.copy(clientAddress = address)
    viewModelScope.launch { loadMarkersForAddress(address) }
  }

  fun selectMarker(marker: WorkerMapMarker) {
    _state.value = _state.value.copy(selectedMarker = marker)
  }

  fun clearSelectedMarker() {
    _state.value = _state.value.copy(selectedMarker = null)
  }

  private suspend fun loadMarkersForAddress(address: Address) {
    _state.value = _state.value.copy(isLoading = true)

    try {
      val workers = userRepository.getAllWorkers().catch { emit(emptyList()) }.first()

      val markers = coroutineScope {
        workers
            .map { worker ->
              async {
                // Solo la zona principal (isDefault = true) y no bloqueada
                val principalZone = userRepository
                    .getWorkZonesByUser(worker.uid).first()
                    .firstOrNull { it.isDefault && !it.blocked && (it.latitude != 0.0 || it.longitude != 0.0) }

                principalZone?.let { zone -> WorkerMapMarker(user = worker, workZone = zone) }
              }
            }
            .awaitAll()
            .filterNotNull()
      }

      val availableCategories = markers.flatMap { it.user.categories }.distinctBy { it.id }

      _state.value =
          _state.value.copy(
              isLoading = false,
              markers = markers,
              filteredMarkers = markers,
              availableCategories = availableCategories,
          )
    } catch (e: Exception) {
      _state.value =
          _state.value.copy(
              isLoading = false,
              errorMessage = e.message ?: "Error cargando trabajadores",
          )
    }
  }

  fun onSearchQueryChanged(query: String) {
    _state.value = _state.value.copy(searchQuery = query)
    applyFilters()
  }

  fun onCategorySelected(categoryId: String?) {
    _state.value = _state.value.copy(selectedCategoryId = categoryId)
    applyFilters()
  }

  fun onCategoryQueryChanged(query: String) {
    _state.value = _state.value.copy(categoryQuery = query)
    println("DEBUG categoryQuery: $query")
    println(
        "DEBUG markers categorias: ${_state.value.markers.map { it.user.name + " -> " + it.user.categories.map { c -> c.name } }}"
    )
    applyFilters()
  }

  private fun applyFilters() {
    val query = _state.value.searchQuery.trim().lowercase()
    val categoryQuery = _state.value.categoryQuery.trim().lowercase()
    val minStars = _state.value.minStars
    val allMarkers = _state.value.markers

    val filtered =
        allMarkers.filter { marker ->
          val matchesName = query.isEmpty() || marker.user.name.lowercase().contains(query)
          val matchesCategory =
              categoryQuery.isEmpty() ||
                  marker.user.categories.any { it.name.lowercase().contains(categoryQuery) }
          val matchesStars = minStars == null || (marker.user.stars ?: 0.0) >= minStars
          matchesName && matchesCategory && matchesStars
        }

    _state.value = _state.value.copy(filteredMarkers = filtered)
  }

  fun onMinStarsSelected(stars: Double?) {
    _state.value = _state.value.copy(minStars = stars)
    applyFilters()
  }
}
