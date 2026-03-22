package com.example.seviya.UI

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.theme.BrandBlue
import com.example.seviya.theme.BrandRed
import com.example.seviya.theme.White
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.workerDailyAppointments.DailyView
import com.example.shared.presentation.workerDailyAppointments.WorkerDailyAppointmentsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
actual fun WorkerDailyAppointmentsScreen(
    workerId: String,
    viewModel: WorkerDailyAppointmentsViewModel,
    onBack: () -> Unit,
    onOpenMaps: (latitude: Double, longitude: Double, clientName: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

    LaunchedEffect(workerId) {
        viewModel.loadAppointments(workerId)
    }

    val defaultLatLng = LatLng(9.9281, -84.0907)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 11f)
    }

    // Centrar camara en primera cita
    LaunchedEffect(uiState.appointments) {
        val first = uiState.appointments.firstOrNull()
        first?.let {
            if (it.location.latitude != 0.0 && it.location.longitude != 0.0) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    LatLng(it.location.latitude, it.location.longitude),
                    12f
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            uiState.appointments.forEachIndexed { index, appointment ->
                if (appointment.location.latitude != 0.0 && appointment.location.longitude != 0.0) {
                    Marker(
                        state = MarkerState(
                            LatLng(appointment.location.latitude, appointment.location.longitude)
                        ),
                        title = appointment.clientName,
                        snippet = appointment.services.firstOrNull()?.name ?: "",
                        onClick = {
                            selectedAppointment = appointment
                            false
                        }
                    )
                }
            }
        }

        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandBlue)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo + back
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { onBack() },
                        shape = RoundedCornerShape(999.dp),
                        color = White.copy(alpha = 0.13f),
                        border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Servi",
                                    color = White,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Text(
                                    text = "Ya",
                                    color = BrandRed,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                        }
                    }
                }

                // Toggle Lista/Mapa
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = White.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { viewModel.onViewChanged(DailyView.LIST) },
                            shape = RoundedCornerShape(999.dp),
                            color = if (uiState.currentView == DailyView.LIST)
                                White else Color.Transparent
                        ) {
                            Text(
                                text = "Lista",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.currentView == DailyView.LIST)
                                        BrandBlue else White
                                )
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { viewModel.onViewChanged(DailyView.MAP) },
                            shape = RoundedCornerShape(999.dp),
                            color = if (uiState.currentView == DailyView.MAP)
                                White else Color.Transparent
                        ) {
                            Text(
                                text = "Mapa",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.currentView == DailyView.MAP)
                                        BrandBlue else White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contador de citas
            Text(
                text = "${uiState.appointments.size} citas hoy",
                color = White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // Botón centrar
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable {
                    val first = uiState.appointments.firstOrNull()
                    first?.let {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(it.location.latitude, it.location.longitude), 14f
                        )
                    }
                },
            color = White,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "📍", fontSize = 20.sp)
            }
        }

        // Loading
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = White)
            }
        }

        // Popup de cita seleccionada
        selectedAppointment?.let { appointment ->
            AppointmentPopup(
                appointment = appointment,
                onDismiss = { selectedAppointment = null },
                onOpenGoogleMaps = {
                    val uri = Uri.parse(
                        "https://www.google.com/maps/dir/?api=1&destination=${appointment.location.latitude},${appointment.location.longitude}"
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                onOpenWaze = {
                    val uri = Uri.parse(
                        "https://waze.com/ul?ll=${appointment.location.latitude},${appointment.location.longitude}&navigate=yes"
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun AppointmentPopup(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onOpenGoogleMaps: () -> Unit,
    onOpenWaze: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info de la cita
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.clientName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = appointment.services.firstOrNull()?.name ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandBlue.copy(alpha = 0.10f)
                    ) {
                        Text(
                            text = "📍 ${appointment.location.district}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BrandBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF8E1)
                ) {
                    Text(
                        text = appointment.serviceStartAt.takeLast(5),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de navegación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onOpenGoogleMaps,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3367D6)
                    )
                ) {
                    Text(
                        text = "Google Maps",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onOpenWaze,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF33CCFF)
                    )
                ) {
                    Text(
                        text = "Waze",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    text = "Cerrar",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}