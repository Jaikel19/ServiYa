package com.example.shared.data.remote

import com.example.shared.domain.entity.Service
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteServicesDataSource : IRemoteServicesDataSource {

    private val db = Firebase.firestore

    override fun getServicesByWorker(workerId: String): Flow<List<Service>> = flow {
        val snapshot = db
            .collection("users")
            .document(workerId)
            .collection("services")
            .get()

        val services = snapshot.documents.map { doc ->
            Service(
                id = doc.id,
                name = doc.get("name"),
                cost = doc.get("cost"),
                duration = doc.get("duration") ?: "",
                description = doc.get("description") ?: ""
            )
        }
        emit(services)
    }
}