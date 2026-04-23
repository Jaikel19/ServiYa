package com.example.shared.data.remote.favoriteWorkers

import com.example.shared.domain.entity.Favorite
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteFavoriteWorkersDataSource : IRemoteFavoriteWorkersDataSource {

  private val db = Firebase.firestore

  override suspend fun getFavoritesByClient(clientId: String): Flow<List<Favorite>> {
    return db.collection("users").document(clientId).collection("favorites").snapshots.map {
        querySnapshot ->
      querySnapshot.documents.mapNotNull { document -> parseFavorite(document) }
    }
  }

  override suspend fun getFavoriteById(clientId: String, favoriteId: String): Favorite? {
    return try {
      val document =
          db.collection("users")
              .document(clientId)
              .collection("favorites")
              .document(favoriteId)
              .get()

      parseFavorite(document)
    } catch (e: Exception) {
      println("ERROR getFavoriteById remote: ${e.message}")
      null
    }
  }

  override suspend fun addFavorite(clientId: String, favorite: Favorite): String {
    if (clientId.isBlank()) return ""
    if (favorite.id.isBlank() && favorite.workerId.isBlank()) return ""

    val documentId =
        when {
          favorite.id.isNotBlank() -> favorite.id
          favorite.workerId.isNotBlank() -> "fav_${favorite.workerId}"
          else -> ""
        }

    if (documentId.isBlank()) return ""

    val favoriteToSave = favorite.copy(id = documentId)

    db.collection("users")
        .document(clientId)
        .collection("favorites")
        .document(documentId)
        .set(favoriteToSave)

    return documentId
  }

  override suspend fun removeFavorite(clientId: String, favoriteId: String) {
    if (clientId.isBlank() || favoriteId.isBlank()) return

    db.collection("users").document(clientId).collection("favorites").document(favoriteId).delete()
  }

  private fun parseFavorite(document: DocumentSnapshot): Favorite? {
    return try {
      val id = readString(document, "id").ifBlank { document.id }
      val workerId = readString(document, "workerId")
      val workerName = readString(document, "workerName")
      val addedAt = readString(document, "addedAt")

      if (workerId.isBlank()) return null

      Favorite(id = id, workerId = workerId, workerName = workerName, addedAt = addedAt)
    } catch (e: Exception) {
      println("ERROR parseFavorite ${document.id}: ${e.message}")
      null
    }
  }

  private fun readString(document: DocumentSnapshot, field: String): String {
    return try {
      document.get<String>(field)
    } catch (e: Exception) {
      ""
    }
  }
}
