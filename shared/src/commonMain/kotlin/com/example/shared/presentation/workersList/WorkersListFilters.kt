package com.example.shared.presentation.workersList

data class WorkersListFilters(
    val selectedCategoryId: String? = null,
    val selectedCategoryName: String? = null,
    val searchQuery: String = "",
    val selectedProvince: String = "",
    val selectedCanton: String = "",
    val selectedDistrict: String = "",
    val selectedDayKey: String = "",
) {
    fun hasLocationFilter(): Boolean = selectedProvince.isNotBlank()

    fun hasDayFilter(): Boolean = selectedDayKey.isNotBlank()
}