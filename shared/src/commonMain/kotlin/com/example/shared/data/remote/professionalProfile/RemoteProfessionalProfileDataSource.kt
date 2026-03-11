package com.example.shared.data.remote.professionalProfile

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.Category
import com.example.shared.domain.entity.WorkerProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteProfessionalProfileDataSource : IRemoteProfessionalProfileDataSource {

    private val db = Firebase.firestore

    override suspend fun getWorkerProfile(workerId: String): Flow<WorkerProfile?> {
        return db.collection("users")
            .document(workerId)
            .snapshots
            .map { snapshot ->
                try {
                    snapshot.data<WorkerProfile>()
                } catch (e: Exception) {
                    null
                }
            }
    }

    override suspend fun getCategoryNames(categoryIds: List<String>): List<String> {
        return categoryIds.mapNotNull { categoryId ->
            try {
                db.collection("categories")
                    .document(categoryId)
                    .get()
                    .data<Category>()
                    .name
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun getWorkerProvinceFromAddresses(workerId: String): String? {
        return try {
            db.collection("users")
                .document(workerId)
                .collection("addresses")
                .get()
                .documents
                .firstOrNull()
                ?.data<Address>()
                ?.province
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getWorkerCancellationPolicy(workerId: String): CancellationPolicy? {
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