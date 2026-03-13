package com.example.shared.data.remote.Service

import com.example.shared.domain.entity.Service
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteServicesDataSource : IRemoteServicesDataSource {

    private val db = Firebase.firestore

    // GET ALL (realtime)
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
                        Service(id = doc.id, name = "Error parsing")
                    }
                }
            }
    }

    // GET ONE
    override suspend fun getServiceById(workerId: String, serviceId: String): Service? {
        return try {
            val doc = db.collection("users")
                .document(workerId)
                .collection("services")
                .document(serviceId)
                .get()
            if (doc.exists) doc.data<Service>().copy(id = doc.id) else null
        } catch (e: Exception) {
            println("ERROR getServiceById: ${e.message}")
            null
        }
    }

    // CREATE
    override suspend fun createService(workerId: String, service: Service): String {
        return try {
            val ref = db.collection("users")
                .document(workerId)
                .collection("services")
                .add(service)
            ref.id
        } catch (e: Exception) {
            println("ERROR createService: ${e.message}")
            ""
        }
    }

    // UPDATE
    override suspend fun updateService(workerId: String, service: Service) {
        try {
            db.collection("users")
                .document(workerId)
                .collection("services")
                .document(service.id)
                .set(service)
        } catch (e: Exception) {
            println("ERROR updateService: ${e.message}")
        }
    }

    override suspend fun deleteService(workerId: String, serviceId: String) {
        try {
            db.collection("users")
                .document(workerId)
                .collection("services")
                .document(serviceId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteService: ${e.message}")
        }
    }
}