package com.example.shared.presentation.clientToWorkerReview

import com.example.shared.domain.entity.Appointment

data class ClientToWorkerReviewUiState(
    val appointment: Appointment? = null,
    val rating: Int = 0,
    val comment: String = "",
    val imageUrls: List<String> = emptyList(),
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)