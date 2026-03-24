package com.example.seviya.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.theme.BrandBlue
import com.example.seviya.theme.BrandRed
import com.example.seviya.theme.White
import com.example.shared.presentation.clientMap.ClientMapViewModel
import com.example.shared.presentation.clientMap.WorkerMapMarker
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronDown
import compose.icons.tablericons.ChevronUp
import compose.icons.tablericons.Clock
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Search
import compose.icons.tablericons.Star
import compose.icons.tablericons.Tag
import compose.icons.tablericons.User
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
actual fun ClientMapScreen(
    clientId: String,
    viewModel: ClientMapViewModel,
    onWorkerClick: (workerId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(clientId) {
        viewModel.loadMap(clientId)
    }

    val defaultLatLng = LatLng(9.9281, -84.0907)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            uiState.clientAddress?.let {
                LatLng(it.latitude, it.longitude)
            } ?: defaultLatLng,
            12f
        )
    }

    LaunchedEffect(uiState.clientAddress) {
        uiState.clientAddress?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.latitude, it.longitude),
                12f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            uiState.clientAddress?.let { address ->
                Marker(
                    state = MarkerState(LatLng(address.latitude, address.longitude)),
                    title = address.alias.ifBlank { "Tu ubicación" }
                )
            }

            uiState.filteredMarkers.forEach { marker ->
                Marker(
                    state = MarkerState(
                        LatLng(marker.workZone.latitude, marker.workZone.longitude)
                    ),
                    title = marker.user.name,
                    snippet = marker.user.categories.firstOrNull()?.name ?: "",
                    onClick = {
                        viewModel.selectMarker(marker)
                        false
                    }
                )
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
            // Logo + MAPA pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Servi",
                        color = White,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "Ya",
                        color = BrandRed,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = White.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MAPA",
                            color = White,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de búsqueda por nombre
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = TablerIcons.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (uiState.searchQuery.isEmpty()) {
                                Text(
                                    text = "Buscar trabajador por nombre...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Gray
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fila: búsqueda por categoría + control de estrellas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Búsqueda por categoría
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = TablerIcons.Tag,
                            contentDescription = "Categoria",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = uiState.categoryQuery,
                            onValueChange = { viewModel.onCategoryQueryChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (uiState.categoryQuery.isEmpty()) {
                                    Text(
                                        text = "Categoría...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.Gray
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                // Control de estrellas
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = White.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = TablerIcons.ChevronDown,
                            contentDescription = "Bajar estrellas",
                            tint = White,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    val current = uiState.minStars?.toInt() ?: 1
                                    if (current <= 1) {
                                        viewModel.onMinStarsSelected(null)
                                    } else {
                                        viewModel.onMinStarsSelected((current - 1).toDouble())
                                    }
                                }
                        )

                        Icon(
                            imageVector = TablerIcons.Star,
                            contentDescription = "Estrellas",
                            tint = White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${uiState.minStars?.toInt() ?: "-"}",
                            color = White,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Icon(
                            imageVector = TablerIcons.ChevronUp,
                            contentDescription = "Subir estrellas",
                            tint = White,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    val current = uiState.minStars?.toInt() ?: 0
                                    if (current >= 5) {
                                        viewModel.onMinStarsSelected(5.0)
                                    } else {
                                        viewModel.onMinStarsSelected((current + 1).toDouble())
                                    }
                                }
                        )
                    }
                }
            }
        }

        // Botón centrar
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        uiState.clientAddress?.let {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                LatLng(it.latitude, it.longitude), 14f
                            )
                        }
                    },
                color = White,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = TablerIcons.MapPin,
                        contentDescription = "Centrar",
                        tint = BrandBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
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

        // Popup
        uiState.selectedMarker?.let { marker ->
            WorkerPopup(
                marker = marker,
                onDismiss = { viewModel.clearSelectedMarker() },
                onViewProfile = { onWorkerClick(marker.user.uid) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun WorkerPopup(
    marker: WorkerMapMarker,
    onDismiss: () -> Unit,
    onViewProfile: () -> Unit,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.User,
                        contentDescription = "Trabajador",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = marker.user.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        marker.user.stars?.let { stars ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF8E1)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = TablerIcons.Star,
                                        contentDescription = null,
                                        tint = Color(0xFF92400E),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stars.toString(),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = marker.user.categories.firstOrNull()?.name ?: "Trabajador",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandBlue.copy(alpha = 0.10f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = TablerIcons.MapPin,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = marker.workZone.district,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BrandBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        marker.user.travelTime?.let { time ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = TablerIcons.Clock,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "~${time} min",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onViewProfile,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text(
                        text = "Ver perfil",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.height(48.dp),
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
}