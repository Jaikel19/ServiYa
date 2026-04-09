package com.example.shared.data.remote.User

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Category
import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RemoteUserDataSource : IRemoteUserDataSource {

    private val db = Firebase.firestore

    override suspend fun getAllWorkers(): Flow<List<User>> = flow {
        val snapshot = db.collection("users").get()

        val workers =
            snapshot.documents.mapNotNull { doc ->
                try {
                    if (doc.id == "_schema") return@mapNotNull null
                    val user = parseUser(doc)
                    if (user.role != "worker") return@mapNotNull null
                    user
                } catch (e: Exception) {
                    println("ERROR parsing user ${doc.id}: ${e.message}")
                    null
                }
            }

        emit(workers)
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val directDoc = db.collection("users").document(userId).get()

            if (directDoc.exists) {
                parseUser(directDoc)
            } else {
                val snapshot = db.collection("users").get()

                val matchedDoc =
                    snapshot.documents.firstOrNull { doc ->
                        if (doc.id == "_schema") return@firstOrNull false

                        val storedUid =
                            runCatching { doc.get<String?>("uid") ?: "" }
                                .getOrDefault("")

                        storedUid == userId
                    }

                matchedDoc?.let { parseUser(it) }
            }
        } catch (e: Exception) {
            println("ERROR resolving user $userId: ${e.message}")
            null
        }
    }

    override suspend fun getWorkZonesByUser(userId: String): Flow<List<WorkZone>> = flow {
        val snapshot = db.collection("users").document(userId).collection("workZones").get()

        val zones =
            snapshot.documents.mapNotNull { doc ->
                try {
                    if (doc.id == "_schema") return@mapNotNull null
                    doc.data<WorkZone>().copy(id = doc.id)
                } catch (e: Exception) {
                    println("ERROR parsing workZone ${doc.id}: ${e.message}")
                    null
                }
            }

        emit(zones)
    }

    override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> {
        return db.collection("users").document(userId).collection("addresses").snapshots.map { snapshot ->
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
        val categories =
            try {
                val rawCategories = doc.get<List<String>>("categories") ?: emptyList()
                rawCategories.map { Category(id = it, name = it) }
            } catch (e: Exception) {
                println("ERROR categories para ${doc.id}: ${e.message}")
                emptyList()
            }

        return User(
            uid = doc.id,
            name = doc.get<String?>("name") ?: "",
            email = doc.get<String?>("email") ?: "",
            identification = doc.get<String?>("identification") ?: "",
            phone = doc.get<String?>("phone") ?: "",
            status = doc.get<String?>("status") ?: "",
            profilePicture = doc.get<String?>("profilePicture") ?: "",
            role = doc.get<String?>("role") ?: "",
            stars = readDouble(doc, "stars"),
            trustScore = readInt(doc, "trustScore"),
            travelTime = readInt(doc, "travelTime"),
            categories = categories,
            createdAt = readCreatedAt(doc),
        )
    }

    private fun readCreatedAt(doc: DocumentSnapshot): String {
        return runCatching { doc.get<String?>("createdAt") ?: "" }
            .getOrElse { "" }
    }

    private fun readInt(doc: DocumentSnapshot, field: String): Int? {
        return runCatching { doc.get<Long>(field)?.toInt() }
            .getOrElse {
                runCatching { doc.get<Int>(field) }
                    .getOrNull()
            }
    }

    private fun readDouble(doc: DocumentSnapshot, field: String): Double? {
        return runCatching { doc.get<Double>(field) }
            .getOrElse {
                runCatching { doc.get<Long>(field)?.toDouble() }
                    .getOrNull()
            }
    }
}