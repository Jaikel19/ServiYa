package com.example.shared.presentation.clientMap

import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Category
import com.example.shared.domain.entity.User
import com.example.shared.domain.entity.WorkZone

data class WorkerMapMarker(val user: User, val workZone: WorkZone)

data class ClientMapUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val markers: List<WorkerMapMarker> = emptyList(),
    val filteredMarkers: List<WorkerMapMarker> = emptyList(),
    val selectedMarker: WorkerMapMarker? = null,
    val clientAddress: Address? = null,
    val addresses: List<Address> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val availableCategories: List<Category> = emptyList(),
    val minStars: Double? = null,
    val categoryQuery: String = "",
)
