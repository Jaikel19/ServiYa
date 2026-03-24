package com.example.shared.presentation.clientAppointmentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.IBookingRepository
import com.example.shared.data.repository.OtpAppointment.IOtpAppointmentRepository
import com.example.shared.data.repository.ReviewMeta.IReviewMetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClientAppointmentDetailViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val otpAppointmentRepository: IOtpAppointmentRepository,
    private val bookingRepository: IBookingRepository,
    private val reviewMetaRepository: IReviewMetaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientAppointmentDetailUiState())
    val uiState: StateFlow<ClientAppointmentDetailUiState> = _uiState

    fun loadAppointmentDetail(appointmentId: String) {
        viewModelScope.launch {
            _uiState.value = ClientAppointmentDetailUiState(isLoading = true)

            try {
                val appointment = appointmentRepository
                    .getAppointmentById(appointmentId)
                    .first()

                println("CLIENT OTP DEBUG 1: appointmentId = $appointmentId")
                println("CLIENT OTP DEBUG 2: appointment = $appointment")

                if (appointment == null) {
                    _uiState.value = ClientAppointmentDetailUiState(
                        isLoading = false,
                        errorMessage = "No se encontró la cita"
                    )
                    return@launch
                }

                val otp = otpAppointmentRepository
                    .getOtpByAppointment(appointmentId)
                    .first()

                println("CLIENT OTP DEBUG 3: otp = $otp")

                val cancellationPolicy =
                    bookingRepository.getCancellationPolicyByWorkerId(appointment.workerId)

                val worker =
                    bookingRepository.getWorkerProfile(appointment.workerId)

                val reviewMeta = reviewMetaRepository
                    .getReviewMeta(appointmentId)
                    .first() ?: com.example.shared.domain.entity.ReviewMeta()

                _uiState.value = ClientAppointmentDetailUiState(
                    isLoading = false,
                    appointment = appointment,
                    otp = otp,
                    worker = worker,
                    cancellationPolicy = cancellationPolicy,
                    reviewMeta = reviewMeta,
                    errorMessage = null
                )

                println("CLIENT OTP DEBUG 4: canShowOtp = ${_uiState.value.canShowOtp}")
                println("CLIENT OTP DEBUG 5: otpCode = ${_uiState.value.otp?.code}")
                println("CLIENT REVIEW DEBUG 6: reviewMeta = ${_uiState.value.reviewMeta}")
                println("CLIENT REVIEW DEBUG 7: canReview = ${_uiState.value.canReview}")

            } catch (e: Exception) {
                println("CLIENT OTP DEBUG ERROR: ${e.message}")
                e.printStackTrace()

                _uiState.value = ClientAppointmentDetailUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar el detalle de la cita"
                )
            }
        }
    }

    fun cancelAppointmentByClient() {
        val appointment = _uiState.value.appointment ?: return

        if (appointment.status != "confirmed") {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Solo se pueden cancelar citas confirmadas"
            )
            return
        }

        viewModelScope.launch {
            try {
                appointmentRepository.cancelAppointmentByClient(appointment.id)
                loadAppointmentDetail(appointment.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cancelar la cita"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}