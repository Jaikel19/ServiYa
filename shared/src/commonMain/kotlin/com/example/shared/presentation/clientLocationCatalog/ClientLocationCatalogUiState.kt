package com.example.shared.presentation.clientLocationCatalog

import com.example.shared.domain.entity.Address

data class ClientLocationCatalogUiState(
    val isLoading: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
)
