package com.example.shared.data.repository.Review

import com.example.shared.data.remote.Review.IRemoteReviewDataSource
import com.example.shared.domain.entity.Review
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ReviewRepository(
    private val remote: IRemoteReviewDataSource
) : IReviewRepository {

    override suspend fun getReviewsByTarget(targetId: String): Flow<List<Review>> =
        remote.getReviewsByTarget(targetId)
            .catch { e ->
                println("ERROR fetching reviews by target: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getReviewsByAppointment(appointmentId: String): Flow<List<Review>> =
        remote.getReviewsByAppointment(appointmentId)
            .catch { e ->
                println("ERROR fetching reviews by appointment: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getReviewById(reviewId: String): Review? =
        try {
            remote.getReviewById(reviewId)
        } catch (e: Exception) {
            println("ERROR getReviewById: ${e.message}")
            null
        }

    override suspend fun createReview(review: Review): String =
        try {
            remote.createReview(review)
        } catch (e: Exception) {
            println("ERROR createReview: ${e.message}")
            ""
        }

    override suspend fun updateReviewStatus(reviewId: String, status: String) =
        try {
            remote.updateReviewStatus(reviewId, status)
        } catch (e: Exception) {
            println("ERROR updateReviewStatus: ${e.message}")
        }

    override suspend fun deleteReview(reviewId: String) =
        try {
            remote.deleteReview(reviewId)
        } catch (e: Exception) {
            println("ERROR deleteReview: ${e.message}")
        }

    override suspend fun createWorkerToClientReview(review: Review): String =
        try {
            val reviewId = remote.createReview(review)
            if (reviewId.isNotBlank()) {
                remote.markWorkerToClientReviewCreated(
                    appointmentId = review.appointmentId,
                    reviewId = reviewId
                )
            }
            reviewId
        } catch (e: Exception) {
            println("ERROR createWorkerToClientReview: ${e.message}")
            ""
        }

    override suspend fun createClientToWorkerReview(review: Review): String =
        try {
            val reviewId = remote.createReview(review)
            if (reviewId.isNotBlank()) {
                remote.markClientToWorkerReviewCreated(
                    appointmentId = review.appointmentId,
                    reviewId = reviewId
                )
            }
            reviewId
        } catch (e: Exception) {
            println("ERROR createClientToWorkerReview: ${e.message}")
            ""
        }
}