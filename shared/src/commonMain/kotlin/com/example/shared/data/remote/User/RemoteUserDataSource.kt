package com.example.shared.data.remote.User

import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.shared.domain.entity.Address
class RemoteUserDataSource : IRemoteUserDataSource {

    private val db = Firebase.firestore

    override suspend fun getAllWorkers(): Flow<List<User>> {
        return db.collection("users")
            .where { "role" equalTo "worker" }
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null
                        doc.data<User>().copy(uid = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing user ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }

    override suspend fun getUserById(userId: String): Flow<User?> {
        return db.collection("users")
            .document(userId)
            .snapshots
            .map { doc ->
                try {
                    if (doc.exists) doc.data<User>().copy(uid = doc.id) else null
                } catch (e: Exception) {
                    println("ERROR parsing user ${doc.id}: ${e.message}")
                    null
                }
            }
    }

    override suspend fun getWorkZonesByUser(userId: String): Flow<List<WorkZone>> {
        return db.collection("users")
            .document(userId)
            .collection("workZones")
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null
                        doc.data<WorkZone>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing workZone ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }
    override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> {
        return db.collection("users")
            .document(userId)
            .collection("addresses")
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null
                        doc.data<Address>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing address ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }
}