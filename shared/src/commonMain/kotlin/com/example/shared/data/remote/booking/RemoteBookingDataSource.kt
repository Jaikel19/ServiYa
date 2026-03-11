package com.example.shared.data.remote

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
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null

                        val booking = doc.data<Booking>()

                        if (booking.workerId != workerId) return@mapNotNull null

                        booking
                    } catch (e: Exception) {
                        println("ERROR mapping booking ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }
}