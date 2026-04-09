package com.example.shared.data.repository.Auth

interface IAuthRepository {
    suspend fun signIn(email: String, password: String): String

    suspend fun getCurrentUserId(): String?

    suspend fun signOut()
}