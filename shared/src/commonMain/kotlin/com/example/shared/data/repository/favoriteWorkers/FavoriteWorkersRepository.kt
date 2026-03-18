package com.example.shared.data.repository.favoriteWorkers

import com.example.shared.data.remote.favoriteWorkers.IRemoteFavoriteWorkersDataSource
import com.example.shared.data.repository.workersList.IWorkersListRepository
import com.example.shared.domain.entity.Favorite
import com.example.shared.domain.entity.WorkerListItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

class FavoriteWorkersRepository(
    private val remote: IRemoteFavoriteWorkersDataSource,
    private val workersListRepository: IWorkersListRepository
) : IFavoriteWorkersRepository {

    override suspend fun getFavoritesByClient(clientId: String): Flow<List<Favorite>> =
        remote.getFavoritesByClient(clientId)
            .catch {
                emit(emptyList())
            }

    override suspend fun getFavoriteWorkersByClient(clientId: String): Flow<List<WorkerListItemData>> =
        combine(
            remote.getFavoritesByClient(clientId),
            workersListRepository.getWorkers()
        ) { favorites, workers ->

            val favoriteIds = favorites
                .map { it.workerId.trim() }
                .filter { it.isNotBlank() }

            workers.filter { worker ->
                favoriteIds.contains(worker.workerId.trim())
            }
        }.catch {
            emit(emptyList())
        }

    override suspend fun getFavoriteById(clientId: String, favoriteId: String): Favorite? =
        try {
            remote.getFavoriteById(clientId, favoriteId)
        } catch (e: Exception) {
            null
        }

    override suspend fun addFavorite(clientId: String, favorite: Favorite): String =
        try {
            remote.addFavorite(clientId, favorite)
        } catch (e: Exception) {
            ""
        }

    override suspend fun removeFavorite(clientId: String, favoriteId: String) =
        try {
            remote.removeFavorite(clientId, favoriteId)
        } catch (e: Exception) {
        }
}