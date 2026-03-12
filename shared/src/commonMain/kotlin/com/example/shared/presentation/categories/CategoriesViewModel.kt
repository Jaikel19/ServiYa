package com.example.shared.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.categories.ICategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val repository: ICategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState(isLoading = true))
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = CategoriesUiState(isLoading = true)

            repository.observeCategories()
                .catch { error ->
                    _uiState.value = CategoriesUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudieron cargar las categorías."
                    )
                }
                .collect { categories ->
                    _uiState.value = CategoriesUiState(
                        isLoading = false,
                        categories = categories,
                        errorMessage = null
                    )
                }
        }
    }
}