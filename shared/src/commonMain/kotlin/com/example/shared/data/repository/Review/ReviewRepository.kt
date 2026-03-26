package com.example.shared.data.repository.Review

import com.example.shared.data.remote.Review.IRemoteReviewDataSource
import com.example.shared.data.repository.catchEmpty
import com.example.shared.data.repository.safeNullableCall
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.Review
import kotlinx.coroutines.flow.Flow

class ReviewRepository(
    private val remote: IRemoteReviewDataSource
) : IReviewRepository {

    override suspend fun getReviewsByTarget(targetId: String): Flow<List<Review>> =
        remote.getReviewsByTarget(targetId).catchEmpty("fetching reviews by target")

    override suspend fun getReviewsByAppointment(appointmentId: String): Flow<List<Review>> =
        remote.getReviewsByAppointment(appointmentId).catchEmpty("fetching reviews by appointment")

    override suspend fun getReviewById(reviewId: String): Review? =
        safeNullableCall("getReviewById") { remote.getReviewById(reviewId) }

    override suspend fun createReview(review: Review): String =
        safeStringCall("createReview") { remote.createReview(review) }

    override suspend fun updateReviewStatus(reviewId: String, status: String) =
        safeUnitCall("updateReviewStatus") { remote.updateReviewStatus(reviewId, status) }

    override suspend fun deleteReview(reviewId: String) =
        safeUnitCall("deleteReview") { remote.deleteReview(reviewId) }

    override suspend fun createWorkerToClientReview(review: Review): String =
        safeStringCall("createWorkerToClientReview") {
            val reviewId = remote.createReview(review)
            if (reviewId.isNotBlank()) {
                remote.markWorkerToClientReviewCreated(
                    appointmentId = review.appointmentId,
                    reviewId = reviewId
                )
            }
            reviewId
        }

    override suspend fun createClientToWorkerReview(review: Review): String =
        safeStringCall("createClientToWorkerReview") {
            val reviewId = remote.createReview(review)
            if (reviewId.isNotBlank()) {
                remote.markClientToWorkerReviewCreated(
                    appointmentId = review.appointmentId,
                    reviewId = reviewId
                )
            }
            reviewId
        }
}