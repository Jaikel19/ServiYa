package com.example.shared.data.remote.ReviewMeta

import com.example.shared.domain.entity.ReviewMeta
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteReviewMetaDataSource : IRemoteReviewMetaDataSource {

    private val db = Firebase.firestore

    override suspend fun getReviewMeta(appointmentId: String): Flow<ReviewMeta?> {
        return db.collection("appointments")
            .document(appointmentId)
            .collection("reviewMeta")
            .document("meta")
            .snapshots
            .map { doc ->
                try {
                    if (doc.exists) {
                        doc.data<ReviewMeta>().copy(id = doc.id)
                    } else {
                        ReviewMeta(id = "meta")
                    }
                } catch (e: Exception) {
                    println("ERROR parsing reviewMeta: ${e.message}")
                    ReviewMeta(id = "meta")
                }
            }
    }

    override suspend fun createReviewMeta(
        appointmentId: String,
        reviewMeta: ReviewMeta
    ): String {
        return try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("reviewMeta")
                .document("meta")
                .set(reviewMeta.copy(id = "meta"))

            "meta"
        } catch (e: Exception) {
            println("ERROR createReviewMeta: ${e.message}")
            ""
        }
    }

    override suspend fun updateClientToWorkerReview(
        appointmentId: String,
        reviewId: String
    ) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("reviewMeta")
                .document("meta")
                .set(
                    mapOf(
                        "clientToWorkerCreated" to true,
                        "clientToWorkerReviewId" to reviewId
                    ),
                    merge = true
                )
        } catch (e: Exception) {
            println("ERROR updateClientToWorkerReview: ${e.message}")
        }
    }

    override suspend fun updateWorkerToClientReview(
        appointmentId: String,
        reviewId: String
    ) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("reviewMeta")
                .document("meta")
                .set(
                    mapOf(
                        "workerToClientCreated" to true,
                        "workerToClientReviewId" to reviewId
                    ),
                    merge = true
                )
        } catch (e: Exception) {
            println("ERROR updateWorkerToClientReview: ${e.message}")
        }
    }
}