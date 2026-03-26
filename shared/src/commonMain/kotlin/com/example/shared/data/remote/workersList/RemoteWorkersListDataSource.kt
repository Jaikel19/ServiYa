package com.example.shared.data.remote.workersList

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.Category
import com.example.shared.domain.entity.WorkZone
import com.example.shared.domain.entity.WorkerProfile
import com.example.shared.domain.entity.WorkerSchedule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteWorkersListDataSource : IRemoteWorkersListDataSource {

    private val db = Firebase.firestore

    private fun toWorkerRemoteItemOrNull(workerId: String, profile: WorkerProfile): WorkerRemoteItem? {
        return if (profile.role == "worker") {
            WorkerRemoteItem(
                workerId = workerId,
                profile = profile
            )
        } else {
            null
        }
    }

    override suspend fun getWorkers(): Flow<List<WorkerRemoteItem>> {
        return db.collection("users")
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    try {
                        val profile = document.data<WorkerProfile>()
                        toWorkerRemoteItemOrNull(document.id, profile)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override suspend fun getWorkersByIds(workerIds: Set<String>): List<WorkerRemoteItem> {
        if (workerIds.isEmpty()) return emptyList()

        return workerIds.mapNotNull { workerId ->
            try {
                val snapshot = db.collection("users")
                    .document(workerId)
                    .get()

                val profile = snapshot.data<WorkerProfile>()
                toWorkerRemoteItemOrNull(snapshot.id, profile)
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

    override suspend fun getWorkerSchedule(workerId: String): List<WorkerSchedule> {
        return try {
            db.collection("users")
                .document(workerId)
                .collection("schedule")
                .get()
                .documents
                .mapNotNull { document ->
                    try {
                        document.data<WorkerSchedule>()
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getWorkerAppointments(workerId: String): List<Appointment> {
        return try {
            db.collection("appointments")
                .get()
                .documents
                .mapNotNull { document ->
                    try {
                        document.data<Appointment>()
                    } catch (e: Exception) {
                        null
                    }
                }
                .filter { appointment ->
                    appointment.workerId == workerId
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getWorkerWorkZones(workerId: String): List<WorkZone> {
        return try {
            db.collection("users")
                .document(workerId)
                .collection("workZones")
                .get()
                .documents
                .mapNotNull { document ->
                    try {
                        document.data<WorkZone>()
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
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