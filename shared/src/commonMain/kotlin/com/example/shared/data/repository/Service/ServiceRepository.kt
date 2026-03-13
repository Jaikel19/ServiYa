package com.example.shared.data.repository.Service

import com.example.shared.data.remote.Service.IRemoteServicesDataSource
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

    override suspend fun getServiceById(workerId: String, serviceId: String): Service? =
        try {
            remote.getServiceById(workerId, serviceId)
        } catch (e: Exception) {
            println("ERROR getServiceById: ${e.message}")
            null
        }

    override suspend fun createService(workerId: String, service: Service): String =
        try {
            remote.createService(workerId, service)
        } catch (e: Exception) {
            println("ERROR createService: ${e.message}")
            ""
        }

    override suspend fun updateService(workerId: String, service: Service) =
        try {
            remote.updateService(workerId, service)
        } catch (e: Exception) {
            println("ERROR updateService: ${e.message}")
        }
    override suspend fun deleteService(workerId: String, serviceId: String) =
        try {
            remote.deleteService(workerId, serviceId)
        } catch (e: Exception) {
            println("ERROR deleteService: ${e.message}")
        }
}