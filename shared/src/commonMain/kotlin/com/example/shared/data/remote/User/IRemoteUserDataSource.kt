package com.example.shared.data.remote.User

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone
import kotlinx.coroutines.flow.Flow

interface IRemoteUserDataSource {
  suspend fun getAllWorkers(): Flow<List<User>>

  suspend fun getUserById(userId: String): User?

  suspend fun getWorkZonesByUser(userId: String): Flow<List<WorkZone>>

  suspend fun getAddressesByUser(userId: String): Flow<List<Address>>

  suspend fun createWorkZone(userId: String, zone: WorkZone)

  suspend fun updateWorkZoneBlocked(userId: String, zoneId: String, blocked: Boolean)

  suspend fun deleteWorkZone(userId: String, zoneId: String)
}