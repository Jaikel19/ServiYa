package com.example.shared.presentation.workerStartAppointmentOtp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.OtpAppointment.IOtpAppointmentRepository
import com.example.shared.utils.DateTimeUtils
import com.example.shared.utils.OtpUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkerStartAppointmentOtpViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val otpAppointmentRepository: IOtpAppointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerStartAppointmentOtpUiState())
    val uiState: StateFlow<WorkerStartAppointmentOtpUiState> = _uiState.asStateFlow()

    fun loadData(appointmentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val appointment = appointmentRepository
                    .getAppointmentById(appointmentId)
                    .first()

                val otp = otpAppointmentRepository
                    .getOtpByAppointment(appointmentId)
                    .first()

                _uiState.value = _uiState.value.copy(
                    appointment = appointment,
                    otp = otp,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando OTP"
                )
            }
        }
    }

    fun onOtpChanged(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(otpInput = digitsOnly)
    }

    fun startAppointmentWithOtp() {
        val appointment = _uiState.value.appointment ?: return
        val otp = _uiState.value.otp ?: return
        val input = _uiState.value.otpInput

        if (!appointment.status.equals("confirmed", ignoreCase = true)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Solo se puede iniciar una cita confirmada"
            )
            return
        }

        if (input.length != 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Ingresa el OTP completo"
            )
            return
        }

        viewModelScope.launch {
            try {
                val inputHash = OtpUtils.sha256(input)

                if (otp.codeHash != inputHash) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "OTP incorrecto"
                    )
                    return@launch
                }

                appointmentRepository.startAppointment(appointment.id)

                otpAppointmentRepository.markOtpAsUsed(
                    appointmentId = appointment.id,
                    otpId = "current",
                    usedAt = DateTimeUtils.nowIsoMinute()
                )

                _uiState.value = _uiState.value.copy(
                    startSuccess = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error iniciando cita"
                )
            }
        }
    }
}