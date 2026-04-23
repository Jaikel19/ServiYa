package com.example.shared.data.remote.Auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class RemoteAuthDataSource : IRemoteAuthDataSource {

    override suspend fun signIn(email: String, password: String): String {
        val result = Firebase.auth.signInWithEmailAndPassword(email.trim(), password)
        return result.user?.uid
            ?: throw IllegalStateException("No se pudo obtener el uid del usuario autenticado.")
    }

    override suspend fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }
}