package com.example.seviya.UI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.entity.WorkerListItemData
import com.example.shared.presentation.workersList.WorkersListUiState
import com.example.shared.presentation.workersList.WorkersListViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.round

private object WorkersListColors {
    val BrandBlue = Color(0xFF004AAD)
    val BrandBlueDark = Color(0xFF083C8B)
    val Surface = Color(0xFFF6F8FC)
    val White = Color(0xFFFFFFFF)
    val Border = Color(0xFFE8EDF5)
    val TextPrimary = Color(0xFF111827)
    val TextSecondary = Color(0xFF6B7280)
    val Inactive = Color(0xFF98A2B3)
    val Red = Color(0xFFEF4444)
    val YellowBg = Color(0xFFFFF5D7)
    val YellowText = Color(0xFFB7791F)
}

private enum class WorkerListSort {
    ALL,
    TOP_RATED,
    NEAREST,
    LOW_PRICE
}

@Composable
fun WorkersListRoute(
    viewModel: WorkersListViewModel,
    selectedCategoryId: String? = null,
    selectedCategoryName: String? = null,
    selectedProvince: String? = null,
    selectedDistrict: String? = null,
    avatarPainter: Painter? = null,
    onWorkerClick: (String) -> Unit = {},
    onThemeClick: () -> Unit = {},
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWorkers()
    }

    WorkersListScreen(
        state = state,
        selectedCategoryId = selectedCategoryId,
        selectedCategoryName = selectedCategoryName,
        selectedProvince = selectedProvince,
        selectedDistrict = selectedDistrict,
        avatarPainter = avatarPainter,
        onWorkerClick = onWorkerClick,
        onThemeClick = onThemeClick,
        onBottomServices = onBottomServices,
        onBottomMap = onBottomMap,
        onBottomSearch = onBottomSearch,
        onBottomNotifications = onBottomNotifications,
        onBottomMenu = onBottomMenu
    )
}

