package com.example.seviya.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.theme.AppBackgroundAlt
import com.example.seviya.theme.BorderSoft
import com.example.seviya.theme.BrandBlue
import com.example.seviya.theme.BrandBlueDeep
import com.example.seviya.theme.BrandRed
import com.example.seviya.theme.InactiveSoft
import com.example.seviya.theme.SoftBlueSurface
import com.example.seviya.theme.TextBluePrimary
import com.example.seviya.theme.TextSecondary
import com.example.seviya.theme.White
import com.example.shared.presentation.workerTravelTime.WorkerTravelTimeUiState
import com.example.shared.presentation.workerTravelTime.WorkerTravelTimeViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Check
import compose.icons.tablericons.ChevronLeft
import compose.icons.tablericons.Clock
import compose.icons.tablericons.InfoCircle

@Composable
fun TravelTimeConfigRoute(
    workerId: String,
    viewModel: WorkerTravelTimeViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(workerId) {
        viewModel.loadData(workerId)
    }

    TravelTimeConfigScreen(
        uiState = uiState,
        onBack = onBack,
        onMinutesChange = { viewModel.updateMinutesText(it) },
        onSave = { viewModel.save(workerId) },
        onClearSaveSuccess = { viewModel.clearSaveSuccess() }
    )
}

@Composable
fun TravelTimeConfigScreen(
    uiState: WorkerTravelTimeUiState,
    onBack: () -> Unit,
    onMinutesChange: (String) -> Unit,
    onSave: () -> Unit,
    onClearSaveSuccess: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Tiempo de traslado guardado correctamente")
            onClearSaveSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        containerColor = AppBackgroundAlt,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data, containerColor = BrandBlue, contentColor = White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            TravelTimeHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = "Configurar Tiempo de Traslado",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextBluePrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Ajusta el tiempo de margen para tus desplazamientos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "TIEMPO DE TRASLADO (MINUTOS)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            ),
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.minutesText,
                            onValueChange = onMinutesChange,
                            placeholder = {
                                Text(
                                    "Ej. 30",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = InactiveSoft
                                )
                            },
                            trailingIcon = {
                                Text(
                                    "min",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextSecondary,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(16.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandBlue
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlue,
                                unfocusedBorderColor = BorderSoft,
                                cursorColor = BrandBlue
                            )
                        )

                        if (!uiState.isValid && uiState.minutesText.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Ingresa un valor entre 0 y 600.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandRed
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        InfoNote(text = "Este tiempo se usará automáticamente para bloquear tu agenda entre servicios.")

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = onSave,
                            enabled = uiState.isValid && !uiState.isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandBlue,
                                disabledContainerColor = InactiveSoft.copy(alpha = 0.35f),
                                contentColor = White,
                                disabledContentColor = White.copy(alpha = 0.75f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    "Guardar Cambios",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = TablerIcons.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                AgendaPreviewCard(travelMinutes = uiState.minutesValue ?: 30)
            }
        }
    }
}

@Composable
private fun TravelTimeHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF003D96), Color(0xFF0052C2))
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
            .height(56.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = TablerIcons.ChevronLeft,
                contentDescription = "Volver",
                tint = White,
                modifier = Modifier.size(22.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = White,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    ) { append("Servi") }
                    withStyle(
                        SpanStyle(
                            color = BrandRed,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    ) { append("Ya") }
                },
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
            )
        }

        Text(
            text = "Configuración",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            ),
            color = White.copy(alpha = 0.70f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )
    }
}

@Composable
private fun InfoNote(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SoftBlueSurface)
            .blueLeftBorder(BrandBlue, 4.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = TablerIcons.InfoCircle,
            contentDescription = null,
            tint = BrandBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = TextBluePrimary
        )
    }
}

@Composable
private fun AgendaPreviewCard(travelMinutes: Int) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.70f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = TablerIcons.Clock,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Vista previa en agenda",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextBluePrimary
                )
            }

            Spacer(Modifier.height(14.dp))

            val endTotalMinutes = 11 * 60 + travelMinutes
            val endHour = endTotalMinutes / 60
            val endMin = endTotalMinutes % 60
            val endTime = "$endHour:${endMin.toString().padStart(2, '0')}"

            AgendaRow(time = "10:00", label = "Servicio: Limpieza", isTravel = false)
            Spacer(Modifier.height(6.dp))
            AgendaRow(time = "11:00", label = "Traslado ($travelMinutes min)", isTravel = true)
            Spacer(Modifier.height(6.dp))
            AgendaRow(time = endTime, label = "Servicio: Jardinería", isTravel = false)
        }
    }
}

@Composable
private fun AgendaRow(time: String, label: String, isTravel: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isTravel) BrandBlue else TextSecondary,
            modifier = Modifier.width(44.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(if (isTravel) 28.dp else 40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isTravel) BrandBlue.copy(alpha = 0.10f) else White
                )
                .then(
                    if (isTravel) Modifier else Modifier.background(White)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isTravel) FontWeight.Bold else FontWeight.SemiBold,
                    letterSpacing = if (isTravel) 0.5.sp else 0.sp
                ),
                color = if (isTravel) BrandBlue else TextBluePrimary,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

private fun Modifier.blueLeftBorder(color: Color, width: Dp): Modifier =
    this.drawBehind {
        drawRect(
            color = color,
            topLeft = Offset(0f, 0f),
            size = Size(width.toPx(), size.height)
        )
    }
