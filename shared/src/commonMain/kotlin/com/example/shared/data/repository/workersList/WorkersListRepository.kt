package com.example.shared.data.repository.workersList

import com.example.shared.data.remote.workersList.IRemoteWorkersListDataSource
import com.example.shared.data.remote.workersList.WorkerRemoteItem
import com.example.shared.data.repository.Service.IServiceRepository
import com.example.shared.domain.entity.WorkerListItemData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WorkersListRepository(
    private val remote: IRemoteWorkersListDataSource,
    private val servicesRepository: IServiceRepository,
) : IWorkersListRepository {

  private suspend fun getStartingPrice(workerId: String): Double {
    val services =
        try {
          servicesRepository.getServicesByWorker(workerId).first()
        } catch (e: Exception) {
          emptyList()
        }

    return services.minOfOrNull { it.cost } ?: 0.0
  }

  private suspend fun buildWorkerListItem(workerRemote: WorkerRemoteItem): WorkerListItemData {
    val workerId = workerRemote.workerId
    val profile = workerRemote.profile

    val address = remote.getWorkerAddress(workerId)
    val categoryNames = remote.getCategoryNames(profile.categories)
    val schedule = remote.getWorkerSchedule(workerId)
    val appointments = remote.getWorkerAppointments(workerId)
    val workZones = remote.getWorkerWorkZones(workerId)
    val startingPrice = getStartingPrice(workerId)

    return WorkerListItemData(
        workerId = workerId,
        uid = profile.uid,
        name = profile.name,
        profilePictureLink = profile.profilePicture,
        stars = profile.stars,
        status = profile.status,
        categoryIds = profile.categories,
        categoryNames = categoryNames,
        province = address?.province.orEmpty(),
        canton = address?.canton.orEmpty(),
        district = address?.district.orEmpty(),
        startingPrice = startingPrice,
        schedule = schedule,
        appointments = appointments,
        workZones = workZones,
    )
  }

  private suspend fun buildWorkerPreviewItem(workerRemote: WorkerRemoteItem): WorkerListItemData =
      coroutineScope {
        val workerId = workerRemote.workerId
        val profile = workerRemote.profile

        val addressDeferred = async { remote.getWorkerAddress(workerId) }
        val categoryNamesDeferred = async { remote.getCategoryNames(profile.categories) }
        val startingPriceDeferred = async { getStartingPrice(workerId) }

        val address = addressDeferred.await()
        val categoryNames = categoryNamesDeferred.await()
        val startingPrice = startingPriceDeferred.await()

        WorkerListItemData(
            workerId = workerId,
            uid = profile.uid,
            name = profile.name,
            profilePictureLink = profile.profilePicture,
            stars = profile.stars,
            status = profile.status,
            categoryIds = profile.categories,
            categoryNames = categoryNames,
            province = address?.province.orEmpty(),
            canton = address?.canton.orEmpty(),
            district = address?.district.orEmpty(),
            startingPrice = startingPrice,
        )
      }

  override suspend fun getWorkers(): Flow<List<WorkerListItemData>> {
    return remote.getWorkers().map { workers ->
      workers
          .map { workerRemote -> buildWorkerListItem(workerRemote) }
          .sortedByDescending { it.stars }
    }
  }

  override suspend fun getWorkersByIds(workerIds: Set<String>): List<WorkerListItemData> {
    return coroutineScope {
          remote
              .getWorkersByIds(workerIds)
              .map { workerRemote -> async { buildWorkerPreviewItem(workerRemote) } }
              .awaitAll()
        }
        .sortedByDescending { it.stars }
  }

  override suspend fun getFavoriteWorkerIds(clientId: String): Flow<Set<String>> {
    return remote.getFavoriteWorkerIds(clientId)
  }

  override suspend fun addFavorite(clientId: String, workerId: String) {
    remote.addFavorite(clientId, workerId)
  }

  override suspend fun removeFavorite(clientId: String, workerId: String) {
    remote.removeFavorite(clientId, workerId)
  }
}
