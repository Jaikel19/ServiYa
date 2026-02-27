package com.example.shared.data.remote

import com.example.shared.domain.entity.Service
import kotlinx.coroutines.flow.Flow

interface IRemoteServicesDataSource {
    fun getServices(): Flow<List<Service>>
}