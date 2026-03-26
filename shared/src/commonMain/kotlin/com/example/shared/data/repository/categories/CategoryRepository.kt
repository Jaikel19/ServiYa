package com.example.shared.data.repository.categories

import com.example.shared.data.remote.categories.IRemoteCategoriesDataSource
import com.example.shared.domain.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val remoteDataSource: IRemoteCategoriesDataSource) :
    ICategoryRepository {

  override fun observeCategories(): Flow<List<Category>> {
    return remoteDataSource.observeCategories()
  }
}
