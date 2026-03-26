package com.example.shared.data.repository.categories

import com.example.shared.domain.entity.Category
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
  fun observeCategories(): Flow<List<Category>>
}
