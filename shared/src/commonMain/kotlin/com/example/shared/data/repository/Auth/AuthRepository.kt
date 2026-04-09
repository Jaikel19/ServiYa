package com.example.shared.data.repository.Auth

import com.example.shared.data.remote.Auth.IRemoteAuthDataSource

class AuthRepository(
    private val remote: IRemoteAuthDataSource
) : IAuthRepository {

    override suspend fun signIn(email: String, password: String): String =
        remote.signIn(email, password)

    override suspend fun getCurrentUserId(): String? =
        remote.getCurrentUserId()

    override suspend fun signOut() {
        remote.signOut()
    }
}