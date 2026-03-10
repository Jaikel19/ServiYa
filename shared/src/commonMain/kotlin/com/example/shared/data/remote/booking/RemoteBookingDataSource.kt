package com.example.shared.data.remote

import com.example.shared.domain.entity.Booking
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteBookingDataSource : IRemoteBookingDataSource {

    private val db = Firebase.firestore

    override suspend fun getBookingsByWorker(workerId: String): Flow<List<Booking>> {
        return db.collection("users")
            .document(workerId)
            .collection("bookings")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<Booking>().copy(id = doc.id)
                    } catch (e: Exception) {
                        Booking(id = doc.id)
                    }
                }
            }
    }
}