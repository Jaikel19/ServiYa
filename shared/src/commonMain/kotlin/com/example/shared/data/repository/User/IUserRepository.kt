package com.example.shared.data.repository.User

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
  suspend fun getAllWorkers(): Flow<List<User>>

  suspend fun getUserById(userId: String): User?

  suspend fun getWorkZonesByUser(userId: String): Flow<List<WorkZone>>

  suspend fun getAddressesByUser(userId: String): Flow<List<Address>>
}