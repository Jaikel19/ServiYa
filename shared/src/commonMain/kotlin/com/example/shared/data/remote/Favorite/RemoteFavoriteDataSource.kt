package com.example.shared.data.remote.Favorite

import com.example.shared.domain.entity.Favorite
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteFavoriteDataSource : IRemoteFavoriteDataSource {

    private val db = Firebase.firestore

    // GET ALL (realtime)
    override suspend fun getFavoritesByClient(clientId: String): Flow<List<Favorite>> {
        return db.collection("users")
            .document(clientId)
            .collection("favorites")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<Favorite>().copy(id = doc.id)
                    } catch (e: Exception) {
                        Favorite(id = doc.id)
                    }
                }
            }
    }

    // GET ONE
    override suspend fun getFavoriteById(clientId: String, favoriteId: String): Favorite? {
        return try {
            val doc = db.collection("users")
                .document(clientId)
                .collection("favorites")
                .document(favoriteId)
                .get()
            if (doc.exists) doc.data<Favorite>().copy(id = doc.id) else null
        } catch (e: Exception) {
            println("ERROR getFavoriteById: ${e.message}")
            null
        }
    }

    // ADD
    override suspend fun addFavorite(clientId: String, favorite: Favorite): String {
        return try {
            val ref = db.collection("users")
                .document(clientId)
                .collection("favorites")
                .add(favorite)
            ref.id
        } catch (e: Exception) {
            println("ERROR addFavorite: ${e.message}")
            ""
        }
    }

    // REMOVE
    override suspend fun removeFavorite(clientId: String, favoriteId: String) {
        try {
            db.collection("users")
                .document(clientId)
                .collection("favorites")
                .document(favoriteId)
                .delete()
        } catch (e: Exception) {
            println("ERROR removeFavorite: ${e.message}")
        }
    }
}