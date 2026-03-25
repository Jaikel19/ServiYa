package com.example.seviya.feature.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.presentation.workerStartAppointmentOtp.WorkerStartAppointmentOtpUiState

@Composable
fun WorkerStartAppointmentOtpScreen(
    uiState: WorkerStartAppointmentOtpUiState,
    onBack: () -> Unit = {},
    onOtpChange: (String) -> Unit = {},
    onStartAppointment: () -> Unit = {}
) {
    val appointment = uiState.appointment

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5F8))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Confirmar Inicio",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold
            )
        )

        appointment?.let {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "CITA CONFIRMADA",
                        color = Color(0xFF0A4DB3),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = it.clientName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Text(
                        text = it.location.reference,
                        color = Color(0xFF6B7A90)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresar Código OTP",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Text(
            text = "Solicita al cliente el código de 6 dígitos para iniciar el servicio.",
            color = Color(0xFF6B7A90)
        )

        OutlinedTextField(
            value = uiState.otpInput,
            onValueChange = onOtpChange,
            label = { Text("OTP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        uiState.errorMessage?.let {
            Text(
                text = it,
                color = Color(0xFFE54848),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = onStartAppointment,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Iniciar cita")
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}