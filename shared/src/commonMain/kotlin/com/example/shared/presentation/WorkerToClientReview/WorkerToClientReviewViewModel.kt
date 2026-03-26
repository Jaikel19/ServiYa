package com.example.shared.presentation.workerToClientReview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.Review.IReviewRepository
import com.example.shared.domain.entity.Review
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
class WorkerToClientReviewViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val reviewRepository: IReviewRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(WorkerToClientReviewUiState())
  val uiState: StateFlow<WorkerToClientReviewUiState> = _uiState.asStateFlow()

  fun loadAppointment(appointmentId: String) {
    viewModelScope.launch {
      try {
        appointmentRepository.getAppointmentById(appointmentId).collectLatest { appointment ->
          if (appointment != null) {
            _uiState.value = _uiState.value.copy(appointment = appointment, errorMessage = null)
          } else {
            _uiState.value =
                _uiState.value.copy(appointment = null, errorMessage = "No se encontró la cita")
          }
        }
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Error cargando la cita")
      }
    }
  }

  fun onRatingChanged(value: Int) {
    _uiState.value = _uiState.value.copy(rating = value, errorMessage = null)
  }

  fun onCommentChanged(value: String) {
    _uiState.value = _uiState.value.copy(comment = value)
  }

  fun onImagesChanged(value: List<String>) {
    _uiState.value = _uiState.value.copy(imageUrls = value)
  }

  fun submitReview() {
    val appointment = _uiState.value.appointment ?: return
    val rating = _uiState.value.rating

    if (rating <= 0) {
      _uiState.value = _uiState.value.copy(errorMessage = "Debes seleccionar una calificación")
      return
    }

    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)

      try {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

        val review =
            Review(
                appointmentId = appointment.id,
                authorId = appointment.workerId,
                authorRole = "worker",
                targetId = appointment.clientId,
                targetRole = "client",
                clientName = appointment.clientName,
                workerName = appointment.workerName,
                rating = rating,
                comment = _uiState.value.comment,
                images = _uiState.value.imageUrls,
                direction = "worker_to_client",
                status = "published",
                serviceSummary = appointment.services.firstOrNull()?.name ?: "",
                createdAt = now,
            )

        val reviewId = reviewRepository.createWorkerToClientReview(review)

        if (reviewId.isBlank()) {
          _uiState.value =
              _uiState.value.copy(
                  isSubmitting = false,
                  errorMessage = "No se pudo guardar la reseña",
              )
          return@launch
        }

        _uiState.value =
            _uiState.value.copy(isSubmitting = false, submitSuccess = true, errorMessage = null)
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isSubmitting = false,
                errorMessage = e.message ?: "Error enviando la reseña",
            )
      }
    }
  }
}
