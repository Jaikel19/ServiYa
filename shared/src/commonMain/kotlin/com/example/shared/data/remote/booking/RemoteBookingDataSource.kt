package com.example.shared.data.remote

import com.example.shared.domain.entity.AppointmentRemote
import com.example.shared.domain.entity.Booking
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
                println("DEBUG appointments total docs: ${snapshot.documents.size}")

                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") {
                            println("DEBUG doc _schema ignorado")
                            return@mapNotNull null
                        }

                        val remote = doc.data<Booking>()
                        println("DEBUG remote doc ${doc.id}: $remote")

                        if (remote.workerId != workerId) {
                            println("DEBUG doc ${doc.id} descartado por workerId=${remote.workerId}")
                            return@mapNotNull null
                        }

                        val booking = Booking(
                            id = doc.id,
                            clientId = remote.clientId,
                            clientName = remote.clientName,
                            workerId = remote.workerId,
                            workerName = remote.workerName,
                            date = extractDatePart(remote.date),
                            time = extractTimePart(remote.date),
                            status = remote.status,
                            totalCost = remote.totalCost,
                            services = remote.services,
                            location = remote.location
                        )

                        println("DEBUG booking mapeado: $booking")
                        booking
                    } catch (e: Exception) {
                        println("DEBUG error mapeando doc ${doc.id}: ${e.message}")
                        e.printStackTrace()
                        null
                    }
                }
            }
    }

    private fun extractDatePart(raw: String): String {
        return when {
            raw.contains("T") -> raw.substringBefore("T")
            raw.contains(" ") -> raw.substringBefore(" ")
            else -> raw
        }
    }

    private fun extractTimePart(raw: String): String {
        return when {
            raw.contains("T") -> raw.substringAfter("T").take(5)
            raw.contains(" ") -> raw.substringAfter(" ").take(5)
            else -> ""
        }
    }
}