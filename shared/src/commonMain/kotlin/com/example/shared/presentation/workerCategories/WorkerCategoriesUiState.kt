package com.example.shared.presentation.workerCategories

import com.example.shared.domain.entity.Category

data class WorkerCategoriesUiState(
    val isLoading: Boolean = false,
    val allCategories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
)
