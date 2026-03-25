package com.example.seviya.feature.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.presentation.clientToWorkerReview.ClientToWorkerReviewViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientToWorkerReviewScreen(
    appointmentId: String,
    onBack: () -> Unit
) {
    val viewModel: ClientToWorkerReviewViewModel = koinViewModel()

    LaunchedEffect(appointmentId) {
        viewModel.loadAppointment(appointmentId)
    }

    ClientToWorkerReviewContent(
        viewModel = viewModel,
        onBack = onBack
    )
}

@Composable
private fun ClientToWorkerReviewContent(
    viewModel: ClientToWorkerReviewViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Calificar trabajador", style = MaterialTheme.typography.headlineMedium)

        // RATING
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { star ->
                Button(
                    onClick = { viewModel.onRatingChanged(star) }
                ) {
                    Text(if (star <= uiState.rating) "★" else "☆")
                }
            }
        }

        //COMENTARIO
        OutlinedTextField(
            value = uiState.comment,
            onValueChange = { viewModel.onCommentChanged(it) },
            label = { Text("Comentario (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        // BOTÓN
        Button(
            onClick = { viewModel.submitReview() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSubmitting
        ) {
            Text("Enviar reseña")
        }

        // ERROR
        uiState.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        // SUCCESS
        if (uiState.submitSuccess) {
            Text("Reseña enviada correctamente")
            LaunchedEffect(Unit) {
                onBack()
            }
        }
    }
}