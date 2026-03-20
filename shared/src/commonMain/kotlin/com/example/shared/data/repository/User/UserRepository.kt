package com.example.shared.data.repository.User


import com.example.shared.data.remote.User.IRemoteUserDataSource
import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import com.example.shared.domain.entity.Address
class UserRepository(
    private val remote: IRemoteUserDataSource
) : IUserRepository {

    override suspend fun getAllWorkers(): Flow<List<User>> =
        remote.getAllWorkers()
            .catch { e ->
                println("ERROR fetching workers: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getUserById(userId: String): Flow<User?> =
        remote.getUserById(userId)
            .catch { e ->
                println("ERROR fetching user: ${e.message}")
                emit(null)
            }

    override suspend fun getWorkZonesByUser(userId: String): Flow<List<WorkZone>> =
        remote.getWorkZonesByUser(userId)
            .catch { e ->
                println("ERROR fetching workZones: ${e.message}")
                emit(emptyList())
            }
    override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> =
        remote.getAddressesByUser(userId)
            .catch { e ->
                println("ERROR fetching addresses: ${e.message}")
                emit(emptyList())
            }
}