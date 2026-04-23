package com.example.shared.data.remote.categories

import com.example.shared.domain.entity.Category
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteCategoriesDataSource : IRemoteCategoriesDataSource {

  private val db = Firebase.firestore

  override fun observeCategories(): Flow<List<Category>> {
    return db.collection("categories").snapshots.map { querySnapshot ->
      querySnapshot.documents
          .mapNotNull { document ->
            runCatching {
                  val category = document.data<Category>()
                  category.copy(
                      id = if (category.id.isBlank()) document.id else category.id,
                      name = if (category.name.isBlank()) formatId(document.id) else category.name,
                  )
                }
                .getOrNull()
          }
          .sortedBy { it.name.lowercase() }
    }
  }

  private fun formatId(raw: String): String {
    return raw.replace("-", " ")
        .replace("_", " ")
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
          word.lowercase().replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
          }
        }
  }
}
