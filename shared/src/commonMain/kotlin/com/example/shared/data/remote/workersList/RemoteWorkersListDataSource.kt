package com.example.shared.data.remote.workersList

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Category
import com.example.shared.domain.entity.WorkerProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteWorkersListDataSource : IRemoteWorkersListDataSource {

    private val db = Firebase.firestore

    override suspend fun getWorkers(): Flow<List<WorkerRemoteItem>> {
        return db.collection("users")
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    try {
                        val profile = document.data<WorkerProfile>()
                        if (profile.role == "worker") {
                            WorkerRemoteItem(
                                workerId = document.id,
                                profile = profile
                            )
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
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

    override suspend fun getWorkerAddress(workerId: String): Address? {
        return try {
            db.collection("users")
                .document(workerId)
                .collection("addresses")
                .get()
                .documents
                .firstOrNull()
                ?.data<Address>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getFavoriteWorkerIds(clientId: String): Flow<Set<String>> {
        return db.collection("users")
            .document(clientId)
            .collection("favorites")
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.map { it.id }.toSet()
            }
    }

    override suspend fun addFavorite(clientId: String, workerId: String) {
        db.collection("users")
            .document(clientId)
            .collection("favorites")
            .document(workerId)
            .set(
                mapOf(
                    "workerId" to workerId
                )
            )
    }

    override suspend fun removeFavorite(clientId: String, workerId: String) {
        db.collection("users")
            .document(clientId)
            .collection("favorites")
            .document(workerId)
            .delete()
    }
}