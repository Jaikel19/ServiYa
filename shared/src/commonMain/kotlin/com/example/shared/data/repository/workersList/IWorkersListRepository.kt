package com.example.shared.data.repository.workersList

import com.example.shared.domain.entity.WorkerListItemData
import kotlinx.coroutines.flow.Flow

interface IWorkersListRepository {
    suspend fun getWorkers(): Flow<List<WorkerListItemData>>
}