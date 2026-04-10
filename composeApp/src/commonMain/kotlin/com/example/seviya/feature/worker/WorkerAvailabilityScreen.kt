package com.example.seviya.feature.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.ErrorRedSoft
import com.example.seviya.core.designsystem.theme.ErrorText
import com.example.seviya.core.designsystem.theme.InactiveSoft
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.presentation.workerAvailability.ExceptionUiItem
import com.example.shared.presentation.workerAvailability.WorkerAvailabilityUiState
import com.example.shared.presentation.workerAvailability.WorkerAvailabilityViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.CalendarOff
import compose.icons.tablericons.Check
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Trash
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerAvailabilityScreen(workerId: String, onBack: () -> Unit) {
    val viewModel: WorkerAvailabilityViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(workerId) { viewModel.loadData(workerId) }

    WorkerAvailabilityContent(
        uiState = uiState,
        onBack = onBack,
        onToggleDay = { viewModel.toggleDay(it) },
        onUpdateDayTime = { key, s, e -> viewModel.updateDayTime(key, s, e) },
        onOpenExceptionDialog = { viewModel.openExceptionDialog() },
        onCloseExceptionDialog = { viewModel.closeExceptionDialog() },
        onUpdatePendingDate = { viewModel.updatePendingDate(it) },
        onUpdatePendingType = { viewModel.updatePendingType(it) },
        onUpdatePendingStart = { viewModel.updatePendingStart(it) },
        onUpdatePendingEnd = { viewModel.updatePendingEnd(it) },
        onConfirmException = { viewModel.confirmException(workerId) },
        onDeleteException = { viewModel.deleteException(workerId, it) },
        onSave = { viewModel.save(workerId) },
        onClearSaveSuccess = { viewModel.clearSaveSuccess() },
        onClearError = { viewModel.clearError() },
    )
}

@Composable
private fun WorkerAvailabilityContent(
    uiState: WorkerAvailabilityUiState,
    onBack: () -> Unit,
    onToggleDay: (String) -> Unit,
    onUpdateDayTime: (String, String, String) -> Unit,
    onOpenExceptionDialog: () -> Unit,
    onCloseExceptionDialog: () -> Unit,
    onUpdatePendingDate: (String) -> Unit,
    onUpdatePendingType: (Boolean) -> Unit,
    onUpdatePendingStart: (String) -> Unit,
    onUpdatePendingEnd: (String) -> Unit,
    onConfirmException: () -> Unit,
    onDeleteException: (String) -> Unit,
    onSave: () -> Unit,
    onClearSaveSuccess: () -> Unit,
    onClearError: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Disponibilidad guardada correctamente")
            onClearSaveSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    if (uiState.showExceptionDialog) {
        AddExceptionDialog(
            pendingDate = uiState.pendingDate,
            pendingIsUnavailable = uiState.pendingIsUnavailable,
            pendingStart = uiState.pendingStart,
            pendingEnd = uiState.pendingEnd,
            onDateChange = onUpdatePendingDate,
            onTypeChange = onUpdatePendingType,
            onStartChange = onUpdatePendingStart,
            onEndChange = onUpdatePendingEnd,
            onConfirm = onConfirmException,
            onDismiss = onCloseExceptionDialog,
        )
    }

    Scaffold(
        containerColor = AppBackgroundAlt,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data, containerColor = BrandBlue, contentColor = White)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            AvailabilityHeader(onBack = onBack)

            if (uiState.isLoading) {
                Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandBlue)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    // Reglas semanales
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Reglas semanales",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextBluePrimary,
                            )
                            Text(
                                text = "HORARIO BASE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                ),
                                color = BrandBlue,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoftBlueSurface)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }

                        uiState.days.forEach { day ->
                            DayCard(
                                label = day.label,
                                enabled = day.enabled,
                                startTime = day.startTime,
                                endTime = day.endTime,
                                onToggle = { onToggleDay(day.dayKey) },
                                onStartChange = { onUpdateDayTime(day.dayKey, it, day.endTime) },
                                onEndChange = { onUpdateDayTime(day.dayKey, day.startTime, it) },
                            )
                        }
                    }

                    // Excepciones
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Excepciones",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextBluePrimary,
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenExceptionDialog() }
                                    .background(SoftBlueSurface)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = TablerIcons.Plus,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(14.dp),
                                )
                                Text(
                                    text = "AGREGAR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                    ),
                                    color = BrandBlue,
                                )
                            }
                        }

                        if (uiState.exceptions.isEmpty()) {
                            Text(
                                text = "Sin excepciones registradas",
                                style = MaterialTheme.typography.bodySmall,
                                color = InactiveSoft,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                            )
                        } else {
                            uiState.exceptions.forEach { exc ->
                                ExceptionCard(
                                    exception = exc,
                                    onDelete = { onDeleteException(exc.date) },
                                )
                            }
                        }
                    }

                    // Guardar
                    Button(
                        onClick = onSave,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            disabledContainerColor = InactiveSoft.copy(alpha = 0.35f),
                            contentColor = White,
                            disabledContentColor = White.copy(alpha = 0.75f),
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                color = White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp),
                            )
                        } else {
                            Text(
                                "Guardar cambios",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = TablerIcons.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCard(
    label: String,
    enabled: Boolean,
    startTime: String,
    endTime: String,
    onToggle: () -> Unit,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) White else White.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 2.dp else 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (enabled) TextBluePrimary else InactiveSoft,
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = BrandBlue,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = BorderSoft,
                    ),
                )
            }

            AnimatedVisibility(
                visible = enabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TimeField(
                            modifier = Modifier.weight(1f),
                            label = "Desde",
                            value = startTime,
                            onValueChange = onStartChange,
                        )
                        TimeField(
                            modifier = Modifier.weight(1f),
                            label = "Hasta",
                            value = endTime,
                            onValueChange = onEndChange,
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !enabled,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = "No disponible",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = InactiveSoft,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun TimeField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            ),
            color = TextSecondary,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { raw ->
                val digits = raw.filter { it.isDigit() }
                val formatted = when {
                    digits.length >= 4 -> "${digits.take(2)}:${digits.drop(2).take(2)}"
                    digits.length >= 2 -> "${digits.take(2)}:${digits.drop(2)}"
                    else -> digits
                }
                onValueChange(formatted)
            },
            placeholder = {
                Text("HH:MM", style = MaterialTheme.typography.bodyMedium, color = InactiveSoft)
            },
            leadingIcon = {
                Icon(
                    imageVector = TablerIcons.Clock,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(18.dp),
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandBlue,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandBlue,
                unfocusedBorderColor = BorderSoft,
                cursorColor = BrandBlue,
            ),
        )
    }
}

