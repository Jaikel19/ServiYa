package com.example.shared.data.remote.User

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone
import kotlinx.coroutines.flow.Flow

interface IRemoteUserDataSource {
    suspend fun getAllWorkers(): Flow<List<User>>
    suspend fun getUserById(userId: String): Flow<User?>
    suspend fun getWorkZonesByUser(userId: String): Flow<List<WorkZone>>
    suspend fun getAddressesByUser(userId: String): Flow<List<Address>>
}