package com.example.seviya.feature.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.seviya.app.FeaturePlaceholder
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.PaymentReceipt
import com.example.shared.presentation.WorkerPaymentDetail.WorkerPaymentDetailViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerPaymentDetailScreen(
    appointmentId: String,
    onBack: () -> Unit,
) {
    val viewModel: WorkerPaymentDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(appointmentId) {
        viewModel.loadPaymentDetail(appointmentId)
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
            },
        )
    } ?: FeaturePlaceholder(
        title = "Comprobante",
        subtitle = "No se encontró el pago.",
    )
}

@Composable
private fun WorkerPaymentDetailContent(
    appointment: Appointment,
    paymentReceipt: PaymentReceipt?,
    onBack: () -> Unit,
    onVerifyPayment: () -> Unit,
    onReportProblem: () -> Unit,
) {
    val showImagePreview = paymentReceipt?.imageUrl?.isNotBlank() == true
    var showFullImage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BrandBlue.copy(alpha = 0.07f),
                        AppBackgroundAlt,
                        AppBackgroundAlt,
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PaymentDetailHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 18.dp, bottom = 180.dp)
            ) {
                Text(
                    text = "Detalle del comprobante",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextBluePrimary,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Revisa la información de la cita y confirma si el pago fue recibido correctamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(18.dp))

                PaymentSummaryCard(
                    appointment = appointment,
                    paymentReceipt = paymentReceipt,
                )

                Spacer(modifier = Modifier.height(18.dp))

                ReceiptSection(
                    showImagePreview = showImagePreview,
                    paymentReceipt = paymentReceipt,
                    onImageClick = { showFullImage = true },
                )
            }
        }

        BottomActionsPanel(
            paymentReceipt = paymentReceipt,
            onVerifyPayment = onVerifyPayment,
            onReportProblem = onReportProblem,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (showFullImage && paymentReceipt?.imageUrl?.isNotBlank() == true) {
            Dialog(onDismissRequest = { showFullImage = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.92f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    KamelImage(
                        resource = asyncPainterResource(paymentReceipt.imageUrl),
                        contentDescription = "Comprobante ampliado",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSummaryCard(
    appointment: Appointment,
    paymentReceipt: PaymentReceipt?,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = White,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f),
        ),
        shadowElevation = 8.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    PaymentStatusChip(paymentReceipt = paymentReceipt)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Monto registrado",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = TextSecondary,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "₡${appointment.totalCost}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = TextBluePrimary,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandBlue.copy(alpha = 0.10f))
                        .border(
                            width = 1.dp,
                            color = BrandBlue.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = TablerIcons.CreditCard,
                        contentDescription = "Pago",
                        tint = BrandBlue,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f))

            Spacer(modifier = Modifier.height(16.dp))

            PaymentInfoRow(
                icon = TablerIcons.User,
                label = "Cliente",
                value = appointment.clientName,
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentInfoRow(
                icon = TablerIcons.CalendarEvent,
                label = "Fecha",
                value = formatAppointmentDate(appointment.serviceStartAt),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SoftBlueSurface,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = TablerIcons.InfoCircle,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(18.dp),
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Verifica la imagen del comprobante y confirma manualmente la recepción del pago.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextBluePrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentStatusChip(paymentReceipt: PaymentReceipt?) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = paymentStatusBackground(paymentReceipt),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(paymentStatusDot(paymentReceipt))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = paymentStatusText(paymentReceipt),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = paymentStatusTextColor(paymentReceipt),
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun PaymentInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BrandBlue.copy(alpha = 0.09f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(17.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextBluePrimary,
            )
        }
    }
}

@Composable
private fun ReceiptSection(
    showImagePreview: Boolean,
    paymentReceipt: PaymentReceipt?,
    onImageClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = White.copy(alpha = 0.96f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
        ),
        shadowElevation = 6.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandBlue.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = TablerIcons.FileText,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Vista previa del recibo",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextBluePrimary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (showImagePreview) {
                            "Toca la imagen para verla ampliada."
                        } else {
                            "El cliente aún no ha adjuntado un comprobante."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showImagePreview) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clickable { onImageClick() },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                    ),
                ) {
                    KamelImage(
                        resource = asyncPainterResource(paymentReceipt?.imageUrl.orEmpty()),
                        contentDescription = "Comprobante de pago",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SoftBlueSurface,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = TablerIcons.InfoCircle,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Asegúrate de revisar que el comprobante corresponda a esta cita.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextBluePrimary,
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                    ),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(BrandBlue.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = TablerIcons.FileText,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(28.dp),
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Sin comprobante adjunto",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextBluePrimary,
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Cuando el cliente suba una imagen, podrás revisarla aquí.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomActionsPanel(
    paymentReceipt: PaymentReceipt?,
    onVerifyPayment: () -> Unit,
    onReportProblem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = White,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
        ),
        shadowElevation = 18.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onVerifyPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                enabled = canVerifyPayment(paymentReceipt),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = TablerIcons.Check,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verificar pago",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }

            OutlinedButton(
                onClick = onReportProblem,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandRed),
                border = BorderStroke(2.dp, BrandRed.copy(alpha = 0.75f)),
                enabled = canReportProblem(paymentReceipt),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = TablerIcons.InfoCircle,
                        contentDescription = null,
                        tint = BrandRed,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Problema con pago",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentDetailHeader(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "payment_detail_header")

    val leftBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "left_badge_scale",
    )

    val rightBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "right_badge_scale",
    )

    val bubbleOffsetLarge by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bubble_offset_large",
    )

    val bubbleOffsetSmall by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bubble_offset_small",
    )

    val bubbleScaleLarge by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bubble_scale_large",
    )

    val bubbleScaleSmall by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bubble_scale_small",
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -260f,
        targetValue = 620f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_offset",
    )

    val entranceVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        entranceVisible.value = true
    }

    AnimatedVisibility(
        visible = entranceVisible.value,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    initialOffsetY = { -it / 3 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing),
                ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                .background(BrandBlue)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(140.dp)
                        .offset(x = shimmerOffset.dp)
                        .graphicsLayer {
                            rotationZ = -18f
                            alpha = 0.16f
                        }
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    White.copy(alpha = 0.45f),
                                    Color.Transparent,
                                )
                            )
                        )
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 42.dp)
                    .graphicsLayer {
                        scaleX = leftBadgeScale
                        scaleY = leftBadgeScale
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(
                        width = 1.dp,
                        color = White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val arrowFloat by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = -2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "arrow_float",
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer { translationY = arrowFloat }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.14f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = TablerIcons.ArrowLeft,
                        contentDescription = "Volver",
                        tint = White,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Servi",
                        color = White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Ya",
                        color = BrandRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            Text(
                text = "COMPROBANTE",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 42.dp)
                    .graphicsLayer {
                        scaleX = rightBadgeScale
                        scaleY = rightBadgeScale
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(
                        width = 1.dp,
                        color = White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .graphicsLayer {
                            translationY = bubbleOffsetLarge
                            scaleX = bubbleScaleLarge
                            scaleY = bubbleScaleLarge
                        }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.08f))
                )

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .align(Alignment.BottomStart)
                        .graphicsLayer {
                            translationY = bubbleOffsetSmall
                            scaleX = bubbleScaleSmall
                            scaleY = bubbleScaleSmall
                        }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.10f))
                )
            }
        }
    }
}

private fun formatAppointmentDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"

    val sections = raw.split("T")
    if (sections.size != 2) return raw

    val dateParts = sections[0].split("-")
    val timeParts = sections[1].split(":")

    if (dateParts.size < 3 || timeParts.size < 2) return raw

    val year = dateParts[0]
    val month = dateParts[1].toIntOrNull() ?: return raw
    val day = dateParts[2]

    val hour24 = timeParts[0].toIntOrNull() ?: return raw
    val minute = timeParts[1]

    val monthName = when (month) {
        1 -> "ene"
        2 -> "feb"
        3 -> "mar"
        4 -> "abr"
        5 -> "may"
        6 -> "jun"
        7 -> "jul"
        8 -> "ago"
        9 -> "sep"
        10 -> "oct"
        11 -> "nov"
        12 -> "dic"
        else -> return raw
    }

    val period = if (hour24 >= 12) "p. m." else "a. m."
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }

    return "$day $monthName $year · $hour12:$minute $period"
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