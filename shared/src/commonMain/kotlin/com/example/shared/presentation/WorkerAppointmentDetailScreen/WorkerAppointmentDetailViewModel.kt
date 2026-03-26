package com.example.shared.presentation.workerAppointmentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import com.example.shared.data.repository.ReviewMeta.IReviewMetaRepository
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.cancellation.AppointmentCancellationCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WorkerAppointmentDetailViewModel(
    private val paymentReceiptRepository: IPaymentReceiptRepository,
    private val reviewMetaRepository: IReviewMetaRepository,
    private val appointmentRepository: IAppointmentRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(WorkerAppointmentDetailUiState())
  val uiState: StateFlow<WorkerAppointmentDetailUiState> = _uiState.asStateFlow()

  fun loadPaymentReceipt(appointmentId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

      paymentReceiptRepository
          .getReceiptByAppointment(appointmentId)
          .onEach { receipt ->
            _uiState.value =
                _uiState.value.copy(
                    paymentReceipt = receipt,
                    isLoading = false,
                    errorMessage = null,
                )
          }
          .catch { e ->
            _uiState.value =
                _uiState.value.copy(
                    paymentReceipt = null,
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar comprobante",
                )
          }
          .collect()
    }
  }

  fun loadReviewMeta(appointmentId: String) {
    viewModelScope.launch {
      reviewMetaRepository
          .getReviewMeta(appointmentId)
          .onEach { meta ->
            _uiState.value =
                _uiState.value.copy(
                    reviewMeta = meta ?: _uiState.value.reviewMeta,
                    errorMessage = null,
                )
          }
          .catch { e ->
            _uiState.value =
                _uiState.value.copy(
                    reviewMeta = _uiState.value.reviewMeta,
                    errorMessage = e.message ?: "Error al cargar reviewMeta",
                )
          }
          .collect()
    }
  }

  fun prepareCancellationPreview(appointment: Appointment) {
    if (!appointment.status.equals("confirmed", ignoreCase = true)) {
      _uiState.value =
          _uiState.value.copy(errorMessage = "Solo se pueden cancelar citas confirmadas")
      return
    }

    _uiState.value =
        _uiState.value.copy(
            isPreparingCancellationPreview = true,
            errorMessage = null,
            successMessage = null,
        )

    val preview = AppointmentCancellationCalculator.buildWorkerPreview(appointment = appointment)

    _uiState.value =
        _uiState.value.copy(
            cancellationPreview = preview,
            showCancellationPreview = true,
            isPreparingCancellationPreview = false,
            isCancellingAppointment = false,
            cancellationCompleted = false,
            errorMessage = null,
            successMessage = null,
        )
  }

  fun dismissCancellationPreview() {
    _uiState.value =
        _uiState.value.copy(showCancellationPreview = false, cancellationPreview = null)
  }

  fun cancelAppointmentByWorker(appointment: Appointment, currentDateTime: String) {
    if (!appointment.status.equals("confirmed", ignoreCase = true)) {
      _uiState.value =
          _uiState.value.copy(errorMessage = "Solo se pueden cancelar citas confirmadas")
      return
    }

    val preview = _uiState.value.cancellationPreview
    if (preview == null) {
      _uiState.value =
          _uiState.value.copy(errorMessage = "Primero debes revisar el cálculo de reembolso")
      return
    }

    viewModelScope.launch {
      _uiState.value =
          _uiState.value.copy(
              isCancellingAppointment = true,
              errorMessage = null,
              successMessage = null,
          )

      try {
        appointmentRepository.cancelAppointmentByWorkerWithRefund(
            appointmentId = appointment.id,
            cancelledAt = currentDateTime,
            refundPercentage = preview.refundPercentage,
            refundAmount = preview.refundAmount,
            policyLabel = preview.policyLabel,
            warningMessage = preview.warningMessage,
        )

        _uiState.value =
            _uiState.value.copy(
                isCancellingAppointment = false,
                showCancellationPreview = false,
                cancellationPreview = null,
                cancellationCompleted = true,
                successMessage = "La cita fue cancelada correctamente.",
                errorMessage = null,
            )
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isCancellingAppointment = false,
                errorMessage = e.message ?: "Error al cancelar la cita",
            )
      }
    }
  }

  fun clearMessages() {
    _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
  }

  fun clearState() {
    _uiState.value = WorkerAppointmentDetailUiState()
  }
}
