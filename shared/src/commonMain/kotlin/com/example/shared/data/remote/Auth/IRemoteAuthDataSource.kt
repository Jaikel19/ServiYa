package com.example.shared.data.remote.Auth

interface IRemoteAuthDataSource {
    suspend fun signIn(email: String, password: String): String

    suspend fun getCurrentUserId(): String?

    suspend fun signOut()
}