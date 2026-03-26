package com.example.shared.presentation.clientAppointmentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.IBookingRepository
import com.example.shared.data.repository.OtpAppointment.IOtpAppointmentRepository
import com.example.shared.data.repository.ReviewMeta.IReviewMetaRepository
import com.example.shared.presentation.cancellation.AppointmentCancellationCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClientAppointmentDetailViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val otpAppointmentRepository: IOtpAppointmentRepository,
    private val bookingRepository: IBookingRepository,
    private val reviewMetaRepository: IReviewMetaRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(ClientAppointmentDetailUiState())
  val uiState: StateFlow<ClientAppointmentDetailUiState> = _uiState

  fun loadAppointmentDetail(
      appointmentId: String,
      successMessage: String? = _uiState.value.successMessage,
  ) {
    viewModelScope.launch {
      _uiState.value =
          _uiState.value.copy(
              isLoading = true,
              errorMessage = null,
              successMessage = successMessage,
          )

      try {
        val appointment = appointmentRepository.getAppointmentById(appointmentId).first()

        if (appointment == null) {
          _uiState.value =
              ClientAppointmentDetailUiState(
                  isLoading = false,
                  errorMessage = "No se encontró la cita",
                  successMessage = successMessage,
              )
          return@launch
        }

        val otp = otpAppointmentRepository.getOtpByAppointment(appointmentId).first()

        val cancellationPolicy =
            bookingRepository.getCancellationPolicyByWorkerId(appointment.workerId)

        val worker = bookingRepository.getWorkerProfile(appointment.workerId)

        val reviewMeta =
            reviewMetaRepository.getReviewMeta(appointmentId).first()
                ?: com.example.shared.domain.entity.ReviewMeta()

        _uiState.value =
            ClientAppointmentDetailUiState(
                isLoading = false,
                appointment = appointment,
                otp = otp,
                worker = worker,
                cancellationPolicy = cancellationPolicy,
                reviewMeta = reviewMeta,
                errorMessage = null,
                successMessage = successMessage,
            )
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                errorMessage = e.message ?: "Error al cargar el detalle de la cita",
            )
      }
    }
  }

  fun prepareCancellationPreview(currentDateTime: String) {
    val currentState = _uiState.value
    val appointment = currentState.appointment

    if (appointment == null) {
      _uiState.value = currentState.copy(errorMessage = "No se encontró la cita")
      return
    }

    if (appointment.status != "confirmed") {
      _uiState.value = currentState.copy(errorMessage = "Solo se pueden cancelar citas confirmadas")
      return
    }

    _uiState.value =
        currentState.copy(
            isPreparingCancellationPreview = true,
            errorMessage = null,
            successMessage = null,
        )

    val preview =
        AppointmentCancellationCalculator.buildClientPreview(
            appointment = appointment,
            cancellationPolicy = currentState.cancellationPolicy,
            currentDateTime = currentDateTime,
        )

    _uiState.value =
        currentState.copy(
            cancellationPreview = preview,
            showCancellationPreview = true,
            isPreparingCancellationPreview = false,
            isCancellingAppointment = false,
            successMessage = null,
            errorMessage = null,
        )
  }

  fun dismissCancellationPreview() {
    _uiState.value =
        _uiState.value.copy(showCancellationPreview = false, cancellationPreview = null)
  }

  fun cancelAppointmentByClient(currentDateTime: String) {
    val currentState = _uiState.value
    val appointment = currentState.appointment
    val preview = currentState.cancellationPreview

    if (appointment == null) {
      _uiState.value = currentState.copy(errorMessage = "No se encontró la cita")
      return
    }

    if (appointment.status != "confirmed") {
      _uiState.value = currentState.copy(errorMessage = "Solo se pueden cancelar citas confirmadas")
      return
    }

    if (preview == null) {
      _uiState.value =
          currentState.copy(errorMessage = "Primero debes revisar el cálculo de reembolso")
      return
    }

    viewModelScope.launch {
      _uiState.value =
          currentState.copy(
              isCancellingAppointment = true,
              errorMessage = null,
              successMessage = null,
          )

      try {
        appointmentRepository.cancelAppointmentByClientWithRefund(
            appointmentId = appointment.id,
            cancelledAt = currentDateTime,
            refundPercentage = preview.refundPercentage,
            refundAmount = preview.refundAmount,
            policyLabel = preview.policyLabel,
            warningMessage = preview.warningMessage,
        )

        loadAppointmentDetail(
            appointmentId = appointment.id,
            successMessage = "La cita fue cancelada correctamente.",
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

  fun clearError() {
    _uiState.value = _uiState.value.copy(errorMessage = null)
  }

  fun clearSuccess() {
    _uiState.value = _uiState.value.copy(successMessage = null)
  }
}
