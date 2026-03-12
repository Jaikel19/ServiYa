package com.example.seviya.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.domain.entity.Service
import com.example.shared.presentation.services.ServicesViewModel

@Composable
fun ServicesScreen(viewModel: ServicesViewModel) {

    val state = viewModel.uiState.collectAsState().value
    val workerId = "worker_demo_001"

    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadServices(workerId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ── Formulario ────────────────────────────────────────
        Text("New Service", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Cost") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val costDouble = cost.toDoubleOrNull() ?: 0.0
                viewModel.createService(
                    workerId,
                    Service(
                        name = name,
                        cost = costDouble,
                        duration = duration,
                        description = description
                    )
                )
                // Limpiar campos
                name = ""
                cost = ""
                duration = ""
                description = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && cost.isNotBlank()
        ) {
            Text("Save Service")
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // ── Lista ─────────────────────────────────────────────
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            state.errorMessage != null -> {
                Text("Error: ${state.errorMessage}")
            }
            state.services.isEmpty() -> {
                Text("No services yet.")
            }
            else -> {
                Text("Services: ${state.services.size}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                state.services.forEach { service ->
                    Text("- ${service.name} ₡${service.cost} · ${service.duration}")
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}