@Composable
fun WorkersListScreen(
    state: WorkersListUiState,
    selectedCategoryId: String? = null,
    selectedCategoryName: String? = null,
    selectedProvince: String? = null,
    selectedDistrict: String? = null,
    avatarPainter: Painter? = null,
    onWorkerClick: (String) -> Unit = {},
    onThemeClick: () -> Unit = {},
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {}
) {
    var selectedSort by remember { mutableStateOf(WorkerListSort.ALL) }

    val visibleWorkers = remember(
        state.workers,
        selectedSort,
        selectedCategoryId,
        selectedCategoryName,
        selectedProvince,
        selectedDistrict
    ) {
        val filtered = state.workers.filter { worker ->
            val matchCategoryId = selectedCategoryId?.let { it in worker.categoryIds } ?: true
            val matchCategoryName = selectedCategoryName?.let { categoryName ->
                worker.categoryNames.any { it.equals(categoryName, ignoreCase = true) }
            } ?: true

            matchCategoryId && matchCategoryName
        }

        when (selectedSort) {
            WorkerListSort.ALL -> filtered.sortedByDescending { it.stars }
            WorkerListSort.TOP_RATED -> filtered.sortedByDescending { it.stars }
            WorkerListSort.LOW_PRICE -> filtered.sortedBy { it.startingPrice }
            WorkerListSort.NEAREST -> {
                filtered.sortedWith(
                    compareByDescending<WorkerListItemData> {
                        when {
                            !selectedDistrict.isNullOrBlank() &&
                                    it.district.equals(selectedDistrict, ignoreCase = true) -> 2
                            !selectedProvince.isNullOrBlank() &&
                                    it.province.equals(selectedProvince, ignoreCase = true) -> 1
                            else -> 0
                        }
                    }.thenByDescending { it.stars }
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = WorkersListColors.BrandBlue,
        bottomBar = {
            WorkersListBottomBar(
                onServices = onBottomServices,
                onMap = onBottomMap,
                onSearch = onBottomSearch,
                onNotifications = onBottomNotifications,
                onMenu = onBottomMenu
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WorkersListColors.BrandBlue)
                .padding(innerPadding)
        ) {
            WorkersHeader(
                selectedCategoryName = selectedCategoryName,
                onThemeClick = onThemeClick
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-18).dp),
                color = WorkersListColors.Surface,
                shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SortRow(
                        selectedSort = selectedSort,
                        onSelectedSort = { selectedSort = it }
                    )

                    when {
                        state.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Cargando trabajadores...",
                                    color = WorkersListColors.TextSecondary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        state.errorMessage != null -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.errorMessage ?: "Ocurrió un error.",
                                    color = WorkersListColors.Red,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        visibleWorkers.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay trabajadores disponibles para esta categoría.",
                                    color = WorkersListColors.TextSecondary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 6.dp,
                                    bottom = 110.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(
                                    items = visibleWorkers,
                                    key = { it.workerId }
                                ) { worker ->
                                    WorkerGridCard(
                                        worker = worker,
                                        avatarPainter = avatarPainter,
                                        onClick = { onWorkerClick(worker.workerId) }
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
private fun WorkersHeader(
    selectedCategoryName: String?,
    onThemeClick: () -> Unit
) {
    val subtitle = selectedCategoryName?.takeIf { it.isNotBlank() }?.let {
        "Explora profesionales en $it"
    } ?: "Encuentra el experto ideal para hoy"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServiYaPill()

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable(onClick = onThemeClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TablerIcons.Moon,
                    contentDescription = "Tema",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Profesionales Disponibles",
            color = Color.White,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.82f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ServiYaPill() {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 4.dp
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = WorkersListColors.BrandBlue,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) { append("SERVI") }

                withStyle(
                    SpanStyle(
                        color = WorkersListColors.Red,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) { append("YA") }
            },
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            fontSize = 17.sp
        )
    }
}

@Composable
private fun SortRow(
    selectedSort: WorkerListSort,
    onSelectedSort: (WorkerListSort) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SortChip(
            text = "Todos",
            selected = selectedSort == WorkerListSort.ALL,
            onClick = { onSelectedSort(WorkerListSort.ALL) }
        )
        SortChip(
            text = "Mejor calificados",
            selected = selectedSort == WorkerListSort.TOP_RATED,
            onClick = { onSelectedSort(WorkerListSort.TOP_RATED) }
        )
        SortChip(
            text = "Más cercanos",
            selected = selectedSort == WorkerListSort.NEAREST,
            onClick = { onSelectedSort(WorkerListSort.NEAREST) }
        )
        SortChip(
            text = "Precio más bajo",
            selected = selectedSort == WorkerListSort.LOW_PRICE,
            onClick = { onSelectedSort(WorkerListSort.LOW_PRICE) }
        )
    }
}

@Composable
private fun SortChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) WorkersListColors.BrandBlue else Color(0xFFF1F4F8),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            color = if (selected) Color.White else Color(0xFF5B6577),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WorkerGridCard(
    worker: WorkerListItemData,
    avatarPainter: Painter?,
    onClick: () -> Unit
) {
    val provinceText = worker.province.ifBlank { "Ubicación no disponible" }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(18.dp), clip = false),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE4ECF7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WorkerCircularPhoto(
                imageUrl = worker.profilePictureLink,
                avatarPainter = avatarPainter
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = worker.name.ifBlank { "Profesional" },
                color = WorkersListColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            WorkerProvinceInline(
                province = provinceText
            )

            Spacer(modifier = Modifier.height(8.dp))

            WorkerRatingInline(
                rating = worker.stars
            )

            Spacer(modifier = Modifier.height(10.dp))

            WorkerPriceText(
                price = worker.startingPrice
            )
        }
    }
}

@Composable
private fun WorkerProvinceInline(
    province: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = TablerIcons.MapPin,
            contentDescription = null,
            tint = WorkersListColors.Inactive,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = province,
            color = WorkersListColors.TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WorkerCircularPhoto(
    imageUrl: String?,
    avatarPainter: Painter?
) {
    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    width = 4.dp,
                    color = Color(0xFFBFD6FF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F6FA)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    !imageUrl.isNullOrBlank() -> {
                        val painterResource = asyncPainterResource(data = imageUrl)

                        KamelImage(
                            resource = painterResource,
                            contentDescription = "Foto del trabajador",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            onFailure = {
                                if (avatarPainter != null) {
                                    Image(
                                        painter = avatarPainter,
                                        contentDescription = "Foto del trabajador",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Text("👷", fontSize = 34.sp)
                                }
                            }
                        )
                    }

                    avatarPainter != null -> {
                        Image(
                            painter = avatarPainter,
                            contentDescription = "Foto del trabajador",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    else -> {
                        Text("👷", fontSize = 34.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerRatingInline(
    rating: Double,
    reviewCount: Int? = null
) {
    val ratingText = remember(rating) {
        val rounded = round(rating * 10) / 10.0
        if (rounded % 1.0 == 0.0) "${rounded.toInt()}.0" else rounded.toString()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "★",
            color = Color(0xFFF4B400),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = if (reviewCount != null) "$ratingText ($reviewCount)" else ratingText,
            color = WorkersListColors.TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun WorkerPriceText(
    price: Double
) {
    val text = if (price > 0.0) formatPrice(price) else "Consultar"

    Text(
        text = "Desde $text",
        color = Color(0xFF004AAD),
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun CategoryTag(
    text: String,
    background: Color,
    border: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(
                width = 1.dp,
                color = border,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private data class CategoryBadgePalette(
    val background: Color,
    val border: Color,
    val text: Color
)

private fun categoryBadge(name: String): CategoryBadgePalette {
    return when (name.trim().lowercase()) {
        "electricidad", "electricista" -> CategoryBadgePalette(
            background = Color(0xFFFFF1F1),
            border = Color(0xFFF9D0D0),
            text = Color(0xFFE11D48)
        )

        "limpieza" -> CategoryBadgePalette(
            background = Color(0xFFF1F7FF),
            border = Color(0xFFD7E6FF),
            text = Color(0xFF2563EB)
        )

        "plomería", "plomeria" -> CategoryBadgePalette(
            background = Color(0xFFF0FDF4),
            border = Color(0xFFD1F2DD),
            text = Color(0xFF16A34A)
        )

        "pintura" -> CategoryBadgePalette(
            background = Color(0xFFFAF5FF),
            border = Color(0xFFE9D8FD),
            text = Color(0xFF9333EA)
        )

        else -> CategoryBadgePalette(
            background = Color(0xFFF8FAFC),
            border = Color(0xFFE2E8F0),
            text = Color(0xFF475569)
        )
    }
}

private fun formatPrice(value: Double): String {
    val rounded = round(value * 100) / 100.0
    return if (rounded % 1.0 == 0.0) {
        "₡${rounded.toInt()}"
    } else {
        "₡$rounded"
    }
}

@Composable
private fun WorkersListBottomBar(
    onServices: () -> Unit,
    onMap: () -> Unit,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onMenu: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Divider(color = Color(0xFFE8ECF2), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Servicios",
                    selected = true,
                    onClick = onServices,
                    icon = TablerIcons.Apps
                )

                BottomNavItem(
                    label = "Mapa",
                    selected = false,
                    onClick = onMap,
                    icon = TablerIcons.MapPin
                )

                BottomNavItem(
                    label = "Buscar",
                    selected = false,
                    onClick = onSearch,
                    icon = TablerIcons.Search
                )

                BottomNavItem(
                    label = "Notif.",
                    selected = false,
                    onClick = onNotifications,
                    icon = TablerIcons.Bell,
                    showDot = true
                )

                BottomNavItem(
                    label = "Menú",
                    selected = false,
                    onClick = onMenu,
                    icon = TablerIcons.User
                )
            }

            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    showDot: Boolean = false
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) WorkersListColors.BrandBlue else WorkersListColors.Inactive,
                modifier = Modifier.size(24.dp)
            )

            if (showDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 3.dp, y = (-2).dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(WorkersListColors.Red)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            color = if (selected) WorkersListColors.BrandBlue else WorkersListColors.Inactive,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
            maxLines = 1
        )
    }
}