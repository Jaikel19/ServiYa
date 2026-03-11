package com.example.shared.data.repository.Service

import com.example.shared.domain.entity.Service
import kotlinx.coroutines.flow.Flow

interface IServiceRepository {
    suspend fun getServicesByWorker(workerId: String): Flow<List<Service>>
    suspend fun getServiceById(workerId: String, serviceId: String): Service?
    suspend fun createService(workerId: String, service: Service): String
    suspend fun updateService(workerId: String, service: Service)
}