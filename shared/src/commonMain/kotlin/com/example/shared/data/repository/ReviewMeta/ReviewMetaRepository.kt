package com.example.shared.data.repository.ReviewMeta

import com.example.shared.data.remote.ReviewMeta.IRemoteReviewMetaDataSource
import com.example.shared.domain.entity.ReviewMeta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ReviewMetaRepository(
    private val remote: IRemoteReviewMetaDataSource
) : IReviewMetaRepository {

    override suspend fun getReviewMeta(appointmentId: String): Flow<ReviewMeta?> =
        remote.getReviewMeta(appointmentId)
            .catch { e ->
                println("ERROR fetching reviewMeta: ${e.message}")
                emit(ReviewMeta(id = "meta"))
            }

    override suspend fun createReviewMeta(
        appointmentId: String,
        reviewMeta: ReviewMeta
    ): String =
        try {
            remote.createReviewMeta(appointmentId, reviewMeta)
        } catch (e: Exception) {
            println("ERROR createReviewMeta: ${e.message}")
            ""
        }

    override suspend fun updateClientToWorkerReview(
        appointmentId: String,
        reviewId: String
    ) =
        try {
            remote.updateClientToWorkerReview(appointmentId, reviewId)
        } catch (e: Exception) {
            println("ERROR updateClientToWorkerReview: ${e.message}")
        }

    override suspend fun updateWorkerToClientReview(
        appointmentId: String,
        reviewId: String
    ) =
        try {
            remote.updateWorkerToClientReview(appointmentId, reviewId)
        } catch (e: Exception) {
            println("ERROR updateWorkerToClientReview: ${e.message}")
        }
}