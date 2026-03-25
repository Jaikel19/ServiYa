package com.example.seviya.UI

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.seviya.core.designsystem.theme.AppBackground
import com.example.seviya.core.designsystem.theme.AvatarBlueSoft
import com.example.seviya.core.designsystem.theme.BlueGrayText
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.CardSurface
import com.example.seviya.core.designsystem.theme.TextPrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Address
import com.example.shared.presentation.clientLocationCatalog.ClientLocationCatalogViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientLocationCatalogScreen(
    clientId: String,
    viewModel: ClientLocationCatalogViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var showSheet by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<Address?>(null) }

    LaunchedEffect(clientId) {
        viewModel.loadAddresses(clientId)
    }

    Scaffold(
        containerColor = White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White)
            ) {
                ClientLocationCatalogTopHeader(
                    onBack = onBack
                )

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandBlue)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(White),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 1.dp,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.addresses.isEmpty()) {
                            item {
                                EmptyLocationsState()
                            }
                        } else {
                            items(uiState.addresses) { address ->
                                LocationCard(
                                    address = address,
                                    onEdit = {
                                        editingAddress = address
                                        showSheet = true
                                    },
                                    onDelete = {
                                        viewModel.deleteAddress(clientId, address.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    editingAddress = null
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
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "+ Agregar Nueva Ubicación",
                    color = White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = CardSurface
        ) {
            AddEditLocationForm(
                initial = editingAddress,
                isSaving = uiState.isSaving,
                onSave = { address ->
                    viewModel.saveAddress(clientId, address)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showSheet = false
                    }
                },
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun ClientLocationCatalogTopHeader(
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "location_catalog_header")

    val leftBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "left_badge_scale"
    )

    val rightBadgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "right_badge_scale"
    )

    val bubbleOffsetLarge by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_offset_large"
    )

    val bubbleOffsetSmall by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_offset_small"
    )

    val bubbleScaleLarge by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_scale_large"
    )

    val bubbleScaleSmall by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_scale_small"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -260f,
        targetValue = 620f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val arrowFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrow_float"
    )

    val entranceVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        entranceVisible.value = true
    }

    AnimatedVisibility(
        visible = entranceVisible.value,
        enter = fadeIn(
            animationSpec = tween(500)
        ) + slideInVertically(
            initialOffsetY = { -it / 3 },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
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
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 20.dp, top = 6.dp)
                    .graphicsLayer {
                        scaleX = leftBadgeScale
                        scaleY = leftBadgeScale
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(
                        width = 1.dp,
                        color = White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer {
                            translationY = arrowFloat
                        }
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.14f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.ArrowLeft,
                        contentDescription = "Volver",
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Servi",
                        color = White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Ya",
                        color = BrandRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Text(
                text = "UBICACIONES",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = 20.dp, top = 6.dp)
                    .graphicsLayer {
                        scaleX = rightBadgeScale
                        scaleY = rightBadgeScale
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(White.copy(alpha = 0.14f))
                    .border(
                        width = 1.dp,
                        color = White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
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

private fun formatCoordinate(value: Double): String {
    val rounded = (value * 100000.0).roundToInt() / 100000.0
    return rounded.toString()
}

private fun formatLocationLabel(lat: Double, lng: Double): String {
    return "${formatCoordinate(lat)}, ${formatCoordinate(lng)}"
}

@Composable
private fun LocationCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .background(CardSurface, RoundedCornerShape(20.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AvatarBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (
                    address.alias.lowercase().contains("trabajo") ||
                    address.alias.lowercase().contains("oficina")
                ) TablerIcons.Briefcase else TablerIcons.Home,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = address.alias.ifBlank { "Sin nombre" },
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    if (address.district.isNotBlank()) append(address.district)
                    if (address.district.isNotBlank() && address.province.isNotBlank()) append(", ")
                    if (address.province.isNotBlank()) append(address.province)
                }.ifBlank { "Sin dirección" },
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (address.reference.isNotBlank()) {
                Text(
                    text = address.reference,
                    color = BlueGrayText,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { onEdit() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.Pencil,
                contentDescription = "Editar",
                tint = BrandBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.Trash,
                contentDescription = "Eliminar",
                tint = BrandRed,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyLocationsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AvatarBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.MapPin,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sin ubicaciones guardadas",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Agrega tus direcciones frecuentes para solicitar citas más rápido.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun AddEditLocationForm(
    initial: Address?,
    isSaving: Boolean,
    onSave: (Address) -> Unit,
    onCancel: () -> Unit
) {
    val isEditing = initial != null

    var alias by remember(initial) { mutableStateOf(initial?.alias ?: "") }
    var province by remember(initial) { mutableStateOf(initial?.province ?: "") }
    var district by remember(initial) { mutableStateOf(initial?.district ?: "") }
    var canton by remember(initial) { mutableStateOf(initial?.canton ?: "") }
    var reference by remember(initial) { mutableStateOf(initial?.reference ?: "") }
    var latitude by remember(initial) { mutableStateOf(initial?.latitude ?: 0.0) }
    var longitude by remember(initial) { mutableStateOf(initial?.longitude ?: 0.0) }
    var locationLabel by remember(initial) {
        mutableStateOf(
            if ((initial?.latitude ?: 0.0) != 0.0) {
                formatLocationLabel(initial!!.latitude, initial.longitude)
            } else {
                "Sin ubicación GPS"
            }
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isEditing) "Editar Ubicación" else "Nueva Ubicación",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppBackground)
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TablerIcons.X,
                    contentDescription = "Cerrar",
                    tint = BlueGrayText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
                .background(CardSurface, RoundedCornerShape(20.dp))
                .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BrandBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SELECCIONAR EN MAPA",
                    color = BrandBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AvatarBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = TablerIcons.MapPin,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = locationLabel,
                        color = BlueGrayText,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CurrentLocationButton(
                onLocationObtained = { lat, lng, prov, cant, dist ->
                    latitude = lat
                    longitude = lng
                    locationLabel = formatLocationLabel(lat, lng)
                    if (prov.isNotBlank()) province = prov
                    if (cant.isNotBlank()) canton = cant
                    if (dist.isNotBlank()) district = dist
                },
                onError = {
                    locationLabel = "No se pudo obtener la ubicación"
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
                .background(CardSurface, RoundedCornerShape(20.dp))
                .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BrandBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DETALLES DE LA UBICACIÓN",
                    color = BrandBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            FormField(
                label = "NOMBRE / ALIAS",
                value = alias,
                onValueChange = { alias = it },
                placeholder = "Ej. Mi Casa, Oficina, Gimnasio"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        label = "PROVINCIA",
                        value = province,
                        onValueChange = { province = it },
                        placeholder = "Ej. San José"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        label = "CANTÓN",
                        value = canton,
                        onValueChange = { canton = it },
                        placeholder = "Ej. Escazú"
                    )
                }
            }

            FormField(
                label = "DISTRITO",
                value = district,
                onValueChange = { district = it },
                placeholder = "Ej. San Rafael"
            )

            FormField(
                label = "REFERENCIAS",
                value = reference,
                onValueChange = { reference = it },
                placeholder = "Ej. Portón blanco frente al parque",
                minLines = 3
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (latitude == 0.0 || longitude == 0.0) {
            Text(
                text = "Debes usar tu ubicación actual para poder guardar",
                color = BrandRed,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                onSave(
                    Address(
                        id = initial?.id ?: "",
                        alias = alias.trim(),
                        province = province.trim(),
                        district = district.trim(),
                        canton = canton.trim(),
                        reference = reference.trim(),
                        latitude = latitude,
                        longitude = longitude,
                        isDefault = initial?.isDefault ?: false
                    )
                )
            },
            enabled = alias.isNotBlank() && latitude != 0.0 && longitude != 0.0 && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (isEditing) "Actualizar Ubicación" else "Guardar Ubicación",
                    color = White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = BlueGrayText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = BlueGrayText, fontSize = 13.sp)
            },
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
                unfocusedTextColor = TextPrimary
            )
        )
    }
}