@Composable
private fun ExceptionCard(
    exception: ExceptionUiItem,
    onDelete: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (exception.isUnavailable) ErrorRedSoft else SoftBlueSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (exception.isUnavailable) TablerIcons.CalendarOff else TablerIcons.Calendar,
                    contentDescription = null,
                    tint = if (exception.isUnavailable) ErrorText else BrandBlue,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exception.displayDate,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextBluePrimary,
                )
                Text(
                    text = if (exception.isUnavailable) "No disponible"
                    else "${exception.startTime} – ${exception.endTime}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                    ),
                    color = if (exception.isUnavailable) ErrorText else BrandBlue,
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onDelete() }
                    .background(ErrorRedSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TablerIcons.Trash,
                    contentDescription = "Eliminar",
                    tint = ErrorText,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun AddExceptionDialog(
    pendingDate: String,
    pendingIsUnavailable: Boolean,
    pendingStart: String,
    pendingEnd: String,
    onDateChange: (String) -> Unit,
    onTypeChange: (Boolean) -> Unit,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Nueva excepción",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextBluePrimary,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Fecha
                Column {
                    Text(
                        "FECHA (AAAA-MM-DD)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = pendingDate,
                        onValueChange = onDateChange,
                        placeholder = {
                            Text("2025-10-15", color = InactiveSoft)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderSoft,
                            cursorColor = BrandBlue,
                        ),
                    )
                }

                // Tipo
                Column {
                    Text(
                        "TIPO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TypeChip(
                            modifier = Modifier.weight(1f),
                            label = "No disponible",
                            selected = pendingIsUnavailable,
                            selectedColor = BrandRed,
                            onClick = { onTypeChange(true) },
                        )
                        TypeChip(
                            modifier = Modifier.weight(1f),
                            label = "Horario especial",
                            selected = !pendingIsUnavailable,
                            selectedColor = BrandBlue,
                            onClick = { onTypeChange(false) },
                        )
                    }
                }

                // Horas (solo si horario especial)
                AnimatedVisibility(visible = !pendingIsUnavailable) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TimeField(
                            modifier = Modifier.weight(1f),
                            label = "Desde",
                            value = pendingStart,
                            onValueChange = onStartChange,
                        )
                        TimeField(
                            modifier = Modifier.weight(1f),
                            label = "Hasta",
                            value = pendingEnd,
                            onValueChange = onEndChange,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = pendingDate.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
            ) {
                Text("Agregar", color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
    )
}

@Composable
private fun TypeChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.5.dp,
                color = if (selected) selectedColor else BorderSoft,
                shape = RoundedCornerShape(10.dp),
            )
            .background(if (selected) selectedColor.copy(alpha = 0.08f) else White)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (selected) selectedColor else InactiveSoft,
        )
    }
}

@Composable
private fun AvailabilityHeader(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "availability_header")

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -260f,
        targetValue = 620f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer",
    )

    val badgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "badge_scale",
    )

    val entranceVisible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entranceVisible.value = true }

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
            // Shimmer
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

            // Back + Logo
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 42.dp)
                    .graphicsLayer { scaleX = badgeScale; scaleY = badgeScale }
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
                Box(
                    modifier = Modifier
                        .size(28.dp)
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
                    Text("Servi", color = White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Ya", color = BrandRed, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Badge derecho
            Text(
                text = "DISPONIBILIDAD",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 42.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(
                        width = 1.dp,
                        color = White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
