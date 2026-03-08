package com.example.shared.data.remote

import com.example.shared.domain.entity.Service
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteServicesDataSource : IRemoteServicesDataSource {

    private val db = Firebase.firestore

    override suspend fun getServicesByWorker(workerId: String): Flow<List<Service>> {
        return db.collection("users")
            .document(workerId)
            .collection("services")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<Service>().copy(id = doc.id)
                    } catch (e: Exception) {
                        // Keep stream alive if a malformed document appears.
                        Service(id = doc.id, name = "Error parsing")
                    }
                }
            }
    }
}
