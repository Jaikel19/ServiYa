package com.example.shared.data.repository.workersList

import com.example.shared.domain.entity.WorkerListItemData
import com.example.shared.presentation.workersList.WorkersListFilters
import kotlinx.coroutines.flow.Flow

interface IWorkersListRepository {
  suspend fun getWorkers(
    filters: WorkersListFilters = WorkersListFilters(),
  ): Flow<List<WorkerListItemData>>

  suspend fun getWorkersByIds(workerIds: Set<String>): List<WorkerListItemData>

  suspend fun getFavoriteWorkerIds(clientId: String): Flow<Set<String>>

  suspend fun addFavorite(clientId: String, workerId: String)

  suspend fun removeFavorite(clientId: String, workerId: String)
}