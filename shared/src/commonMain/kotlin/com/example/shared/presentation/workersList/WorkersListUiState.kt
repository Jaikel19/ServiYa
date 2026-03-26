package com.example.shared.presentation.workersList

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.WorkerListItemData

data class WorkersListUiState(
    val isLoading: Boolean = false,
    val isLoadingFavorites: Boolean = false,
    val isLoadingAddresses: Boolean = false,
    val workers: List<WorkerListItemData> = emptyList(),
    val savedAddresses: List<Address> = emptyList(),
    val selectedAddressId: String? = null,
    val favoriteWorkerIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
)
