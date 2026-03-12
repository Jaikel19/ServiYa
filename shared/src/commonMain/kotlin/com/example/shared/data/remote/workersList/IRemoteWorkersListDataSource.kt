package com.example.shared.data.remote.workersList

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.WorkerProfile
import kotlinx.coroutines.flow.Flow

data class WorkerRemoteItem(
    val workerId: String,
    val profile: WorkerProfile
)

interface IRemoteWorkersListDataSource {
    suspend fun getWorkers(): Flow<List<WorkerRemoteItem>>
    suspend fun getCategoryNames(categoryIds: List<String>): List<String>
    suspend fun getWorkerAddress(workerId: String): Address?
}