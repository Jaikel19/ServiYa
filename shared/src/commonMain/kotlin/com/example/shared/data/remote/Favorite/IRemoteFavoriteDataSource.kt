package com.example.shared.data.remote.Favorite

import com.example.shared.domain.entity.Favorite
import kotlinx.coroutines.flow.Flow

interface IRemoteFavoriteDataSource {
    suspend fun getFavoritesByClient(clientId: String): Flow<List<Favorite>>
    suspend fun getFavoriteById(clientId: String, favoriteId: String): Favorite?
    suspend fun addFavorite(clientId: String, favorite: Favorite): String
    suspend fun removeFavorite(clientId: String, favoriteId: String)
}