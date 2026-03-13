package com.example.shared.data.repository.Favorite

import com.example.shared.data.remote.Favorite.IRemoteFavoriteDataSource
import com.example.shared.domain.entity.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class FavoriteRepository(
    private val remote: IRemoteFavoriteDataSource
) : IFavoriteRepository {

    override suspend fun getFavoritesByClient(clientId: String): Flow<List<Favorite>> =
        remote.getFavoritesByClient(clientId)
            .catch { e ->
                println("ERROR fetching favorites: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getFavoriteById(clientId: String, favoriteId: String): Favorite? =
        try {
            remote.getFavoriteById(clientId, favoriteId)
        } catch (e: Exception) {
            println("ERROR getFavoriteById: ${e.message}")
            null
        }

    override suspend fun addFavorite(clientId: String, favorite: Favorite): String =
        try {
            remote.addFavorite(clientId, favorite)
        } catch (e: Exception) {
            println("ERROR addFavorite: ${e.message}")
            ""
        }

    override suspend fun removeFavorite(clientId: String, favoriteId: String) =
        try {
            remote.removeFavorite(clientId, favoriteId)
        } catch (e: Exception) {
            println("ERROR removeFavorite: ${e.message}")
        }
}