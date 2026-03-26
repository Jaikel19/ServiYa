package com.example.shared.presentation.WorkerRequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkerRequestsViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val paymentReceiptRepository: IPaymentReceiptRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(WorkerRequestsUiState())
  val uiState: StateFlow<WorkerRequestsUiState> = _state.asStateFlow()

  fun loadRequests(workerId: String) {
    viewModelScope.launch {
      _state.value =
          _state.value.copy(isLoading = true, requests = emptyList(), errorMessage = null)

      appointmentRepository
          .getAppointmentsByWorker(workerId)
          .catch { e ->
            _state.value =
                _state.value.copy(
                    isLoading = false,
                    requests = emptyList(),
                    errorMessage = e.message ?: "Error fetching requests",
                )
          }
          .collectLatest { appointments ->
            val pending = appointments.filter { it.status.equals("pending", ignoreCase = true) }

            _state.value =
                _state.value.copy(isLoading = false, requests = pending, errorMessage = null)
          }
    }
  }

  fun loadPaymentPending(workerId: String) {
    viewModelScope.launch {
      _state.value =
          _state.value.copy(
              isLoadingPayments = true,
              paymentPendingAppointments = emptyList(),
              errorMessage = null,
          )

      appointmentRepository
          .getAppointmentsByWorker(workerId)
          .catch { e ->
            _state.value =
                _state.value.copy(
                    isLoadingPayments = false,
                    paymentPendingAppointments = emptyList(),
                    errorMessage = e.message ?: "Error fetching payment pending",
                )
          }
          .collectLatest { appointments ->
            val paymentPendingList =
                appointments.filter { it.status.equals("payment_pending", ignoreCase = true) }

            val pairs = mutableListOf<Pair<Appointment, PaymentReceipt>>()

            for (appointment in paymentPendingList) {
              try {
                val receipt =
                    paymentReceiptRepository.getReceiptByAppointment(appointment.id).first()

                if (
                    receipt != null &&
                        receipt.status.equals("PENDING", ignoreCase = true) &&
                        receipt.imageUrl.isNotBlank()
                ) {
                  pairs.add(Pair(appointment, receipt))
                }
              } catch (_: Exception) {}
            }

            _state.value =
                _state.value.copy(
                    isLoadingPayments = false,
                    paymentPendingAppointments = pairs,
                    errorMessage = null,
                )
          }
    }
  }

  fun acceptRequest(appointment: Appointment) {
    viewModelScope.launch {
      // Aprobar la cita
      appointmentRepository.approveAppointment(appointment.id)

      // Crear PaymentReceipt con campos vacíos y status "pending"
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

      paymentReceiptRepository.createReceipt(appointmentId = appointment.id, receipt = receipt)
    }
  }

  fun rejectRequest(appointment: Appointment) {
    viewModelScope.launch { appointmentRepository.rejectAppointmentByWorker(appointment.id) }
  }

  fun confirmPayment(appointment: Appointment, receiptId: String) {
    viewModelScope.launch {
      paymentReceiptRepository.updateReceiptStatus(
          appointmentId = appointment.id,
          receiptId = receiptId,
          status = "APPROVED",
          note = null,
          reviewedAt = null,
          reviewedBy = null,
          rejectionReason = null,
      )

      appointmentRepository.confirmPayment(appointment.id)
    }
  }

  fun reportPaymentProblem(
      appointment: Appointment,
      receiptId: String,
      note: String = "Problema con comprobante",
  ) {
    viewModelScope.launch {
      paymentReceiptRepository.updateReceiptStatus(
          appointmentId = appointment.id,
          receiptId = receiptId,
          status = "REJECTED",
          note = note,
          reviewedAt = null,
          reviewedBy = null,
          rejectionReason = note,
      )
    }
  }

  fun confirmPayment(appointment: Appointment) {
    viewModelScope.launch {
      val receipt = paymentReceiptRepository.getReceiptByAppointment(appointment.id).first()

      val receiptId = receipt?.id ?: return@launch

      confirmPayment(appointment, receiptId)
    }
  }

  fun cancelPayment(appointment: Appointment) {
    viewModelScope.launch {
      val receipt = paymentReceiptRepository.getReceiptByAppointment(appointment.id).first()

      val receiptId = receipt?.id ?: return@launch

      reportPaymentProblem(appointment, receiptId)
    }
  }

  fun refreshPaymentPending(workerId: String) {
    loadPaymentPending(workerId)
  }
}
