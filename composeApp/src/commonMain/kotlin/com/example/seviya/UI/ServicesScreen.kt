package com.example.seviya.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.presentation.services.ServicesViewModel

@Composable
fun ServicesScreen(viewModel: ServicesViewModel) {

    val state = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Servicios cargados: ${state.services.size}")

        if (state.isLoading) {
            Text("Cargando servicios...")
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.services.forEach { service ->
            Text("- ${service.title} ₡${service.price}")
        }
    }
}