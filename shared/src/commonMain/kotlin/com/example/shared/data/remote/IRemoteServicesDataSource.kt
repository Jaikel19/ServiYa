package com.example.shared.data.remote

import com.example.shared.domain.entity.Service
import kotlinx.coroutines.flow.Flow

interface IRemoteServicesDataSource {
    fun getServicesByWorker(workerId: String): Flow<List<Service>>
}