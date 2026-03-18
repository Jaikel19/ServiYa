package com.example.shared.presentation.workersList

import com.example.shared.domain.entity.WorkerListItemData

data class WorkersListUiState(
    val isLoading: Boolean = false,
    val workers: List<WorkerListItemData> = emptyList(),
    val favoriteWorkerIds: Set<String> = emptySet(),
    val errorMessage: String? = null
)