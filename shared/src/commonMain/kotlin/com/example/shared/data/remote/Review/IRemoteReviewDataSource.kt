package com.example.shared.data.remote.Review

import com.example.shared.domain.entity.Review
import kotlinx.coroutines.flow.Flow

interface IRemoteReviewDataSource {
    suspend fun getReviewsByTarget(targetId: String): Flow<List<Review>>
    suspend fun getReviewsByAppointment(appointmentId: String): Flow<List<Review>>
    suspend fun getReviewById(reviewId: String): Review?
    suspend fun createReview(review: Review): String
    suspend fun updateReviewStatus(reviewId: String, status: String)
    suspend fun deleteReview(reviewId: String)

    suspend fun markWorkerToClientReviewCreated(
        appointmentId: String,
        reviewId: String
    )

    suspend fun markClientToWorkerReviewCreated(
        appointmentId: String,
        reviewId: String
    )
}