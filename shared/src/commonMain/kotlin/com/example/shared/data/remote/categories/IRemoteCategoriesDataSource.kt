package com.example.shared.data.remote.categories

import com.example.shared.domain.entity.Category
import kotlinx.coroutines.flow.Flow

interface IRemoteCategoriesDataSource {
  fun observeCategories(): Flow<List<Category>>
}
