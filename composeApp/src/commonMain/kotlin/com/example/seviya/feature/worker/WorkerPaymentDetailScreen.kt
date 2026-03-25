package com.example.seviya.feature.worker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.seviya.app.FeaturePlaceholder
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt
import com.example.shared.presentation.WorkerPaymentDetail.WorkerPaymentDetailViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerPaymentDetailScreen(
    bookingId: String,
    onBack: () -> Unit
) {
    val viewModel: WorkerPaymentDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.loadPaymentDetail(bookingId)
    }

    LaunchedEffect(uiState.paymentVerified) {
        if (uiState.paymentVerified) {
            onBack()
        }
    }

    uiState.appointment?.let { appointment ->
        WorkerPaymentDetailContent(
            appointment = appointment,
            paymentReceipt = uiState.paymentReceipt,
            onBack = onBack,
            onVerifyPayment = {
                viewModel.verifyPayment()
                onBack()
            },
            onReportProblem = {
                viewModel.reportProblem()
                onBack()
            }
        )
    } ?: FeaturePlaceholder(
        title = "Comprobante",
        subtitle = "No se encontró el pago."
    )
}

@Composable
private fun WorkerPaymentDetailContent(
    appointment: Appointment,
    paymentReceipt: PaymentReceipt?,
    onBack: () -> Unit,
    onVerifyPayment: () -> Unit,
    onReportProblem: () -> Unit
) {
    val showImagePreview = paymentReceipt?.imageUrl?.isNotBlank() == true
    var showFullImage by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .background(BrandBlue)
            ) {
                Surface(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 24.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .clickable { onBack() },
                    shape = RoundedCornerShape(999.dp),
                    color = White.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Servi",
                                color = White,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = "Ya",
                                color = BrandRed,
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
                    color = White.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PAGO",
                            color = White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = paymentStatusBackground(paymentReceipt)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(paymentStatusDot(paymentReceipt))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = paymentStatusText(paymentReceipt),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = paymentStatusTextColor(paymentReceipt),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "₡${appointment.totalCost}",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrandBlue.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💳", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "👤", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cliente:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = appointment.clientName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📅", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fecha:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = appointment.serviceStartAt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Vista previa del recibo",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (showImagePreview) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .clickable {
                                showFullImage = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        KamelImage(
                            resource = asyncPainterResource(paymentReceipt?.imageUrl.orEmpty()),
                            contentDescription = "Comprobante de pago",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🧾", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Sin comprobante adjunto",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (showImagePreview) {
                    Text(
                        text = "Toca la imagen para verla ampliada",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(160.dp))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onVerifyPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                enabled = canVerifyPayment(paymentReceipt)
            ) {
                Text(
                    text = "Verificar pago",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            OutlinedButton(
                onClick = onReportProblem,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandRed
                ),
                border = BorderStroke(2.dp, BrandRed),
                enabled = canReportProblem(paymentReceipt)
            ) {
                Text(
                    text = "Problema con pago",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        if (showFullImage && paymentReceipt?.imageUrl?.isNotBlank() == true) {
            Dialog(
                onDismissRequest = { showFullImage = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.92f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    KamelImage(
                        resource = asyncPainterResource(paymentReceipt.imageUrl),
                        contentDescription = "Comprobante ampliado",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }
    }
}

private fun paymentStatusText(paymentReceipt: PaymentReceipt?): String {
    return when (paymentReceipt?.status?.uppercase()) {
        "APPROVED" -> "Aprobado"
        "REJECTED" -> "Con problema"
        else -> "Pendiente"
    }
}

private fun paymentStatusBackground(paymentReceipt: PaymentReceipt?): Color {
    return when (paymentReceipt?.status?.uppercase()) {
        "APPROVED" -> Color(0xFFE8F7EC)
        "REJECTED" -> Color(0xFFFCE9E9)
        else -> Color(0xFFFFF3CD)
    }
}

private fun paymentStatusDot(paymentReceipt: PaymentReceipt?): Color {
    return when (paymentReceipt?.status?.uppercase()) {
        "APPROVED" -> Color(0xFF16A34A)
        "REJECTED" -> Color(0xFFDC2626)
        else -> Color(0xFFF59E0B)
    }
}

private fun paymentStatusTextColor(paymentReceipt: PaymentReceipt?): Color {
    return when (paymentReceipt?.status?.uppercase()) {
        "APPROVED" -> Color(0xFF166534)
        "REJECTED" -> Color(0xFF991B1B)
        else -> Color(0xFF92400E)
    }
}

private fun canVerifyPayment(paymentReceipt: PaymentReceipt?): Boolean {
    return paymentReceipt?.id?.isNotBlank() == true &&
            paymentReceipt.status.equals("PENDING", ignoreCase = true)
}

private fun canReportProblem(paymentReceipt: PaymentReceipt?): Boolean {
    return paymentReceipt?.id?.isNotBlank() == true &&
            paymentReceipt.status.equals("PENDING", ignoreCase = true)
}