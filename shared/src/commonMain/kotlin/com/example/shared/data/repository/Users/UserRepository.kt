package com.example.shared.data.repository.Users

import com.example.shared.data.remote.Users.IRemoteUserDataSource
import com.example.shared.domain.entity.Users
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class UserRepository(
    private val remote: IRemoteUserDataSource
) : IUserRepository {

    override suspend fun getUserById(userId: String): Flow<Users?> =
        remote.getUserById(userId)
            .catch { e ->
                println("ERROR fetching user: ${e.message}")
                emit(null)
            }

    override suspend fun createUser(user: Users) =
        try {
            remote.createUser(user)
        } catch (e: Exception) {
            println("ERROR createUser: ${e.message}")
        }

    override suspend fun updateUser(user: Users) =
        try {
            remote.updateUser(user)
        } catch (e: Exception) {
            println("ERROR updateUser: ${e.message}")
        }

    override suspend fun deleteUser(userId: String) =
        try {
            remote.deleteUser(userId)
        } catch (e: Exception) {
            println("ERROR deleteUser: ${e.message}")
        }
}