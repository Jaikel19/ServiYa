package com.example.shared.data.repository

import com.example.shared.domain.entity.Service
import kotlinx.coroutines.flow.Flow

interface IServiceRepository {
    suspend fun getServicesByWorker(workerId: String): Flow<List<Service>>
}