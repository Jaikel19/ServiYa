package com.example.shared.data.remote.Service

import com.example.shared.data.remote.FirestoreSubcollectionCrud
import com.example.shared.domain.entity.Service
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow

class RemoteServicesDataSource : IRemoteServicesDataSource {

  private val crud = FirestoreSubcollectionCrud(Firebase.firestore, "users", "services")

  override suspend fun getServicesByWorker(workerId: String): Flow<List<Service>> =
      crud.observeList(workerId) { doc -> doc.data<Service>().copy(id = doc.id) }

  override suspend fun getServiceById(workerId: String, serviceId: String): Service? =
      crud.getDocument(workerId, serviceId) { doc -> doc.data<Service>().copy(id = doc.id) }

  override suspend fun createService(workerId: String, service: Service): String =
      crud.addDocument(workerId, service)

  override suspend fun updateService(workerId: String, service: Service) =
      crud.setDocument(workerId, service.id, service)

  override suspend fun deleteService(workerId: String, serviceId: String) =
      crud.deleteDocument(workerId, serviceId)
}
