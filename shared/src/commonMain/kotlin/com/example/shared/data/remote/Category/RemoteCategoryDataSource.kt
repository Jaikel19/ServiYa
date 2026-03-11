package com.example.shared.data.remote.Category

import com.example.shared.domain.entity.Category
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteCategoryDataSource : IRemoteCategoryDataSource {

    private val db = Firebase.firestore

    // GET ALL (realtime)
    override suspend fun getCategories(): Flow<List<Category>> {
        return db.collection("categories")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<Category>().copy(id = doc.id)
                    } catch (e: Exception) {
                        Category(id = doc.id, name = "Error parsing")
                    }
                }
            }
    }

    // GET ONE
    override suspend fun getCategoryById(categoryId: String): Category? {
        return try {
            val doc = db.collection("categories")
                .document(categoryId)
                .get()
            if (doc.exists) doc.data<Category>().copy(id = doc.id) else null
        } catch (e: Exception) {
            println("ERROR getCategoryById: ${e.message}")
            null
        }
    }

    // CREATE
    override suspend fun createCategory(category: Category): String {
        return try {
            val ref = db.collection("categories").add(category)
            ref.id
        } catch (e: Exception) {
            println("ERROR createCategory: ${e.message}")
            ""
        }
    }

    // UPDATE
    override suspend fun updateCategory(category: Category) {
        try {
            db.collection("categories")
                .document(category.id)
                .set(category)
        } catch (e: Exception) {
            println("ERROR updateCategory: ${e.message}")
        }
    }
}