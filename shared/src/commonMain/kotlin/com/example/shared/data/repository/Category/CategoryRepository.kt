package com.example.shared.data.repository.Category

import com.example.shared.data.remote.Category.IRemoteCategoryDataSource
import com.example.shared.domain.entity.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class CategoryRepository(
    private val remote: IRemoteCategoryDataSource
) : ICategoryRepository {

    override suspend fun getCategories(): Flow<List<Category>> =
        remote.getCategories()
            .catch { e ->
                println("ERROR fetching categories: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getCategoryById(categoryId: String): Category? =
        try {
            remote.getCategoryById(categoryId)
        } catch (e: Exception) {
            println("ERROR getCategoryById: ${e.message}")
            null
        }

    override suspend fun createCategory(category: Category): String =
        try {
            remote.createCategory(category)
        } catch (e: Exception) {
            println("ERROR createCategory: ${e.message}")
            ""
        }

    override suspend fun updateCategory(category: Category) =
        try {
            remote.updateCategory(category)
        } catch (e: Exception) {
            println("ERROR updateCategory: ${e.message}")
        }
}
