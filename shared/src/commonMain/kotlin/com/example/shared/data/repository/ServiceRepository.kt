package com.example.shared.data.repository

import com.example.shared.data.remote.IRemoteServicesDataSource
import com.example.shared.domain.entity.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ServiceRepository(
    private val remote: IRemoteServicesDataSource
) : IServiceRepository {

    override suspend fun getServicesByWorker(workerId: String): Flow<List<Service>> =
        remote.getServicesByWorker(workerId)
            .catch { e ->
                println("ERROR fetching services: ${e.message}")
                emit(emptyList())
            }
}