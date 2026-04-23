package com.example.shared.presentation.workerLocationCatalog

import com.example.shared.domain.entity.WorkZone

data class WorkerLocationCatalogUiState(
    val isLoading: Boolean = false,
    val locations: List<WorkZone> = emptyList(),      // alias != blank, blocked = false
    val blockedZones: List<WorkZone> = emptyList(),   // blocked = true
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
)
