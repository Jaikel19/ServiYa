package com.example.shared.presentation.services

import com.example.shared.domain.entity.Category

data class CategoriesUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null
)