package com.example.shared.data.remote.workersList

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.WorkZone
import com.example.shared.domain.entity.WorkerProfile
import com.example.shared.domain.entity.WorkerSchedule
import kotlinx.coroutines.flow.Flow

data class WorkerRemoteItem(
  val workerId: String,
  val profile: WorkerProfile,
)

interface IRemoteWorkersListDataSource {
  suspend fun getWorkers(): Flow<List<WorkerRemoteItem>>

  suspend fun getWorkersByIds(workerIds: Set<String>): List<WorkerRemoteItem>

  suspend fun getCategoryNamesMap(categoryIds: Set<String>): Map<String, String>

  suspend fun getWorkerAddress(workerId: String): Address?

  suspend fun getWorkerSchedule(workerId: String): List<WorkerSchedule>

  suspend fun getWorkerAppointments(workerId: String): List<Appointment>

  suspend fun getWorkerWorkZones(workerId: String): List<WorkZone>

  suspend fun getFavoriteWorkerIds(clientId: String): Flow<Set<String>>

  suspend fun addFavorite(clientId: String, workerId: String)

  suspend fun removeFavorite(clientId: String, workerId: String)
}