package com.example.shared.presentation.workerAppointmentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import com.example.shared.data.repository.ReviewMeta.IReviewMetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WorkerAppointmentDetailViewModel(
    private val paymentReceiptRepository: IPaymentReceiptRepository,
    private val reviewMetaRepository: IReviewMetaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerAppointmentDetailUiState())
    val uiState: StateFlow<WorkerAppointmentDetailUiState> = _uiState.asStateFlow()

    fun loadPaymentReceipt(appointmentId: String) {
        viewModelScope.launch {
            println("DEBUG loading paymentReceipt for appointmentId: $appointmentId")

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            paymentReceiptRepository.getReceiptByAppointment(appointmentId)
                .onEach { receipt ->
                    println("DEBUG paymentReceipt loaded in ViewModel: $receipt")

                    _uiState.value = _uiState.value.copy(
                        paymentReceipt = receipt,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .catch { e ->
                    println("ERROR loading paymentReceipt in ViewModel: ${e.message}")

                    _uiState.value = _uiState.value.copy(
                        paymentReceipt = null,
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar comprobante"
                    )
                }
                .collect()
        }
    }

    fun loadReviewMeta(appointmentId: String) {
        viewModelScope.launch {
            println("DEBUG loading reviewMeta for appointmentId: $appointmentId")

            reviewMetaRepository.getReviewMeta(appointmentId)
                .onEach { meta ->
                    println("DEBUG reviewMeta loaded in ViewModel: $meta")

                    _uiState.value = _uiState.value.copy(
                        reviewMeta = meta ?: _uiState.value.reviewMeta,
                        errorMessage = null
                    )
                }
                .catch { e ->
                    println("ERROR loading reviewMeta in ViewModel: ${e.message}")

                    _uiState.value = _uiState.value.copy(
                        reviewMeta = _uiState.value.reviewMeta,
                        errorMessage = e.message ?: "Error al cargar reviewMeta"
                    )
                }
                .collect()
        }
    }

    fun clearState() {
        _uiState.value = WorkerAppointmentDetailUiState()
    }
}