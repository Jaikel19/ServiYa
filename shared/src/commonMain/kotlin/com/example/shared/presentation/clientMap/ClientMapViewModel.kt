package com.example.shared.presentation.clientMap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.User.IUserRepository
import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.WorkZone
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
    _state.value = _state.value.copy(isLoading = true, markers = emptyList(), filteredMarkers = emptyList())

    val mutex = Mutex()

    try {
      val workers = userRepository.getAllWorkers().catch { emit(emptyList()) }.first()

      coroutineScope {
        workers.map { worker ->
          async {
            val allZones = userRepository.getWorkZonesByUser(worker.uid).first()

            // Si alguna zona bloqueada del trabajador coincide con la ubicación de Ana → no mostrar
            if (workerBlockedAddress(allZones, address)) return@async

            val principalZone = allZones
                .firstOrNull { it.isDefault && !it.blocked && (it.latitude != 0.0 || it.longitude != 0.0) }
                ?: return@async

            val marker = WorkerMapMarker(user = worker, workZone = principalZone)

            // Agregar al estado en cuanto este trabajador esté listo
            mutex.withLock {
              val updated = _state.value.markers + marker
              val availableCategories = updated.flatMap { it.user.categories }.distinctBy { it.id }
              _state.value = _state.value.copy(
                  markers = updated,
                  filteredMarkers = updated,
                  availableCategories = availableCategories,
              )
              applyFilters()
            }
          }
        }.awaitAll()
      }

      _state.value = _state.value.copy(isLoading = false)
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

  // Retorna true si el trabajador bloqueó explícitamente la zona donde está Ana
  private fun workerBlockedAddress(zones: List<WorkZone>, address: Address): Boolean {
    val selectedProvince = normalizeText(address.province)
    val selectedCanton = normalizeText(address.canton)
    val selectedDistrict = normalizeText(address.district)

    if (selectedProvince.isBlank()) return false

    return zones.filter { it.blocked }.any { zone ->
      val zoneProvince = normalizeText(zone.province)
      val zoneCanton = normalizeText(zone.canton)
      val zoneDistrict = normalizeText(zone.district)

      // La provincia siempre debe coincidir
      if (zoneProvince != selectedProvince) return@any false

      when {
        // Zona a nivel provincia (sin cantón): bloquea toda la provincia
        zoneCanton.isBlank() -> true
        // Zona a nivel cantón (sin distrito): bloquea todo el cantón
        zoneDistrict.isBlank() -> zoneCanton == selectedCanton
        // Zona a nivel distrito: bloquea solo ese distrito exacto
        else -> zoneCanton == selectedCanton && zoneDistrict == selectedDistrict
      }
    }
  }

  private fun normalizeText(value: String): String =
      value.trim().lowercase()
          .replace("á", "a").replace("é", "e").replace("í", "i")
          .replace("ó", "o").replace("ú", "u")
          .replace("ä", "a").replace("ë", "e").replace("ï", "i")
          .replace("ö", "o").replace("ü", "u")
          .replace("ñ", "n")
}
