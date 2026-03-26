package com.example.shared.presentation.clientDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.IBookingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ClientDashboardViewModel(private val bookingRepository: IBookingRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(ClientDashboardUiState())
  val uiState: StateFlow<ClientDashboardUiState> = _uiState.asStateFlow()

  private var bookingsJob: Job? = null

  fun loadBookings(clientId: String) {
    if (clientId.isBlank()) return

    bookingsJob?.cancel()
    bookingsJob =
        viewModelScope.launch {
          _uiState.value =
              _uiState.value.copy(
                  clientId = clientId,
                  isLoading = true,
                  errorMessage = null,
                  bookings = emptyList(),
              )

          try {
            val clientProfile = bookingRepository.getWorkerProfile(clientId)

            _uiState.value =
                _uiState.value.copy(
                    clientId = clientId,
                    clientName = clientProfile?.name.orEmpty(),
                    clientPhotoUrl = clientProfile?.profilePicture.orEmpty(),
                )
          } catch (e: Exception) {
            println("ERROR loading client profile: ${e.message}")
          }

          bookingRepository
              .getBookingsByClient(clientId)
              .onEach { bookings ->
                val fallbackName = bookings.firstOrNull()?.clientName.orEmpty()

                _uiState.value =
                    _uiState.value.copy(
                        clientId = clientId,
                        clientName = _uiState.value.clientName.ifBlank { fallbackName },
                        bookings = bookings,
                        isLoading = false,
                        errorMessage = null,
                    )
              }
              .catch { e ->
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        bookings = emptyList(),
                        errorMessage = e.message ?: "Error al cargar las citas del cliente.",
                    )
              }
              .collect()
        }
  }
}
