package com.example.shared.data.repository.Users

import com.example.shared.domain.entity.Users
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun getUserById(userId: String): Flow<Users?>
    suspend fun createUser(user: Users)
    suspend fun updateUser(user: Users)
    suspend fun deleteUser(userId: String)
}