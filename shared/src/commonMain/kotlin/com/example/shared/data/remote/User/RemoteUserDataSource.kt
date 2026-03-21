package com.example.shared.data.remote.User

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Category
import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteUserDataSource : IRemoteUserDataSource {

    private val db = Firebase.firestore

    override suspend fun getAllWorkers(): Flow<List<User>> {
        return db.collection("users")
            .snapshots
            .map { snapshot ->
                println("DEBUG total docs encontrados: ${snapshot.documents.size}")
                snapshot.documents.mapNotNull { doc ->
                    try {
                        if (doc.id == "_schema") return@mapNotNull null
                        val user = parseUser(doc)
                        if (user.role != "worker") return@mapNotNull null
                        println("DEBUG parseado OK: ${user.name} - ${user.uid}")
                        user
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
                    if (doc.exists) parseUser(doc) else null
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

    private fun parseUser(doc: DocumentSnapshot): User {
        val categories = try {
            val rawCategories = doc.get<List<String>>("categories") ?: emptyList()
            rawCategories.map { Category(id = it, name = it) }
        } catch (e: Exception) {
            println("ERROR categories para ${doc.id}: ${e.message}")
            emptyList()
        }

        return User(
            uid = doc.id,
            name = doc.get<String>("name") ?: "",
            email = doc.get<String>("email") ?: "",
            identification = doc.get<String>("identification") ?: "",
            phone = doc.get<String>("phone") ?: "",
            status = doc.get<String>("status") ?: "",
            profilePicture = doc.get<String>("profilePicture") ?: "",
            role = doc.get<String>("role") ?: "",
            stars = doc.get<Double>("stars"),
            trustScore = doc.get<Long>("trustScore")?.toInt(),
            travelTime = doc.get<Long>("travelTime")?.toInt(),
            categories = categories,
            createdAt = doc.get<String>("createdAt") ?: ""
        )
    }
}