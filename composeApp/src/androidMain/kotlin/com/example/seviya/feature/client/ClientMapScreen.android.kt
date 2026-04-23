package com.example.seviya.feature.client

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AppBackground
import com.example.seviya.core.designsystem.theme.BlueGrayText
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.ClientSectionCardBorder
import com.example.seviya.core.designsystem.theme.InactiveSoft
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.SoftSurface
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.presentation.clientMap.ClientMapViewModel
import com.example.shared.presentation.clientMap.WorkerMapMarker
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
actual fun ClientMapPlatformScreen(
    clientId: String,
    viewModel: ClientMapViewModel,
    onWorkerClick: (workerId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val defaultLatLng = LatLng(9.9281, -84.0907)

    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(
                uiState.clientAddress?.let { LatLng(it.latitude, it.longitude) } ?: defaultLatLng,
                12f,
            )
    }

    LaunchedEffect(uiState.clientAddress) {
        uiState.clientAddress?.let {
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 12f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
        ) {
            uiState.clientAddress?.let { address ->
                Marker(
                    state = MarkerState(LatLng(address.latitude, address.longitude)),
                    title = address.alias.ifBlank { "Tu ubicación" },
                )
            }

            uiState.filteredMarkers.forEach { marker ->
                Marker(
                    state = MarkerState(LatLng(marker.workZone.latitude, marker.workZone.longitude)),
                    title = marker.user.name,
                    snippet = marker.user.categories.firstOrNull()?.name ?: "",
                    onClick = {
                        viewModel.selectMarker(marker)
                        false
                    },
                )
            }
        }

        ClientMapPremiumHeader(
            searchQuery = uiState.searchQuery,
            categoryQuery = uiState.categoryQuery,
            minStars = uiState.minStars,
            onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
            onCategoryQueryChange = { viewModel.onCategoryQueryChanged(it) },
            onDecreaseStars = {
                val current = uiState.minStars?.toInt() ?: 1
                if (current <= 1) {
                    viewModel.onMinStarsSelected(null)
                } else {
                    viewModel.onMinStarsSelected((current - 1).toDouble())
                }
            },
            onIncreaseStars = {
                val current = uiState.minStars?.toInt() ?: 0
                if (current >= 5) {
                    viewModel.onMinStarsSelected(5.0)
                } else {
                    viewModel.onMinStarsSelected((current + 1).toDouble())
                }
            },
            modifier = Modifier.align(Alignment.TopCenter),
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        uiState.clientAddress?.let {
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 14f)
                        }
                    },
                shape = RoundedCornerShape(18.dp),
                color = White,
                border = BorderStroke(1.dp, BorderSoft),
                shadowElevation = 8.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = TablerIcons.MapPin,
                        contentDescription = "Centrar mapa",
                        tint = BrandBlue,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = White)
            }
        }

        uiState.selectedMarker?.let { marker ->
            WorkerPopup(
                marker = marker,
                onDismiss = { viewModel.clearSelectedMarker() },
                onViewProfile = { onWorkerClick(marker.user.uid) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun ClientMapPremiumHeader(
    searchQuery: String,
    categoryQuery: String,
    minStars: Double?,
    onSearchQueryChange: (String) -> Unit,
    onCategoryQueryChange: (String) -> Unit,
    onDecreaseStars: () -> Unit,
    onIncreaseStars: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "client_map_header")

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

    LaunchedEffect(Unit) { entranceVisible.value = true }

    AnimatedVisibility(
        visible = entranceVisible.value,
        enter =
            fadeIn(animationSpec = tween(500)) +
                    slideInVertically(
                        initialOffsetY = { -it / 3 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing),
                    ),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(BrandBlue)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(140.dp)
                        .offset(x = shimmerOffset.dp)
                        .graphicsLayer {
                            rotationZ = -18f
                            alpha = 0.14f
                        }
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    White.copy(alpha = 0.42f),
                                    Color.Transparent,
                                )
                            )
                        )
                )
            }

            Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 22.dp, top = 20.dp)) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
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
                        .size(38.dp)
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier
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
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
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

                    Text(
                        text = "MAPA",
                        modifier = Modifier
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
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Encuentra trabajadores por nombre, categoría o calificación",
                    color = White.copy(alpha = 0.88f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = White.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.12f)),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PremiumMapSearchField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = "Buscar trabajador por nombre...",
                            leadingIcon = TablerIcons.User,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PremiumMapSearchField(
                                value = categoryQuery,
                                onValueChange = onCategoryQueryChange,
                                placeholder = "Categoría...",
                                leadingIcon = TablerIcons.Tag,
                                modifier = Modifier.weight(1f),
                            )

                            PremiumStarsControl(
                                minStars = minStars,
                                onDecrease = onDecreaseStars,
                                onIncrease = onIncreaseStars,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumMapSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(18.dp),
        color = White,
        border = BorderStroke(
            1.dp,
            if (value.isNotBlank()) BrandBlue.copy(alpha = 0.18f) else BorderSoft
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank()) SoftBlueSurface else AppBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (value.isNotBlank()) BrandBlue else InactiveSoft,
                    modifier = Modifier.size(15.dp),
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = TextBluePrimary,
                    fontWeight = FontWeight.SemiBold,
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium.copy(color = InactiveSoft),
                            )
                        }
                        innerTextField()
                    }
                },
            )

            if (value.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(SoftSurface)
                        .clickable { onValueChange("") },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = TablerIcons.X,
                        contentDescription = "Limpiar",
                        tint = BlueGrayText,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumStarsControl(
    minStars: Double?,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Surface(
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(18.dp),
        color = White,
        border = BorderStroke(1.dp, ClientSectionCardBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(SoftSurface)
                    .clickable { onDecrease() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TablerIcons.ChevronDown,
                    contentDescription = "Bajar estrellas",
                    tint = BlueGrayText,
                    modifier = Modifier.size(14.dp),
                )
            }

            Surface(shape = RoundedCornerShape(999.dp), color = SoftBlueSurface) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = TablerIcons.Star,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(13.dp),
                    )

                    Text(
                        text = minStars?.toInt()?.toString() ?: "-",
                        color = BrandBlue,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(SoftSurface)
                    .clickable { onIncrease() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TablerIcons.ChevronUp,
                    contentDescription = "Subir estrellas",
                    tint = BlueGrayText,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun WorkerPopup(
    marker: WorkerMapMarker,
    onDismiss: () -> Unit,
    onViewProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            White,
                            Color(0xFFF9FBFF),
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileImage(
                    imageUrl = marker.user.profilePicture,
                    name = marker.user.name,
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = marker.user.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextBluePrimary,
                        ),
                        maxLines = 1,
                    )

                    Text(
                        text = formatCategoryName(
                            marker.user.categories.firstOrNull()?.name ?: "Trabajador"
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BlueGrayText,
                            fontWeight = FontWeight.Medium,
                        ),
                        maxLines = 1,
                    )
                }

                marker.user.stars?.let { stars ->
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFFFFF6D9),
                        border = BorderStroke(1.dp, Color(0xFFFFE7A3)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = TablerIcons.Star,
                                contentDescription = null,
                                tint = Color(0xFFE0A100),
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = stars.toString(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF8A5A00),
                                ),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoChip(
                    icon = TablerIcons.MapPin,
                    text = marker.workZone.district.ifBlank { "Zona disponible" },
                    containerColor = BrandBlue.copy(alpha = 0.10f),
                    contentColor = BrandBlue,
                    modifier = Modifier.weight(1f),
                )

                marker.user.travelTime?.let { time ->
                    InfoChip(
                        icon = TablerIcons.Clock,
                        text = "~${time} min",
                        containerColor = Color(0xFFEAF8EE),
                        contentColor = Color(0xFF2E7D32),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(0.85f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSoft),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextBluePrimary
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = TablerIcons.X,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Cerrar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }

                Button(
                    onClick = onViewProfile,
                    modifier = Modifier
                        .weight(1.15f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = TablerIcons.Eye,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(17.dp),
                        )
                        Text(
                            text = "Ver perfil",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = White,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileImage(
    imageUrl: String?,
    name: String,
) {
    Surface(
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFEAF2FF),
        border = BorderStroke(1.dp, Color(0xFFD6E4FA)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            when {
                !imageUrl.isNullOrBlank() -> {
                    KamelImage(
                        resource = asyncPainterResource(imageUrl),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }

                else -> {
                    Text(
                        text = name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = BrandBlue,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.10f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(15.dp),
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
            )
        }
    }
}

private fun formatCategoryName(category: String): String {
    val clean = category.trim()
    if (clean.isEmpty()) return category
    return clean.replaceFirstChar { first ->
        if (first.isLowerCase()) first.titlecase() else first.toString()
    }
}