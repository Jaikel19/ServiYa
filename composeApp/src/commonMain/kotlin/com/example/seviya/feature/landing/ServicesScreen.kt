package com.example.seviya.feature.landing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.domain.entity.Service
import com.example.shared.presentation.services.ServicesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ServicesScreen() {
    val viewModel: ServicesViewModel = koinViewModel()
    ServicesContent(viewModel = viewModel)
}

@Composable
private fun ServicesContent(viewModel: ServicesViewModel) {

    val state = viewModel.uiState.collectAsState().value
    val workerId = "worker_demo_001"

    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var editingService by remember { mutableStateOf<Service?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadServices(workerId)
    }

    // Cuando se selecciona un servicio para editar, llena los campos
    LaunchedEffect(editingService) {
        editingService?.let {
            name = it.name
            cost = it.cost.toString()
            duration = it.duration
            description = it.description
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Formulario ────────────────────────────────────────
        Text(
            text = if (editingService != null) "Edit Service" else "New Service",
            style = MaterialTheme.typography.titleMedium
        )
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Botón cancelar edición
            if (editingService != null) {
                OutlinedButton(
                    onClick = {
                        editingService = null
                        name = ""
                        cost = ""
                        duration = ""
                        description = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }

            // Botón guardar / actualizar
            Button(
                onClick = {
                    val costDouble = cost.toDoubleOrNull() ?: 0.0
                    if (editingService != null) {
                        viewModel.updateService(
                            workerId,
                            editingService!!.copy(
                                name = name,
                                cost = costDouble,
                                duration = duration,
                                description = description
                            )
                        )
                    } else {
                        viewModel.createService(
                            workerId,
                            Service(
                                name = name,
                                cost = costDouble,
                                duration = duration,
                                description = description
                            )
                        )
                    }
                    editingService = null
                    name = ""
                    cost = ""
                    duration = ""
                    description = ""
                },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank() && cost.isNotBlank()
            ) {
                Text(if (editingService != null) "Update" else "Save")
            }
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(service.name, style = MaterialTheme.typography.bodyLarge)
                            Text("₡${service.cost} · ${service.duration}")
                            if (service.description.isNotBlank()) {
                                Text(service.description, style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Editar
                                OutlinedButton(
                                    onClick = { editingService = service },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Edit")
                                }
                                // Eliminar
                                Button(
                                    onClick = { viewModel.deleteService(workerId, service.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}