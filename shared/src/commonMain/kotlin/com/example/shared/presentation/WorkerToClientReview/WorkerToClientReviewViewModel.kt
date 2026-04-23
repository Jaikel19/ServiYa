package com.example.shared.presentation.workerToClientReview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.Review.IReviewRepository
import com.example.shared.data.repository.notifications.INotificationsRepository
import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import com.example.shared.domain.entity.NotificationDeepLinks
import com.example.shared.domain.entity.NotificationTypes
import com.example.shared.presentation.notifications.pushNotification
import com.example.shared.domain.entity.Review
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
class WorkerToClientReviewViewModel(
    private val appointmentRepository: IAppointmentRepository,
    private val reviewRepository: IReviewRepository,
    private val professionalProfileRepository: IProfessionalProfileRepository,
    private val notificationsRepository: INotificationsRepository,
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

          val updatedReviews = reviewRepository.getReviewsByTarget(review.targetId).first()

          val publishedReviews = updatedReviews.filter {
              it.status.equals("published", ignoreCase = true)
          }

          val averageStars =
              if (publishedReviews.isNotEmpty()) {
                  publishedReviews.map { it.rating }.average()
              } else {
                  review.rating.toDouble()
              }

          professionalProfileRepository.updateStars(
              userId = review.targetId,
              stars = averageStars,
          )

          notificationsRepository.pushNotification(
              userId = appointment.workerId,
              recipientRole = "worker",
              title = "Reseña enviada",
              message = "Tu calificación al cliente se envió correctamente.",
              type = NotificationTypes.REVIEW_SUBMITTED_SUCCESS,
              appointmentId = appointment.id,
              deepLink = NotificationDeepLinks.WORKER_REQUESTS,
              actorId = appointment.workerId,
          )

          notificationsRepository.pushNotification(
              userId = appointment.clientId,
              recipientRole = "client",
              title = "Nueva reseña recibida",
              message = "${appointment.workerName} te dejó una nueva reseña.",
              type = NotificationTypes.REVIEW_RECEIVED,
              appointmentId = appointment.id,
              deepLink = NotificationDeepLinks.CLIENT_APPOINTMENT_DETAIL,
              actorId = appointment.workerId,
          )

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
