package com.example.shared.presentation.requestAppointment

import com.example.shared.domain.entity.Address

data class RequestAppointmentUiState(
    val isCreating: Boolean = false,
    val isCreated: Boolean = false,
    val createdAppointmentId: String? = null,
    val errorMessage: String? = null,
    val isLoadingAddresses: Boolean = false,
    val savedAddresses: List<Address> = emptyList(),
    val selectedAddressId: String = "",
)
