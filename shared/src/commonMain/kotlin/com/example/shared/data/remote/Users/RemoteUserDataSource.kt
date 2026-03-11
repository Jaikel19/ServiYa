package com.example.shared.data.remote.Users

import com.example.shared.domain.entity.Users
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteUserDataSource : IRemoteUserDataSource {

    private val db = Firebase.firestore

    // GET ONE (realtime)
    override suspend fun getUserById(userId: String): Flow<Users?> {
        return db.collection("users")
            .document(userId)
            .snapshots
            .map { doc ->
                try {
                    if (doc.exists) doc.data<Users>().copy(uid = doc.id) else null
                } catch (e: Exception) {
                    println("ERROR parsing user: ${e.message}")
                    null
                }
            }
    }

    // CREATE
    override suspend fun createUser(user: Users) {
        try {
            db.collection("users")
                .document(user.uid)
                .set(user)
        } catch (e: Exception) {
            println("ERROR createUser: ${e.message}")
        }
    }

    // UPDATE
    override suspend fun updateUser(user: Users) {
        try {
            db.collection("users")
                .document(user.uid)
                .set(user)
        } catch (e: Exception) {
            println("ERROR updateUser: ${e.message}")
        }
    }

    // DELETE
    override suspend fun deleteUser(userId: String) {
        try {
            db.collection("users")
                .document(userId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteUser: ${e.message}")
        }
    }
}