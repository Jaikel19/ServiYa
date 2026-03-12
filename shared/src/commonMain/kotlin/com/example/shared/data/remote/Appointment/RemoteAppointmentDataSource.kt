package com.example.shared.data.remote.appointment

import com.example.shared.domain.entity.Appointment
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteAppointmentDataSource : IRemoteAppointmentDataSource {

    private val db = Firebase.firestore

    // GET ALL BY CLIENT (realtime)
    override suspend fun getAppointmentsByClient(clientId: String): Flow<List<Appointment>> {
        return db.collection("appointments")
            .where { "clientId" equalTo clientId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<Appointment>().copy(id = doc.id)
                    } catch (e: Exception) {
                        Appointment(id = doc.id)
                    }
                }
            }
    }

    // GET ALL BY WORKER (realtime)
    override suspend fun getAppointmentsByWorker(workerId: String): Flow<List<Appointment>> {
        return db.collection("appointments")
            .where { "workerId" equalTo workerId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<Appointment>().copy(id = doc.id)
                    } catch (e: Exception) {
                        Appointment(id = doc.id)
                    }
                }
            }
    }

    // GET ONE (realtime)
    override suspend fun getAppointmentById(appointmentId: String): Flow<Appointment?> {
        return db.collection("appointments")
            .document(appointmentId)
            .snapshots
            .map { doc ->
                try {
                    if (doc.exists) doc.data<Appointment>().copy(id = doc.id) else null
                } catch (e: Exception) {
                    println("ERROR parsing appointment: ${e.message}")
                    null
                }
            }
    }

    // CREATE
    override suspend fun createAppointment(appointment: Appointment): String {
        return try {
            val ref = db.collection("appointments").add(appointment)
            ref.id
        } catch (e: Exception) {
            println("ERROR createAppointment: ${e.message}")
            ""
        }
    }

    // UPDATE
    override suspend fun updateAppointment(appointment: Appointment) {
        try {
            db.collection("appointments")
                .document(appointment.id)
                .set(appointment)
        } catch (e: Exception) {
            println("ERROR updateAppointment: ${e.message}")
        }
    }

    // DELETE
    override suspend fun deleteAppointment(appointmentId: String) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteAppointment: ${e.message}")
        }
    }
}