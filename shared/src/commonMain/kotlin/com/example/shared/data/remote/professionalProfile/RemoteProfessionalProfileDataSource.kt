package com.example.shared.data.remote.professionalProfile

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.Category
import com.example.shared.domain.entity.PortfolioItem
import com.example.shared.domain.entity.WorkerProfile
import com.example.shared.domain.entity.WorkerReviewItem
import com.example.shared.domain.entity.WorkerSchedule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    override suspend fun getWorkerPortfolios(workerId: String): List<PortfolioItem> {
        return try {
            db.collection("users")
                .document(workerId)
                .collection("portfolios")
                .get()
                .documents
                .mapNotNull { document ->
                    try {
                        document.data<PortfolioItem>().copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getWorkerReviews(workerId: String): List<WorkerReviewItem> {
        return try {
            db.collection("Review")
                .get()
                .documents
                .mapNotNull { document ->
                    try {
                        document.data<WorkerReviewItem>().copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                .filter { review ->
                    review.targetId == workerId &&
                            review.targetRole.equals("worker", ignoreCase = true) &&
                            review.direction.equals("client_to_worker", ignoreCase = true) &&
                            review.status.equals("published", ignoreCase = true)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getWorkerAppointments(workerId: String): Flow<List<Appointment>> {
        return try {
            val appointments = db.collection("appointments")
                .get()
                .documents
                .mapNotNull { document ->
                    try {
                        document.data<Appointment>().copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                .filter { appointment ->
                    appointment.workerId == workerId
                }

            flowOf(appointments)
        } catch (e: Exception) {
            flowOf(emptyList())
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