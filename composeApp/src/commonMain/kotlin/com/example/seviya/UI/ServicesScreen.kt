package com.example.seviya.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.presentation.services.ServicesViewModel

@Composable
fun ServicesScreen(viewModel: ServicesViewModel) {

    val state = viewModel.uiState.collectAsState().value

    // Cambia este ID por un UID real de tu Firestore para probar
    LaunchedEffect(Unit) {
        viewModel.loadServices("p25ZTOWaBs1w4lwK9O5H")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Loading services...")
            }
            state.errorMessage != null -> {
                Text("Error: ${state.errorMessage}")
            }
            state.services.isEmpty() -> {
                Text("No services found.")
            }
            else -> {
                Text("Services found: ${state.services.size}")
                Spacer(modifier = Modifier.height(16.dp))
                state.services.forEach { service ->
                    Text("- ${service.name} ₡${service.cost}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}