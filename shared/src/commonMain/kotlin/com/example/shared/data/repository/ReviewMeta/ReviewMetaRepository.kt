package com.example.shared.data.repository.ReviewMeta

import com.example.shared.data.remote.ReviewMeta.IRemoteReviewMetaDataSource
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
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
        safeStringCall("createReviewMeta") { remote.createReviewMeta(appointmentId, reviewMeta) }

    override suspend fun updateClientToWorkerReview(
        appointmentId: String,
        reviewId: String
    ) = safeUnitCall("updateClientToWorkerReview") {
        remote.updateClientToWorkerReview(appointmentId, reviewId)
    }

    override suspend fun updateWorkerToClientReview(
        appointmentId: String,
        reviewId: String
    ) = safeUnitCall("updateWorkerToClientReview") {
        remote.updateWorkerToClientReview(appointmentId, reviewId)
    }
}