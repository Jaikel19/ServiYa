package com.example.shared.data.repository.favoriteWorkers

import com.example.shared.data.remote.favoriteWorkers.IRemoteFavoriteWorkersDataSource
import com.example.shared.data.repository.catchEmpty
import com.example.shared.data.repository.safeNullableCall
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.data.repository.workersList.IWorkersListRepository
import com.example.shared.domain.entity.Favorite
import com.example.shared.domain.entity.WorkerListItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class FavoriteWorkersRepository(
    private val remote: IRemoteFavoriteWorkersDataSource,
    private val workersListRepository: IWorkersListRepository,
) : IFavoriteWorkersRepository {

  override suspend fun getFavoritesByClient(clientId: String): Flow<List<Favorite>> =
      remote.getFavoritesByClient(clientId).catchEmpty("fetching favorites")

  override suspend fun getFavoriteWorkersByClient(
      clientId: String
  ): Flow<List<WorkerListItemData>> =
      remote
          .getFavoritesByClient(clientId)
          .mapLatest { favorites ->
            val favoriteIds =
                favorites.map { it.workerId.trim() }.filter { it.isNotBlank() }.toSet()

            if (favoriteIds.isEmpty()) {
              emptyList()
            } else {
              workersListRepository.getWorkersByIds(favoriteIds)
            }
          }
          .catchEmpty("fetching favorite workers")

  override suspend fun getFavoriteById(clientId: String, favoriteId: String): Favorite? =
      safeNullableCall("getFavoriteById") { remote.getFavoriteById(clientId, favoriteId) }

  override suspend fun addFavorite(clientId: String, favorite: Favorite): String =
      safeStringCall("addFavorite") { remote.addFavorite(clientId, favorite) }

  override suspend fun removeFavorite(clientId: String, favoriteId: String) =
      safeUnitCall("removeFavorite") { remote.removeFavorite(clientId, favoriteId) }
}
