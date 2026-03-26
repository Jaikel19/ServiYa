package com.example.shared.data.repository.ReviewMeta

import com.example.shared.domain.entity.ReviewMeta
import kotlinx.coroutines.flow.Flow

interface IReviewMetaRepository {
  suspend fun getReviewMeta(appointmentId: String): Flow<ReviewMeta?>

  suspend fun createReviewMeta(appointmentId: String, reviewMeta: ReviewMeta): String

  suspend fun updateClientToWorkerReview(appointmentId: String, reviewId: String)

  suspend fun updateWorkerToClientReview(appointmentId: String, reviewId: String)
}
