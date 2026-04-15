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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.*

import com.example.shared.presentation.workerZones.WorkerZonesUiState
import com.example.shared.presentation.workerZones.WorkerZonesViewModel
import com.example.shared.presentation.workerZones.ZoneItem
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Check
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Trash
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerZonesScreen(workerId: String, onBack: () -> Unit) {
    val viewModel: WorkerZonesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(workerId) { viewModel.loadData(workerId) }

    WorkerZonesContent(
        uiState = uiState,
        onBack = onBack,
        onToggleZone = { viewModel.toggleZone(it) },
        onToggleBlocked = { viewModel.toggleBlocked(it) },
        onClearError = { viewModel.clearError() },
    )
}

@Composable
private fun WorkerZonesContent(
    uiState: WorkerZonesUiState,
    onBack: () -> Unit,
    onToggleZone: (ZoneItem) -> Unit,
    onToggleBlocked: (String) -> Unit,
    onClearError: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            WorkerZonesHeader(onBack = onBack)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                item {
                    ZonesSummaryCard(
                        selectedCount = uiState.selectedCount,
                        activeCount = uiState.activeZoneIds.size,
                        blockedCount = uiState.blockedZoneIds.size,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = BrandBlue, strokeWidth = 3.dp)
                        }
                    }
                } else {
                    WorkerZonesUiState.PROVINCES.forEach { province ->
                        val zonesForProvince = WorkerZonesUiState.ALL_ZONES.filter { it.province == province }
                        val selectedInProvince = uiState.selectedCountByProvince(province)

                        item(key = "header-$province") {
                            ProvinceHeader(
                                province = province,
                                selectedCount = selectedInProvince,
                                total = zonesForProvince.size,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        items(zonesForProvince, key = { it.id }) { zone ->
                            val isServed = zone.id in uiState.servedZoneIds
                            val isBlocked = zone.id in uiState.blockedZoneIds
                            ZoneRow(
                                zone = zone,
                                isServed = isServed,
                                isBlocked = isBlocked,
                                isSaving = uiState.isSaving,
                                onToggle = { onToggleZone(zone) },
                                onToggleBlocked = { onToggleBlocked(zone.id) },
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item(key = "spacer-$province") {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerZonesHeader(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "worker_zones_header")

    val leftBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.035f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "left_badge_scale",
    )
    val bubbleOffsetLarge by infiniteTransition.animateFloat(
        initialValue = -4f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bubble_offset",
    )
    val bubbleScaleLarge by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bubble_scale",
    )
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -260f, targetValue = 620f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer_offset",
    )
    val arrowFloat by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -2f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "arrow_float",
    )

    val entranceVisible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entranceVisible.value = true }

    AnimatedVisibility(
        visible = entranceVisible.value,
        enter = fadeIn(tween(500)) + slideInVertically(
            initialOffsetY = { -it / 3 },
            animationSpec = tween(600, easing = FastOutSlowInEasing),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                .background(BrandBlue),
        ) {
            // Shimmer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(140.dp)
                        .offset(x = shimmerOffset.dp)
                        .graphicsLayer { rotationZ = -18f; alpha = 0.16f }
                        .background(
                            Brush.linearGradient(
                                listOf(Color.Transparent, White.copy(alpha = 0.45f), Color.Transparent),
                            )
                        ),
                )
            }

            // Back button + logo
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 42.dp)
                    .graphicsLayer { scaleX = leftBadgeScale; scaleY = leftBadgeScale }
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(1.dp, White.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer { translationY = arrowFloat }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.14f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(TablerIcons.ArrowLeft, contentDescription = "Volver", tint = White, modifier = Modifier.size(16.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Servi", color = White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Ya", color = BrandRed, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Badge right
            Text(
                text = "CONFIGURACIÓN",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 42.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(1.dp, White.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            // Decorative bubble
            Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .graphicsLayer { translationY = bubbleOffsetLarge; scaleX = bubbleScaleLarge; scaleY = bubbleScaleLarge }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.08f)),
                )
            }
        }
    }
}

@Composable
private fun ZonesSummaryCard(selectedCount: Int, activeCount: Int, blockedCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BrandBlue)
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "ESTADO DE COBERTURA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = White.copy(alpha = 0.8f),
            )
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("$selectedCount", fontSize = 40.sp, fontWeight = FontWeight.Black, color = White)
                Text(
                    text = if (selectedCount == 1) "zona configurada" else "zonas configuradas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusBadge(label = "Activas", count = activeCount, color = Color(0xFF4ADE80))
                StatusBadge(label = "Bloqueadas", count = blockedCount, color = Color(0xFFFBBF24))
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(White.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text("$count $label", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
    }
}

@Composable
private fun ProvinceHeader(province: String, selectedCount: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(0.dp),
            )
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(TablerIcons.MapPin, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(18.dp))
            Text(
                text = province,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextBluePrimary,
            )
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (selectedCount > 0) BrandBlue.copy(alpha = 0.12f) else InactiveSoft.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = if (selectedCount > 0) "$selectedCount/$total seleccionadas" else "0 seleccionadas",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedCount > 0) BrandBlue else BlueGrayText,
                letterSpacing = 0.5.sp,
            )
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSoft))
}

@Composable
private fun ZoneRow(
    zone: ZoneItem,
    isServed: Boolean,
    isBlocked: Boolean,
    isSaving: Boolean,
    onToggle: () -> Unit,
    onToggleBlocked: () -> Unit,
) {
    val borderColor = when {
        isBlocked -> Color(0xFFFBBF24).copy(alpha = 0.5f)
        isServed -> BrandBlue.copy(alpha = 0.25f)
        else -> BorderSoft
    }
    val iconTint = when {
        isBlocked -> Color(0xFFF59E0B)
        isServed -> BrandBlue
        else -> InactiveSoft
    }
    val iconBg = when {
        isBlocked -> Color(0xFFFEF3C7)
        isServed -> BrandBlue.copy(alpha = 0.1f)
        else -> InactiveSoft.copy(alpha = 0.1f)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(TablerIcons.MapPin, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = zone.canton,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isServed) TextBluePrimary else TextSecondary,
                    modifier = Modifier.weight(1f),
                )

                Checkbox(
                    checked = isServed,
                    onCheckedChange = { onToggle() },
                    enabled = !isSaving,
                    colors = CheckboxDefaults.colors(
                        checkedColor = BrandBlue,
                        uncheckedColor = InactiveSoft,
                        checkmarkColor = White,
                    ),
                )
            }

            // Botón de bloqueo — solo visible si la zona está seleccionada
            if (isServed) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onToggleBlocked,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isBlocked) Color(0xFFFBBF24) else BorderSoft,
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                ) {
                    Icon(
                        imageVector = if (isBlocked) TablerIcons.Check else TablerIcons.Trash,
                        contentDescription = null,
                        tint = if (isBlocked) Color(0xFFF59E0B) else BrandRed,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBlocked) "Desbloquear zona" else "Bloquear zona",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBlocked) Color(0xFFF59E0B) else BrandRed,
                    )
                }
            }
        }
    }
}
