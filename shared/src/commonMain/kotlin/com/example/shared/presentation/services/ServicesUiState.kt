package com.example.shared.presentation.services

import com.example.shared.domain.entity.Service

data class ServicesUiState(
    val isLoading: Boolean = false,
    val services: List<Service> = emptyList(),
    val errorMessage: String? = null,
)
