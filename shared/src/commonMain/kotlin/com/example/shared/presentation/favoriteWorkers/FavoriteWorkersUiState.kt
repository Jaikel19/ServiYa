package com.example.shared.presentation.favoriteWorkers

import com.example.shared.domain.entity.WorkerListItemData

data class FavoriteWorkersUiState(
    val isLoading: Boolean = false,
    val workers: List<WorkerListItemData> = emptyList(),
    val errorMessage: String? = null,
    val debugMessage: String = ""
)