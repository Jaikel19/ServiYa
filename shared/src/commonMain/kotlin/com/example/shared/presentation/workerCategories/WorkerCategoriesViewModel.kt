package com.example.shared.presentation.workerCategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.categories.ICategoryRepository
import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WorkerCategoriesViewModel(
    private val categoryRepository: ICategoryRepository,
    private val profileRepository: IProfessionalProfileRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(WorkerCategoriesUiState(isLoading = true))
  val uiState: StateFlow<WorkerCategoriesUiState> = _uiState.asStateFlow()

  fun loadData(workerId: String) {
    viewModelScope.launch {
      combine(
              categoryRepository.observeCategories(),
              profileRepository.getWorkerCategoryIds(workerId),
          ) { allCategories, selectedIds ->
            Pair(allCategories, selectedIds.toSet())
          }
          .catch { e ->
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar categorías",
                )
          }
          .collect { (allCategories, selectedIds) ->
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    allCategories = allCategories,
                    selectedCategoryIds = selectedIds,
                )
          }
    }
  }

  fun toggleCategory(categoryId: String) {
    val current = _uiState.value.selectedCategoryIds.toMutableSet()
    if (current.contains(categoryId)) current.remove(categoryId) else current.add(categoryId)
    _uiState.value = _uiState.value.copy(selectedCategoryIds = current)
  }

  fun updateSearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun saveCategories(workerId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
      try {
        profileRepository.updateWorkerCategories(
            workerId,
            _uiState.value.selectedCategoryIds.toList(),
        )
        _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(
                isSaving = false,
                errorMessage = e.message ?: "Error al guardar categorías",
            )
      }
    }
  }

  fun clearSaveSuccess() {
    _uiState.value = _uiState.value.copy(saveSuccess = false)
  }
}
