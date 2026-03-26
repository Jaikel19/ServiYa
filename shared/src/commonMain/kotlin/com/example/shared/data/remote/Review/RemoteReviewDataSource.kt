package com.example.shared.data.remote.Review

import com.example.shared.domain.entity.Review
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteReviewDataSource : IRemoteReviewDataSource {

  private val db = Firebase.firestore

  // GET ALL BY TARGET (realtime)
  override suspend fun getReviewsByTarget(targetId: String): Flow<List<Review>> {
    return db.collection("Review")
        .where { "targetId" equalTo targetId }
        .snapshots
        .map { snapshot ->
          snapshot.documents.map { doc ->
            try {
              doc.data<Review>().copy(id = doc.id)
            } catch (e: Exception) {
              Review(id = doc.id)
            }
          }
        }
  }

  // GET ALL BY APPOINTMENT (realtime)
  override suspend fun getReviewsByAppointment(appointmentId: String): Flow<List<Review>> {
    return db.collection("Review")
        .where { "appointmentId" equalTo appointmentId }
        .snapshots
        .map { snapshot ->
          snapshot.documents.map { doc ->
            try {
              doc.data<Review>().copy(id = doc.id)
            } catch (e: Exception) {
              Review(id = doc.id)
            }
          }
        }
  }

  // GET ONE
  override suspend fun getReviewById(reviewId: String): Review? {
    return try {
      val doc = db.collection("Review").document(reviewId).get()
      if (doc.exists) doc.data<Review>().copy(id = doc.id) else null
    } catch (e: Exception) {
      println("ERROR getReviewById: ${e.message}")
      null
    }
  }

  // CREATE
  override suspend fun createReview(review: Review): String {
    return try {
      val ref = db.collection("Review").add(review)
      ref.id
    } catch (e: Exception) {
      println("ERROR createReview: ${e.message}")
      ""
    }
  }

  // UPDATE STATUS
  override suspend fun updateReviewStatus(reviewId: String, status: String) {
    try {
      db.collection("Review").document(reviewId).update("status" to status)
    } catch (e: Exception) {
      println("ERROR updateReviewStatus: ${e.message}")
    }
  }

  // DELETE
  override suspend fun deleteReview(reviewId: String) {
    try {
      db.collection("Review").document(reviewId).delete()
    } catch (e: Exception) {
      println("ERROR deleteReview: ${e.message}")
    }
  }

  override suspend fun markWorkerToClientReviewCreated(appointmentId: String, reviewId: String) {
    try {
      db.collection("appointments")
          .document(appointmentId)
          .collection("reviewMeta")
          .document("meta")
          .set(
              mapOf("workerToClientCreated" to true, "workerToClientReviewId" to reviewId),
              merge = true,
          )
    } catch (e: Exception) {
      println("ERROR markWorkerToClientReviewCreated: ${e.message}")
    }
  }

  override suspend fun markClientToWorkerReviewCreated(appointmentId: String, reviewId: String) {
    try {
      db.collection("appointments")
          .document(appointmentId)
          .collection("reviewMeta")
          .document("meta")
          .set(
              mapOf("clientToWorkerCreated" to true, "clientToWorkerReviewId" to reviewId),
              merge = true,
          )
    } catch (e: Exception) {
      println("ERROR markClientToWorkerReviewCreated: ${e.message}")
    }
  }
}
