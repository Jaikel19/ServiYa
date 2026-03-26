package com.example.shared.data.remote

import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Reusable helper for Firestore subcollection CRUD operations.
 *
 * Handles the common pattern of: parentCollection/{parentId}/subcollection/{docId}
 *
 * Usage:
 * ```
 * private val crud = FirestoreSubcollectionCrud(db, "users", "addresses")
 *
 * fun getAll(userId: String) = crud.observeList(userId) { doc -> doc.data<Address>().copy(id = doc.id) }
 * fun getById(userId: String, id: String) = crud.getDocument(userId, id) { doc -> doc.data<Address>().copy(id = doc.id) }
 * fun create(userId: String, a: Address) = crud.addDocument(userId, a)
 * fun update(userId: String, a: Address) = crud.setDocument(userId, a.id, a)
 * fun delete(userId: String, id: String) = crud.deleteDocument(userId, id)
 * ```
 */
class FirestoreSubcollectionCrud(
    private val db: FirebaseFirestore,
    private val parentCollection: String,
    private val subcollection: String,
) {
  fun subcollectionRef(parentId: String): CollectionReference =
      db.collection(parentCollection).document(parentId).collection(subcollection)

  /**
   * Observes all documents in the subcollection as a Flow<List<T>>. [transform] maps each document
   * snapshot to the domain type.
   */
  fun <T> observeList(
      parentId: String,
      transform: (dev.gitlive.firebase.firestore.DocumentSnapshot) -> T?,
  ): Flow<List<T>> =
      subcollectionRef(parentId).snapshots.map { snapshot ->
        snapshot.documents.mapNotNull { doc ->
          try {
            transform(doc)
          } catch (e: Exception) {
            println("ERROR parsing $subcollection ${doc.id}: ${e.message}")
            null
          }
        }
      }

  /** Observes the first document in the subcollection as a Flow<T?>. */
  fun <T> observeFirst(
      parentId: String,
      transform: (dev.gitlive.firebase.firestore.DocumentSnapshot) -> T?,
  ): Flow<T?> =
      subcollectionRef(parentId).snapshots.map { snapshot ->
        snapshot.documents.firstOrNull()?.let { doc ->
          try {
            transform(doc)
          } catch (e: Exception) {
            println("ERROR parsing $subcollection: ${e.message}")
            null
          }
        }
      }

  /** Observes a single document by ID as a Flow<T?>. */
  fun <T> observeDocument(
      parentId: String,
      docId: String,
      transform: (dev.gitlive.firebase.firestore.DocumentSnapshot) -> T?,
  ): Flow<T?> =
      subcollectionRef(parentId).document(docId).snapshots.map { doc ->
        try {
          if (doc.exists) transform(doc) else null
        } catch (e: Exception) {
          println("ERROR parsing $subcollection/$docId: ${e.message}")
          null
        }
      }

  /** Gets a single document by ID (one-shot). */
  suspend fun <T> getDocument(
      parentId: String,
      docId: String,
      transform: (dev.gitlive.firebase.firestore.DocumentSnapshot) -> T?,
  ): T? =
      try {
        val doc = subcollectionRef(parentId).document(docId).get()
        if (doc.exists) transform(doc) else null
      } catch (e: Exception) {
        println("ERROR get $subcollection/$docId: ${e.message}")
        null
      }

  /** Adds a new document to the subcollection and returns its generated ID. */
  suspend fun addDocument(parentId: String, data: Any): String =
      try {
        val ref = subcollectionRef(parentId).add(data)
        ref.id
      } catch (e: Exception) {
        println("ERROR add to $subcollection: ${e.message}")
        ""
      }

  /** Sets (creates or overwrites) a document with a specific ID. */
  suspend fun setDocument(parentId: String, docId: String, data: Any) {
    try {
      subcollectionRef(parentId).document(docId).set(data)
    } catch (e: Exception) {
      println("ERROR set $subcollection/$docId: ${e.message}")
    }
  }

  /** Updates specific fields on a document. */
  suspend fun updateFields(parentId: String, docId: String, vararg fields: Pair<String, Any?>) {
    try {
      subcollectionRef(parentId).document(docId).update(*fields)
    } catch (e: Exception) {
      println("ERROR update $subcollection/$docId: ${e.message}")
    }
  }

  /** Deletes a document by ID. */
  suspend fun deleteDocument(parentId: String, docId: String) {
    try {
      subcollectionRef(parentId).document(docId).delete()
    } catch (e: Exception) {
      println("ERROR delete $subcollection/$docId: ${e.message}")
    }
  }
}
