package com.example.shared.data.repository.Service

import com.example.shared.data.remote.Service.IRemoteServicesDataSource
import com.example.shared.data.repository.catchEmpty
import com.example.shared.data.repository.safeNullableCall
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.Service
import kotlinx.coroutines.flow.Flow

class ServiceRepository(
    private val remote: IRemoteServicesDataSource
) : IServiceRepository {

    override suspend fun getServicesByWorker(workerId: String): Flow<List<Service>> =
        remote.getServicesByWorker(workerId).catchEmpty("fetching services")

    override suspend fun getServiceById(workerId: String, serviceId: String): Service? =
        safeNullableCall("getServiceById") { remote.getServiceById(workerId, serviceId) }

    override suspend fun createService(workerId: String, service: Service): String =
        safeStringCall("createService") { remote.createService(workerId, service) }

    override suspend fun updateService(workerId: String, service: Service) =
        safeUnitCall("updateService") { remote.updateService(workerId, service) }

    override suspend fun deleteService(workerId: String, serviceId: String) =
        safeUnitCall("deleteService") { remote.deleteService(workerId, serviceId) }
}