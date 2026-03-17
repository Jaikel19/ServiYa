package com.example.seviya.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.UI.BookingRequestCard
import com.example.shared.domain.entity.Booking
import com.example.shared.presentation.WorkerRequest.WorkerRequestsUiState

enum class RequestFilter { PENDING, PAYMENT_PENDING }

@Composable
fun WorkerRequestsScreen(
    uiState: WorkerRequestsUiState,
    onAccept: (Booking) -> Unit,
    onReject: (Booking) -> Unit,
    onConfirm: (Booking) -> Unit,
    onCancel: (Booking) -> Unit,
    onOpenRequestDetail: (bookingId: String) -> Unit = {},
    onOpenPaymentDetail: (bookingId: String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(RequestFilter.PENDING) }

    val filteredRequests = uiState.requests.filter {
        when (selectedFilter) {
            RequestFilter.PENDING -> it.status == "pending"
            RequestFilter.PAYMENT_PENDING -> it.status == "payment_pending"
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Surface(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 24.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Servi",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = "Ya",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 28.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SOLICITUDES",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
            }
        }

        // Título + filtros
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Solicitudes Pendientes",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "VISTA RÁPIDA POR PROXIMIDAD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = selectedFilter == RequestFilter.PENDING,
                        onClick = { selectedFilter = RequestFilter.PENDING },
                        label = {
                            Text(
                                text = "Pendientes",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == RequestFilter.PENDING,
                            selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            borderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    FilterChip(
                        selected = selectedFilter == RequestFilter.PAYMENT_PENDING,
                        onClick = { selectedFilter = RequestFilter.PAYMENT_PENDING },
                        label = {
                            Text(
                                text = "Pendientes de pago",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == RequestFilter.PAYMENT_PENDING,
                            selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            borderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // Content
        when {
            uiState.isLoading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            uiState.errorMessage != null -> {
                item {
                    Text(
                        "Error: ${uiState.errorMessage}",
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.requests.isEmpty() -> {
                item {
                    Text(
                        "No hay solicitudes disponibles.",
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            filteredRequests.isEmpty() -> {
                item {
                    Text(
                        text = when (selectedFilter) {
                            RequestFilter.PENDING -> "No hay solicitudes pendientes."
                            RequestFilter.PAYMENT_PENDING -> "No hay solicitudes pendientes de pago."
                        },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                items(filteredRequests) { booking ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        BookingRequestCard(
                            booking = booking,
                            onAccept = {
                                when (selectedFilter) {
                                    RequestFilter.PENDING -> onAccept(booking)
                                    RequestFilter.PAYMENT_PENDING -> onConfirm(booking)
                                }
                            },
                            onReject = {
                                when (selectedFilter) {
                                    RequestFilter.PENDING -> onReject(booking)
                                    RequestFilter.PAYMENT_PENDING -> onCancel(booking)
                                }
                            },
                            onCardClick = {
                                when (selectedFilter) {
                                    RequestFilter.PENDING -> onOpenRequestDetail(booking.id)
                                    RequestFilter.PAYMENT_PENDING -> onOpenPaymentDetail(booking.id)
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}