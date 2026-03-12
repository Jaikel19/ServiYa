package com.example.shared.data.remote.ReviewMeta

import com.example.shared.domain.entity.ReviewMeta
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteReviewMetaDataSource : IRemoteReviewMetaDataSource {

    private val db = Firebase.firestore

    // GET (realtime)
    override suspend fun getReviewMeta(appointmentId: String): Flow<ReviewMeta?> {
        return db.collection("appointments")
            .document(appointmentId)
            .collection("reviewMeta")
            .snapshots
            .map { snapshot ->
                snapshot.documents.firstOrNull()?.let { doc ->
                    try {
                        doc.data<ReviewMeta>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing reviewMeta: ${e.message}")
                        null
                    }
                }
            }
    }

    // CREATE
    override suspend fun createReviewMeta(appointmentId: String, reviewMeta: ReviewMeta): String {
        return try {
            val ref = db.collection("appointments")
                .document(appointmentId)
                .collection("reviewMeta")
                .add(reviewMeta)
            ref.id
        } catch (e: Exception) {
            println("ERROR createReviewMeta: ${e.message}")
            ""
        }
    }

    // UPDATE CLIENT TO WORKER
    override suspend fun updateClientToWorkerReview(appointmentId: String, reviewId: String) {
        try {
            val snapshot = db.collection("appointments")
                .document(appointmentId)
                .collection("reviewMeta")
                .get()
            val docId = snapshot.documents.firstOrNull()?.id ?: return
            db.collection("appointments")
                .document(appointmentId)
                .collection("reviewMeta")
                .document(docId)
                .update(
                    "clientToWorkerCreated" to true,
                    "clientToWorkerReviewId" to reviewId
                )
        } catch (e: Exception) {
            println("ERROR updateClientToWorkerReview: ${e.message}")
        }
    }

    // UPDATE WORKER TO CLIENT
    override suspend fun updateWorkerToClientReview(appointmentId: String, reviewId: String) {
        try {
            val snapshot = db.collection("appointments")
                .document(appointmentId)
                .collection("reviewMeta")
                .get()
            val docId = snapshot.documents.firstOrNull()?.id ?: return
            db.collection("appointments")
                .document(appointmentId)
                .collection("reviewMeta")
                .document(docId)
                .update(
                    "workerToClientCreated" to true,
                    "workerToClientReviewId" to reviewId
                )
        } catch (e: Exception) {
            println("ERROR updateWorkerToClientReview: ${e.message}")
        }
    }
}