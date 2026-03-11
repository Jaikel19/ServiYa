package com.example.shared.data.remote.cancellationPolicy

import com.example.shared.domain.entity.CancellationPolicy
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class RemoteCancellationPolicyDataSource : IRemoteCancellationPolicyDataSource {

    private val db = Firebase.firestore

    override suspend fun getCancellationPolicy(workerId: String): CancellationPolicy? {
        return try {
            db.collection("users")
                .document(workerId)
                .collection("cancellationPolicy")
                .document("cancellationPolicy")
                .get()
                .data<CancellationPolicy>()
        } catch (e: Exception) {
            null
        }
    }
}