package com.example.shared.presentation.ClientPaymentUpload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.cloudinary.CloudinaryService
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.PaymentReceipt.IPaymentReceiptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClientPaymentUploadViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val paymentReceiptRepository: IPaymentReceiptRepository,
    private val cloudinaryService: CloudinaryService,
) : ViewModel() {

  private val _state = MutableStateFlow(ClientPaymentUploadUiState())
  val uiState: StateFlow<ClientPaymentUploadUiState> = _state.asStateFlow()

  fun loadData(appointmentId: String) {
    viewModelScope.launch {
      _state.value = _state.value.copy(isLoading = true, errorMessage = null)
      try {
        val appointment = appointmentRepository.getAppointmentById(appointmentId).first()

        val receipt =
            paymentReceiptRepository
                .getReceiptByAppointment(appointmentId)
                .catch { emit(null) }
                .first()

        _state.value =
            _state.value.copy(
                isLoading = false,
                appointment = appointment,
                paymentReceipt = receipt,
            )
      } catch (e: Exception) {
        _state.value =
            _state.value.copy(isLoading = false, errorMessage = e.message ?: "Error cargando datos")
      }
    }
  }

  fun onImageUploaded(imageUrl: String) {
    val appointment = _state.value.appointment ?: return
    val receipt = _state.value.paymentReceipt ?: return

    viewModelScope.launch {
      _state.value = _state.value.copy(isUploading = true)
      try {
        paymentReceiptRepository.updateReceiptImageUrl(
            appointmentId = appointment.id,
            receiptId = receipt.id,
            imageUrl = imageUrl,
        )
        appointmentRepository.markPaymentPending(appointment.id)
        _state.value = _state.value.copy(isUploading = false, uploadSuccess = true)
      } catch (e: Exception) {
        _state.value =
            _state.value.copy(
                isUploading = false,
                errorMessage = e.message ?: "Error guardando comprobante",
            )
      }
    }
  }

  fun onUploadError(message: String) {
    _state.value = _state.value.copy(isUploading = false, errorMessage = message)
  }

  fun onImageSelected(imageBytes: ByteArray) {
    _state.value = _state.value.copy(selectedImageBytes = imageBytes)
  }

  fun uploadReceipt() {
    val appointment = _state.value.appointment ?: return
    val receipt = _state.value.paymentReceipt ?: return
    val imageBytes = _state.value.selectedImageBytes ?: return

    viewModelScope.launch {
      _state.value = _state.value.copy(isUploading = true, errorMessage = null)
      try {
        // Subir imagen a Cloudinary
        val imageUrl =
            cloudinaryService.uploadImage(imageBytes = imageBytes, fileName = "receipttest123")

        if (imageUrl.isBlank()) {
          _state.value =
              _state.value.copy(isUploading = false, errorMessage = "Error al subir la imagen")
          return@launch
        }

        // Actualizar imageUrl en el receipt (status se mantiene "pending")
        paymentReceiptRepository.updateReceiptImageUrl(
            appointmentId = appointment.id,
            receiptId = receipt.id,
            imageUrl = imageUrl,
        )

        // Cambiar Appointment a payment_pending
        appointmentRepository.markPaymentPending(appointment.id)

        _state.value = _state.value.copy(isUploading = false, uploadSuccess = true)
      } catch (e: Exception) {
        _state.value =
            _state.value.copy(
                isUploading = false,
                errorMessage = e.message ?: "Error subiendo comprobante",
            )
      }
    }
  }
}
