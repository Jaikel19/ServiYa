package com.example.shared.data.repository.User

import com.example.shared.data.remote.User.IRemoteUserDataSource
import com.example.shared.data.repository.catchEmpty
import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val remote: IRemoteUserDataSource
) : IUserRepository {

    override suspend fun getAllWorkers(): Flow<List<User>> =
        remote.getAllWorkers().catchEmpty("fetching workers")

    override suspend fun getUserById(userId: String): User? =
        remote.getUserById(userId)

    override suspend fun getWorkZonesByUser(userId: String): Flow<List<WorkZone>> =
        remote.getWorkZonesByUser(userId).catchEmpty("fetching workZones")

    override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> =
        remote.getAddressesByUser(userId).catchEmpty("fetching addresses")

    override suspend fun createWorkZone(userId: String, zone: WorkZone) =
        remote.createWorkZone(userId, zone)

    override suspend fun updateWorkZoneBlocked(userId: String, zoneId: String, blocked: Boolean) =
        remote.updateWorkZoneBlocked(userId, zoneId, blocked)

    override suspend fun deleteWorkZone(userId: String, zoneId: String) =
        remote.deleteWorkZone(userId, zoneId)
}