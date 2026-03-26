package com.example.shared.data.repository.favoriteWorkers

import com.example.shared.domain.entity.Favorite
import com.example.shared.domain.entity.WorkerListItemData
import kotlinx.coroutines.flow.Flow

interface IFavoriteWorkersRepository {
  suspend fun getFavoritesByClient(clientId: String): Flow<List<Favorite>>

  suspend fun getFavoriteWorkersByClient(clientId: String): Flow<List<WorkerListItemData>>

  suspend fun getFavoriteById(clientId: String, favoriteId: String): Favorite?

  suspend fun addFavorite(clientId: String, favorite: Favorite): String

  suspend fun removeFavorite(clientId: String, favoriteId: String)
}
