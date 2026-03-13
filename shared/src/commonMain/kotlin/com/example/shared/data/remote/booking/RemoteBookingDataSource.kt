package com.example.shared.data.remote

import com.example.shared.domain.entity.Booking
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.WorkerProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteBookingDataSource : IRemoteBookingDataSource {

    private val db = Firebase.firestore

    override fun getBookingsByWorker(workerId: String): Flow<List<Booking>> {
        return db.collection("appointments")
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null

                        val booking = doc.data<Booking>().copy(id = doc.id)

                        if (booking.workerId != workerId) return@mapNotNull null

                        booking
                    } catch (e: Exception) {
                        println("ERROR mapping booking ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }

    override fun getBookingsByClient(clientId: String): Flow<List<Booking>> {
        return db.collection("appointments")
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null

                        val booking = doc.data<Booking>().copy(id = doc.id)

                        if (booking.clientId != clientId) return@mapNotNull null

                        booking
                    } catch (e: Exception) {
                        println("ERROR mapping booking ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }

    override suspend fun getBookingById(bookingId: String): Booking? {
        return try {
            val doc = db.collection("appointments")
                .document(bookingId)
                .get()

            if (!doc.exists) {
                null
            } else {
                doc.data<Booking>().copy(id = doc.id)
            }
        } catch (e: Exception) {
            println("ERROR getting booking by id $bookingId: ${e.message}")
            null
        }
    }

    override suspend fun getCancellationPolicyByWorkerId(workerId: String): CancellationPolicy? {
        return try {
            val doc = db.collection("users")
                .document(workerId)
                .collection("cancellationPolicy")
                .document("cancellationPolicy")
                .get()

            if (!doc.exists) {
                null
            } else {
                doc.data<CancellationPolicy>()
            }
        } catch (e: Exception) {
            println("ERROR getting cancellation policy for worker $workerId: ${e.message}")
            null
        }
    }

    override suspend fun getWorkerProfile(workerId: String): WorkerProfile? {
        return try {
            val doc = db.collection("users")
                .document(workerId)
                .get()

            if (!doc.exists) {
                null
            } else {
                doc.data<WorkerProfile>().copy(uid = doc.id)
            }
        } catch (e: Exception) {
            println("ERROR getting worker profile for $workerId: ${e.message}")
            null
        }
    }

    override suspend fun confirmPayment(bookingId: String) {
        db.collection("appointments")
            .document(bookingId)
            .update(
                "status" to "confirmed"
            )
    }

    override suspend fun startAppointment(bookingId: String) {
        db.collection("appointments")
            .document(bookingId)
            .update(
                "status" to "in_progress"
            )
    }

    override suspend fun completeAppointment(bookingId: String) {
        db.collection("appointments")
            .document(bookingId)
            .update(
                "status" to "completed"
            )
    }

    override suspend fun cancelAppointmentByWorker(bookingId: String) {
        db.collection("appointments")
            .document(bookingId)
            .update(
                "status" to "cancelled",
                "cancellationReason" to "Cancelada por trabajador",
                "cancellationBy" to "worker"
            )
    }

    override suspend fun cancelAppointmentByClient(bookingId: String) {
        db.collection("appointments")
            .document(bookingId)
            .update(
                "status" to "cancelled",
                "cancellationReason" to "Cancelada por cliente",
                "cancellationBy" to "client"
            )
    }
}