package com.example.shared.data.remote.Category

import com.example.shared.domain.entity.Category
import kotlinx.coroutines.flow.Flow

interface IRemoteCategoryDataSource {
    suspend fun getCategories(): Flow<List<Category>>
    suspend fun getCategoryById(categoryId: String): Category?
    suspend fun createCategory(category: Category): String
    suspend fun updateCategory(category: Category)
}