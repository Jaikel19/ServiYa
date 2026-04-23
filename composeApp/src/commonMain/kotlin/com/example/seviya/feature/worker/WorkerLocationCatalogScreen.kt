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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.*
import com.example.seviya.feature.client.CurrentLocationButton
import com.example.shared.data.local.costaRicaProvinces
import com.example.shared.domain.entity.WorkZone
import com.example.shared.domain.entity.CostaRicaCanton
import com.example.shared.domain.entity.CostaRicaDistrict
import com.example.shared.domain.entity.CostaRicaProvince
import com.example.shared.presentation.workerLocationCatalog.WorkerLocationCatalogViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun WorkerLocationCatalogScreen(workerId: String, onBack: () -> Unit) {
    val viewModel: WorkerLocationCatalogViewModel = koinViewModel()
    WorkerLocationCatalogContent(workerId = workerId, viewModel = viewModel, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkerLocationCatalogContent(
    workerId: String,
    viewModel: WorkerLocationCatalogViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var showSheet by remember { mutableStateOf(false) }
    var editingLocation by remember { mutableStateOf<WorkZone?>(null) }

    LaunchedEffect(workerId) { viewModel.loadLocations(workerId) }

    Scaffold(containerColor = White) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(White)) {
            Column(modifier = Modifier.fillMaxSize().background(White)) {
                WorkerLocationCatalogHeader(onBack = onBack)

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = BrandBlue)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(innerPadding).background(White),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 100.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        val isEmpty = uiState.locations.isEmpty() && uiState.blockedZones.isEmpty()
                        if (isEmpty) {
                            item { WorkerEmptyLocationsState() }
                        } else {
                            // ── Mis Ubicaciones ───────────────────────
                            if (uiState.locations.isNotEmpty()) {
                                item {
                                    CatalogSectionHeader(
                                        title = "MIS UBICACIONES",
                                        count = uiState.locations.size,
                                        color = BrandBlue,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                items(uiState.locations) { location ->
                                    WorkerLocationCard(
                                        location = location,
                                        onEdit = {
                                            editingLocation = location
                                            showSheet = true
                                        },
                                        onDelete = { viewModel.deleteLocation(location.id) },
                                        onSetDefault = { viewModel.setDefaultLocation(location.id) },
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }

                            // ── Zonas Bloqueadas ──────────────────────
                            if (uiState.blockedZones.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    CatalogSectionHeader(
                                        title = "ZONAS BLOQUEADAS",
                                        count = uiState.blockedZones.size,
                                        color = Color(0xFFF59E0B),
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                items(uiState.blockedZones) { zone ->
                                    BlockedZoneCatalogCard(
                                        zone = zone,
                                        onEdit = {
                                            editingLocation = zone
                                            showSheet = true
                                        },
                                        onDelete = { viewModel.deleteLocation(zone.id) },
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    editingLocation = null
                    showSheet = true
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp, start = 32.dp, end = 32.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "+ Agregar Nueva Ubicación",
                    color = White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = CardSurface,
        ) {
            WorkerAddEditLocationForm(
                initial = editingLocation,
                isSaving = uiState.isSaving,
                onSave = { zone ->
                    viewModel.saveLocation(zone)
                    scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                },
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                },
            )
        }
    }
}

@Composable
private fun WorkerLocationCatalogHeader(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "worker_location_header")

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

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 20.dp, top = 6.dp)
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

            Text(
                text = "MIS UBICACIONES",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = 20.dp, top = 6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(1.dp, White.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )

            Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .graphicsLayer {
                            translationY = bubbleOffsetLarge
                            scaleX = bubbleScaleLarge
                            scaleY = bubbleScaleLarge
                        }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.08f)),
                )
            }
        }
    }
}

private fun formatCoord(value: Double): String {
    val rounded = (value * 100000.0).roundToInt() / 100000.0
    return rounded.toString()
}

@Composable
private fun WorkerLocationCard(
    location: WorkZone,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .background(CardSurface, RoundedCornerShape(20.dp))
            .border(
                width = if (location.isDefault) 1.5.dp else 1.dp,
                color = if (location.isDefault) BrandBlue else BorderSoft,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (location.isDefault) BrandBlue.copy(alpha = 0.12f) else AvatarBlueSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (location.isDefault) TablerIcons.Star else TablerIcons.MapPin,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = location.alias.ifBlank { "Sin nombre" },
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (location.isDefault) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandBlue)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = "PRINCIPAL",
                                color = White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                            )
                        }
                    }
                }

                Text(
                    text = buildString {
                        if (location.district.isNotBlank()) append(location.district)
                        if (location.district.isNotBlank() && location.canton.isNotBlank()) append(", ")
                        if (location.canton.isNotBlank()) append(location.canton)
                        if (location.canton.isNotBlank() && location.province.isNotBlank()) append(", ")
                        if (location.province.isNotBlank()) append(location.province)
                    }.ifBlank { "Sin dirección" },
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (location.latitude != 0.0 || location.longitude != 0.0) {
                    Text(
                        text = "${formatCoord(location.latitude)}, ${formatCoord(location.longitude)}",
                        color = BlueGrayText,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onEdit() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(TablerIcons.Pencil, contentDescription = "Editar", tint = BrandBlue, modifier = Modifier.size(20.dp))
            }

            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onDelete() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(TablerIcons.Trash, contentDescription = "Eliminar", tint = BrandRed, modifier = Modifier.size(20.dp))
            }
        }

        if (!location.isDefault) {
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = onSetDefault,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
            ) {
                Icon(
                    imageVector = TablerIcons.Star,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Establecer como principal",
                    color = BrandBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun WorkerEmptyLocationsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(120.dp).clip(CircleShape).background(AvatarBlueSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(TablerIcons.MapPin, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(56.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sin ubicaciones registradas",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Agrega tu ubicación principal para que los clientes sepan dónde trabajas.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun WorkerAddEditLocationForm(
    initial: WorkZone?,
    isSaving: Boolean,
    onSave: (WorkZone) -> Unit,
    onCancel: () -> Unit,
) {
    val isEditing = initial != null

    var alias by remember(initial) { mutableStateOf(initial?.alias ?: "") }
    var isBlocked by remember(initial) { mutableStateOf(initial?.blocked ?: false) }
    var selectedProvince by remember(initial) { mutableStateOf(initial?.province ?: "") }
    var selectedCanton by remember(initial) { mutableStateOf(initial?.canton ?: "") }
    var selectedDistrict by remember(initial) { mutableStateOf(initial?.district ?: "") }
    var reference by remember(initial) { mutableStateOf(initial?.reference ?: "") }
    var latitude by remember(initial) { mutableStateOf(initial?.latitude ?: 0.0) }
    var longitude by remember(initial) { mutableStateOf(initial?.longitude ?: 0.0) }
    var locationError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isEditing) "Editar Ubicación" else "Nueva Ubicación",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppBackground)
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(TablerIcons.X, contentDescription = "Cerrar", tint = BlueGrayText, modifier = Modifier.size(18.dp))
            }
        }

        // ── Checkbox: Bloquear zona ───────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isBlocked) Color(0xFFFEF3C7) else AppBackground
                )
                .border(
                    1.dp,
                    if (isBlocked) Color(0xFFFBBF24) else BorderSoft,
                    RoundedCornerShape(14.dp),
                )
                .clickable { isBlocked = !isBlocked }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Bloquear zona",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBlocked) Color(0xFFF59E0B) else TextPrimary,
                )
                Text(
                    text = if (isBlocked) "No necesita coordenadas — solo INEC"
                           else "Marcar esta entrada como zona bloqueada",
                    fontSize = 11.sp,
                    color = if (isBlocked) Color(0xFFF59E0B).copy(alpha = 0.8f) else BlueGrayText,
                )
            }
            androidx.compose.material3.Switch(
                checked = isBlocked,
                onCheckedChange = { isBlocked = it },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFF59E0B),
                    checkedTrackColor = Color(0xFFFEF3C7),
                    checkedBorderColor = Color(0xFFFBBF24),
                ),
            )
        }

        // ── Section 1: Alias ──────────────────────────────────────────────
        SectionCard(number = "1", title = "NOMBRE / ALIAS") {
            WorkerFormField(
                label = "NOMBRE",
                value = alias,
                onValueChange = { alias = it },
                placeholder = "Ej. Mi Casa, Taller, Oficina",
            )
        }

        // ── Section 2: INEC Location Picker ──────────────────────────────
        SectionCard(number = "2", title = "UBICACIÓN INEC") {
            // Province
            InecPickerField(
                label = "PROVINCIA",
                selectedValue = selectedProvince,
                placeholder = "Seleccionar provincia",
                items = costaRicaProvinces.map { it.name },
                onItemSelected = { provinceName ->
                    selectedProvince = provinceName
                    selectedCanton = ""
                    selectedDistrict = ""
                },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Canton (depends on province)
            val cantonsForProvince = remember(selectedProvince) {
                costaRicaProvinces.find { it.name == selectedProvince }?.cantons?.map { it.name }
                    ?: emptyList()
            }
            InecPickerField(
                label = "CANTÓN",
                selectedValue = selectedCanton,
                placeholder = if (selectedProvince.isBlank()) "Primero selecciona provincia" else "Seleccionar cantón",
                items = cantonsForProvince,
                enabled = selectedProvince.isNotBlank(),
                onItemSelected = { cantonName ->
                    selectedCanton = cantonName
                    selectedDistrict = ""
                },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // District (depends on canton)
            val districtsForCanton = remember(selectedProvince, selectedCanton) {
                costaRicaProvinces
                    .find { it.name == selectedProvince }
                    ?.cantons
                    ?.find { it.name == selectedCanton }
                    ?.districts
                    ?.map { it.name }
                    ?: emptyList()
            }
            InecPickerField(
                label = "DISTRITO",
                selectedValue = selectedDistrict,
                placeholder = if (selectedCanton.isBlank()) "Primero selecciona cantón" else "Seleccionar distrito",
                items = districtsForCanton,
                enabled = selectedCanton.isNotBlank(),
                onItemSelected = { selectedDistrict = it },
            )
        }

        // ── Section 3: Coordenadas (oculta si es zona bloqueada) ─────────
        AnimatedVisibility(
            visible = !isBlocked,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
        SectionCard(number = "3", title = "COORDENADAS") {
            // ── Opción A: GPS ─────────────────────────────────────────────
            CoordOptionLabel(icon = TablerIcons.CurrentLocation, text = "GPS — usar ubicación actual")
            Spacer(modifier = Modifier.height(6.dp))
            CurrentLocationButton(
                onLocationObtained = { lat, lng, prov, cant, dist ->
                    latitude = lat
                    longitude = lng
                    locationError = false
                    if (prov.isNotBlank() && selectedProvince.isBlank()) selectedProvince = prov
                    if (cant.isNotBlank() && selectedCanton.isBlank()) selectedCanton = cant
                    if (dist.isNotBlank() && selectedDistrict.isBlank()) selectedDistrict = dist
                },
                onError = { locationError = true },
            )
            if (locationError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No se pudo obtener la ubicación. Verifica los permisos de GPS.",
                    color = BrandRed, fontSize = 12.sp, lineHeight = 17.sp,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Opción B: Link de Google Maps ─────────────────────────────
            CoordOptionLabel(icon = TablerIcons.Link, text = "Link de Google Maps")
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Abre Maps → toca tu ubicación guardada → \"Compartir\" → \"Copiar enlace\".",
                color = BlueGrayText, fontSize = 11.sp, lineHeight = 16.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            var mapsUrl by remember { mutableStateOf("") }
            var mapsError by remember { mutableStateOf(false) }
            WorkerFormField(
                label = "LINK DE GOOGLE MAPS",
                value = mapsUrl,
                onValueChange = { mapsUrl = it; mapsError = false },
                placeholder = "https://maps.app.goo.gl/...",
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    val result = com.example.shared.utils.extractLatLngFromMapsUrl(mapsUrl)
                    if (result != null) {
                        latitude = result.first
                        longitude = result.second
                        mapsError = false
                    } else {
                        mapsError = true
                    }
                },
                enabled = mapsUrl.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(TablerIcons.MapPin, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Extraer coordenadas", color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            if (mapsError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No se encontraron coordenadas. Prueba con la opción manual.",
                    color = BrandRed, fontSize = 12.sp, lineHeight = 17.sp,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Opción C: Manual ──────────────────────────────────────────
            CoordOptionLabel(icon = TablerIcons.LetterCase, text = "Manual — pega \"lat, lng\" de Maps")
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "En Google Maps haz long-press sobre tu ubicación y copia las coordenadas que aparecen.",
                color = BlueGrayText, fontSize = 11.sp, lineHeight = 16.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            var manualCoords by remember { mutableStateOf("") }
            var manualError by remember { mutableStateOf(false) }
            WorkerFormField(
                label = "COORDENADAS",
                value = manualCoords,
                onValueChange = { manualCoords = it; manualError = false },
                placeholder = "9.93000, -84.08000",
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    val result = com.example.shared.utils.extractLatLngFromMapsUrl(manualCoords)
                    if (result != null) {
                        latitude = result.first
                        longitude = result.second
                        manualError = false
                    } else {
                        manualError = true
                    }
                },
                enabled = manualCoords.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(TablerIcons.Check, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aplicar coordenadas", color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            if (manualError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Formato inválido. Ejemplo: 9.93000, -84.08000",
                    color = BrandRed, fontSize = 12.sp, lineHeight = 17.sp,
                )
            }

            // ── Resultado ─────────────────────────────────────────────────
            if (latitude != 0.0 || longitude != 0.0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandBlue.copy(alpha = 0.08f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(TablerIcons.MapPin, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${formatCoord(latitude)}, ${formatCoord(longitude)}",
                        color = BrandBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        } // end AnimatedVisibility

        // ── Section 4: Reference (optional) ──────────────────────────────
        SectionCard(number = "4", title = "REFERENCIAS (OPCIONAL)") {
            WorkerFormField(
                label = "SEÑAS",
                value = reference,
                onValueChange = { reference = it },
                placeholder = "Ej. Portón azul frente al parque",
                minLines = 3,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (!isBlocked && latitude == 0.0 && longitude == 0.0) {
            Text(
                text = "Debes capturar las coordenadas para guardar la ubicación.",
                color = BrandRed,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        val canSave = alias.isNotBlank() && selectedProvince.isNotBlank() &&
            (isBlocked || (latitude != 0.0 || longitude != 0.0)) && !isSaving

        Button(
            onClick = {
                onSave(
                    WorkZone(
                        id = initial?.id ?: "",
                        alias = alias.trim(),
                        province = selectedProvince.trim(),
                        canton = selectedCanton.trim(),
                        district = selectedDistrict.trim(),
                        reference = reference.trim(),
                        latitude = if (isBlocked) 0.0 else latitude,
                        longitude = if (isBlocked) 0.0 else longitude,
                        isDefault = if (isBlocked) false else (initial?.isDefault ?: false),
                        blocked = isBlocked,
                        locationCode = initial?.locationCode ?: "",
                    )
                )
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (isEditing) "Actualizar Ubicación" else "Guardar Ubicación",
                    color = White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SectionCard(number: String, title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp))
            .background(CardSurface, RoundedCornerShape(20.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(BrandBlue),
                contentAlignment = Alignment.Center,
            ) {
                Text(number, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = BrandBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun InecPickerField(
    label: String,
    selectedValue: String,
    placeholder: String,
    items: List<String>,
    enabled: Boolean = true,
    onItemSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember(expanded) { mutableStateOf("") }

    val filtered = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items
        else items.filter { it.lowercase().contains(searchQuery.lowercase()) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = BlueGrayText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (enabled) White else AppBackground)
                .border(
                    width = if (expanded) 2.dp else 1.dp,
                    color = when {
                        expanded -> BrandBlue
                        !enabled -> BorderSoft.copy(alpha = 0.5f)
                        else -> BorderSoft
                    },
                    shape = RoundedCornerShape(14.dp),
                )
                .clickable(enabled = enabled) { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (selectedValue.isBlank()) placeholder else selectedValue,
                    color = if (selectedValue.isBlank()) BlueGrayText else TextPrimary,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = if (expanded) TablerIcons.ChevronUp else TablerIcons.ChevronDown,
                    contentDescription = null,
                    tint = if (enabled) BlueGrayText else BlueGrayText.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(White)
                    .border(1.dp, BorderSoft, RoundedCornerShape(14.dp))
                    .padding(8.dp),
            ) {
                if (items.size > 6) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar...", color = BlueGrayText, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(TablerIcons.Search, contentDescription = null, tint = BlueGrayText, modifier = Modifier.size(16.dp))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderSoft,
                            focusedContainerColor = AppBackground,
                            unfocusedContainerColor = AppBackground,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                    if (filtered.isEmpty()) {
                        item {
                            Text(
                                text = "Sin resultados",
                                color = BlueGrayText,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    } else {
                        items(filtered) { item ->
                            val isSelected = item == selectedValue
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BrandBlue.copy(alpha = 0.08f) else Color.Transparent)
                                    .clickable {
                                        onItemSelected(item)
                                        expanded = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = item,
                                    color = if (isSelected) BrandBlue else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                )
                                if (isSelected) {
                                    Icon(
                                        TablerIcons.Check,
                                        contentDescription = null,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = BlueGrayText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = BlueGrayText, fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            maxLines = if (minLines > 1) 5 else 1,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandBlue,
                unfocusedBorderColor = BorderSoft,
                focusedContainerColor = White,
                unfocusedContainerColor = AppBackground,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
            ),
        )
    }
}

@Composable
private fun CatalogSectionHeader(title: String, count: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.height(1.dp).weight(1f).background(color.copy(alpha = 0.3f)))
        Text(
            text = "$title  ($count)",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Box(modifier = Modifier.height(1.dp).weight(1f).background(color.copy(alpha = 0.3f)))
    }
}

@Composable
private fun BlockedZoneCatalogCard(
    zone: WorkZone,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFBEB), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFEF3C7)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(TablerIcons.Lock, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = zone.alias.ifBlank { zone.canton.ifBlank { "Zona bloqueada" } },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = buildString {
                    if (zone.district.isNotBlank()) append(zone.district + ", ")
                    if (zone.canton.isNotBlank()) append(zone.canton + ", ")
                    if (zone.province.isNotBlank()) append(zone.province)
                }.trimEnd(',', ' ').ifBlank { "Sin dirección" },
                fontSize = 12.sp,
                color = BlueGrayText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).clickable { onEdit() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(TablerIcons.Pencil, contentDescription = "Editar", tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
        }
        Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).clickable { onDelete() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(TablerIcons.Trash, contentDescription = "Eliminar", tint = BrandRed, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun CoordOptionLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(BrandBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(13.dp))
        }
        Text(text = text, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
