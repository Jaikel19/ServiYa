package com.example.shared.presentation.WorkerRequestDetailViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import com.example.shared.domain.entity.PaymentReceipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkerRequestDetailViewModel(
    private val repository: IAppointmentRepository,
    private val paymentReceiptRepository: IPaymentReceiptRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(WorkerRequestDetailUiState())
  val uiState: StateFlow<WorkerRequestDetailUiState> = _uiState.asStateFlow()

  fun loadBooking(appointmentId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
      try {
        repository
            .getAppointmentById(appointmentId)
            .catch { e ->
              _uiState.value =
                  _uiState.value.copy(
                      isLoading = false,
                      errorMessage = e.message ?: "Error cargando solicitud",
                  )
            }
            .first()
            .let { appointment ->
              _uiState.value = _uiState.value.copy(isLoading = false, appointment = appointment)
            }
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                errorMessage = e.message ?: "Error cargando solicitud",
            )
      }
    }
  }

  fun acceptRequest() {
    val appointmentId = _uiState.value.appointment?.id ?: return
    viewModelScope.launch {
      // Aprobar la cita
      repository.approveAppointment(appointmentId)

      // Crear PaymentReceipt vacío con status "pending"
      val receipt =
          PaymentReceipt(
              id = "",
              attemptNumber = 0L,
              imageUrl = "",
              note = null,
              sentAt = "",
              reviewedAt = null,
              reviewedBy = null,
              rejectionReason = null,
              status = "pending",
          )

      paymentReceiptRepository.createReceipt(appointmentId = appointmentId, receipt = receipt)
    }
  }

  fun rejectRequest() {
    val bookingId = _uiState.value.appointment?.id ?: return
    viewModelScope.launch { repository.rejectAppointmentByWorker(bookingId) }
  }
}
