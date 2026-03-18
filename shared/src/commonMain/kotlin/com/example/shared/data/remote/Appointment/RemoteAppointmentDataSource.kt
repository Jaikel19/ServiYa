package com.example.shared.data.remote.appointment

import com.example.shared.domain.entity.Appointment
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteAppointmentDataSource : IRemoteAppointmentDataSource {

    private val db = Firebase.firestore
    private val appointmentsCollection = "appointments"

    override suspend fun getAppointmentsByClient(clientId: String): Flow<List<Appointment>> {
        return db.collection(appointmentsCollection)
            .where { "clientId" equalTo clientId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null
                        doc.data<Appointment>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing appointment by client ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }

    override suspend fun getAppointmentsByWorker(workerId: String): Flow<List<Appointment>> {
        return db.collection(appointmentsCollection)
            .where { "workerId" equalTo workerId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null
                        doc.data<Appointment>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing appointment by worker ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }

    override suspend fun getAppointmentById(appointmentId: String): Flow<Appointment?> {
        return db.collection(appointmentsCollection)
            .document(appointmentId)
            .snapshots
            .map { doc ->
                try {
                    if (doc.exists) doc.data<Appointment>().copy(id = doc.id) else null
                } catch (e: Exception) {
                    println("ERROR parsing appointment ${doc.id}: ${e.message}")
                    null
                }
            }
    }

    override suspend fun createAppointment(appointment: Appointment): String {
        return try {
            val ref = db.collection(appointmentsCollection).add(appointment.copy(id = ""))
            ref.set(appointment.copy(id = ref.id))
            ref.id
        } catch (e: Exception) {
            println("ERROR createAppointment: ${e.message}")
            ""
        }
    }

    override suspend fun updateAppointment(appointment: Appointment) {
        try {
            db.collection(appointmentsCollection)
                .document(appointment.id)
                .set(appointment)
        } catch (e: Exception) {
            println("ERROR updateAppointment: ${e.message}")
        }
    }

    override suspend fun deleteAppointment(appointmentId: String) {
        try {
            db.collection(appointmentsCollection)
                .document(appointmentId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteAppointment: ${e.message}")
        }
    }

    override suspend fun approveAppointment(appointmentId: String) {
        db.collection(appointmentsCollection)
            .document(appointmentId)
            .update(
                "status" to "approved"
            )
    }

    override suspend fun confirmPayment(appointmentId: String) {
        db.collection(appointmentsCollection)
            .document(appointmentId)
            .update(
                "status" to "confirmed"
            )
    }

    override suspend fun startAppointment(appointmentId: String) {
        db.collection(appointmentsCollection)
            .document(appointmentId)
            .update(
                "status" to "in_progress"
            )
    }

    override suspend fun completeAppointment(appointmentId: String) {
        db.collection(appointmentsCollection)
            .document(appointmentId)
            .update(
                "status" to "completed"
            )
    }

    override suspend fun rejectAppointmentByWorker(appointmentId: String) {
        db.collection(appointmentsCollection)
            .document(appointmentId)
            .update(
                "status" to "rejected",
                "cancellationReason" to "Cancelada por trabajador",
                "cancellationBy" to "worker"
            )
    }

    override suspend fun cancelAppointmentByWorker(appointmentId: String) {
        db.collection(appointmentsCollection)
            .document(appointmentId)
            .update(
                "status" to "cancelled",
                "cancellationReason" to "Cancelada por trabajador",
                "cancellationBy" to "worker"
            )
    }

    override suspend fun cancelAppointmentByClient(appointmentId: String) {
        db.collection(appointmentsCollection)
            .document(appointmentId)
            .update(
                "status" to "cancelled",
                "cancellationReason" to "Cancelada por cliente",
                "cancellationBy" to "client"
            )
    }
}