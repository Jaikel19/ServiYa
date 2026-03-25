package com.example.seviya.feature.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BackgroundTopBlue
import com.example.seviya.core.designsystem.theme.BorderSoftAlt
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandBlueSoft
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.ImageBlueSoftAlt
import com.example.seviya.core.designsystem.theme.TextPrimaryAlt
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Category
import com.example.shared.domain.entity.WorkerListItemData
import com.example.shared.presentation.categories.CategoriesUiState
import com.example.shared.presentation.categories.CategoriesViewModel
import com.example.shared.presentation.favoriteWorkers.FavoriteWorkersUiState
import com.example.shared.presentation.favoriteWorkers.FavoriteWorkersViewModel
import com.example.shared.presentation.workersList.WorkersListUiState
import com.example.shared.presentation.workersList.WorkersListViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Apps
import compose.icons.tablericons.ArrowRight
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.Brush
import compose.icons.tablericons.Building
import compose.icons.tablericons.Camera
import compose.icons.tablericons.Car
import compose.icons.tablericons.Droplet
import compose.icons.tablericons.Heart
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Paint
import compose.icons.tablericons.Plug
import compose.icons.tablericons.Scissors
import compose.icons.tablericons.Tree
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.round

private const val HOME_CATEGORY_LIMIT = 8
private const val HOME_FAVORITES_LIMIT = 5
private const val HOME_CATEGORY_SECTION_LIMIT = 4
private const val HOME_WORKERS_PER_SECTION_LIMIT = 5

private data class ClientHomeCategorySection(
    val category: Category,
    val workers: List<WorkerListItemData>
)

private data class HomeCategoryVisuals(
    val icon: ImageVector,
    val cardStart: Color,
    val cardEnd: Color,
    val border: Color,
    val iconPlateStart: Color,
    val iconPlateEnd: Color,
    val iconTint: Color,
    val accent: Color
)

@Composable
fun ClientHomeRoute(
    clientId: String,
    categoriesViewModel: CategoriesViewModel,
    favoriteWorkersViewModel: FavoriteWorkersViewModel,
    workersListViewModel: WorkersListViewModel,
    avatarPainter: Painter? = null,
    onWorkerClick: (String) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onOpenCategoriesCatalog: () -> Unit = {},
    onCategoryClick: (Category) -> Unit = {}
) {
    val categoriesState by categoriesViewModel.uiState.collectAsState()
    val favoritesState by favoriteWorkersViewModel.uiState.collectAsState()
    val workersState by workersListViewModel.uiState.collectAsState()

    LaunchedEffect(clientId) {
        favoriteWorkersViewModel.loadFavoriteWorkers(clientId)
        workersListViewModel.loadWorkers()
        workersListViewModel.loadFavoriteWorkerIds(clientId)
    }

    ClientHomeScreen(
        categoriesState = categoriesState,
        favoritesState = favoritesState,
        workersState = workersState,
        avatarPainter = avatarPainter,
        onWorkerClick = onWorkerClick,
        onFavoritesClick = onFavoritesClick,
        onOpenCategoriesCatalog = onOpenCategoriesCatalog,
        onCategoryClick = onCategoryClick,
        onToggleFavorite = { workerId ->
            workersListViewModel.toggleFavorite(clientId, workerId)
        }
    )
}

@Composable
fun ClientHomeScreen(
    categoriesState: CategoriesUiState,
    favoritesState: FavoriteWorkersUiState,
    workersState: WorkersListUiState,
    avatarPainter: Painter? = null,
    onWorkerClick: (String) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onOpenCategoriesCatalog: () -> Unit = {},
    onCategoryClick: (Category) -> Unit = {},
    onToggleFavorite: (String) -> Unit = {}
) {
    val visibleCategories = remember(categoriesState.categories) {
        categoriesState.categories.take(HOME_CATEGORY_LIMIT)
    }

    val visibleFavorites = remember(favoritesState.workers) {
        favoritesState.workers
            .sortedByDescending { it.stars }
            .take(HOME_FAVORITES_LIMIT)
    }

    val featuredSections = remember(categoriesState.categories, workersState.workers) {
        categoriesState.categories
            .take(HOME_CATEGORY_SECTION_LIMIT)
            .mapNotNull { category ->
                val workers = workersState.workers
                    .filter { workerBelongsToCategory(it, category) }
                    .sortedByDescending { it.stars }
                    .take(HOME_WORKERS_PER_SECTION_LIMIT)

                if (workers.isEmpty()) null
                else ClientHomeCategorySection(category = category, workers = workers)
            }
    }

    val isInitialLoading = categoriesState.isLoading && workersState.isLoading

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackgroundAlt,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundTopBlue,
                            AppBackgroundAlt
                        )
                    )
                )
                .padding(padding)
        ) {
            ClientHomeHeader(
                favoritesCount = favoritesState.workers.size,
                onFavoritesClick = onFavoritesClick
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-10).dp),
                color = AppBackgroundAlt,
                shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
            ) {
                when {
                    isInitialLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = BrandBlue,
                                strokeWidth = 3.5.dp
                            )
                        }
                    }

                    categoriesState.errorMessage != null && categoriesState.categories.isEmpty() -> {
                        HomeMessageCard(
                            text = categoriesState.errorMessage ?: "No se pudieron cargar las categorías.",
                            isError = true
                        )
                    }

                    workersState.errorMessage != null && workersState.workers.isEmpty() -> {
                        HomeMessageCard(
                            text = workersState.errorMessage ?: "No se pudieron cargar los trabajadores.",
                            isError = true
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 36.dp)
                        ) {
                            item {
                                SectionHeaderWithSubtitle(
                                    title = "Categorías",
                                    onActionClick = onOpenCategoriesCatalog
                                )

                                if (visibleCategories.isEmpty()) {
                                    HomeMessageCard(
                                        text = "No hay categorías disponibles por ahora."
                                    )
                                } else {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        items(
                                            items = visibleCategories,
                                            key = { it.id }
                                        ) { category ->
                                            CategoryBubbleCard(
                                                category = category,
                                                onClick = { onCategoryClick(category) }
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                SectionHeader(
                                    title = "Tus favoritos",
                                    onActionClick = onFavoritesClick
                                )

                                when {
                                    favoritesState.isLoading && favoritesState.workers.isEmpty() -> {
                                        HomeMessageCard(text = "Cargando favoritos...")
                                    }

                                    visibleFavorites.isEmpty() -> {
                                        HomeMessageCard(
                                            text = "Todavía no tienes trabajadores favoritos."
                                        )
                                    }

                                    else -> {
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                                        ) {
                                            items(
                                                items = visibleFavorites,
                                                key = { it.workerId }
                                            ) { worker ->
                                                HomeWorkerCard(
                                                    worker = worker,
                                                    avatarPainter = avatarPainter,
                                                    isFavorite = true,
                                                    displayCategoryName = null,
                                                    onClick = { onWorkerClick(worker.workerId) },
                                                    onFavoriteClick = {
                                                        onToggleFavorite(worker.workerId)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            items(
                                items = featuredSections,
                                key = { it.category.id }
                            ) { section ->
                                SectionHeader(
                                    title = "Destacados en ${section.category.name}",
                                    onActionClick = { onCategoryClick(section.category) }
                                )

                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    items(
                                        items = section.workers,
                                        key = { it.workerId }
                                    ) { worker ->
                                        HomeWorkerCard(
                                            worker = worker,
                                            avatarPainter = avatarPainter,
                                            isFavorite = workersState.favoriteWorkerIds.contains(worker.workerId),
                                            displayCategoryName = section.category.name,
                                            onClick = { onWorkerClick(worker.workerId) },
                                            onFavoriteClick = {
                                                onToggleFavorite(worker.workerId)
                                            }
                                        )
                                    }
                                }
                            }

                            if (featuredSections.isEmpty() && !workersState.isLoading) {
                                item {
                                    SectionTitle(
                                        title = "Destacados por categoría",
                                        subtitle = "Aún no hay suficientes trabajadores cargados"
                                    )
                                    HomeMessageCard(
                                        text = "Cuando existan trabajadores asociados a las primeras categorías, aquí se mostrarán los destacados."
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
private fun ClientHomeHeader(
    favoritesCount: Int,
    onFavoritesClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = White.copy(alpha = 0.08f)
            ) {}

            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomStart),
                shape = CircleShape,
                color = White.copy(alpha = 0.10f)
            ) {}
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandBadge()

                Surface(
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(onClick = onFavoritesClick),
                    shape = RoundedCornerShape(18.dp),
                    color = White.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box {
                            Icon(
                                imageVector = TablerIcons.Heart,
                                contentDescription = "Favoritos",
                                tint = White,
                                modifier = Modifier.size(21.dp)
                            )

                            if (favoritesCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 7.dp, y = (-4).dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(BrandRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (favoritesCount > 9) "9+" else favoritesCount.toString(),
                                        color = White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Trabajadores Destacados",
                color = White,
                fontSize = 24.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Explora categorías y descubre perfiles recomendados.",
                color = White.copy(alpha = 0.82f),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun BrandBadge() {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = White.copy(alpha = 0.13f),
        border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Servi",
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Ya",
                color = BrandRed,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun SectionHeaderWithSubtitle(
    title: String,
    subtitle: String? = null,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = TextPrimaryAlt,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = onActionClick),
                shape = CircleShape,
                color = White,
                border = BorderStroke(1.dp, BorderSoftAlt),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = TablerIcons.ArrowRight,
                        contentDescription = "Ir a categorías",
                        tint = BrandBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 12.dp)
    ) {
        Text(
            text = title,
            color = TextPrimaryAlt,
            fontSize = 23.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = TextPrimaryAlt,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Surface(
            modifier = Modifier
                .size(36.dp)
                .clickable(onClick = onActionClick),
            shape = CircleShape,
            color = White,
            border = BorderStroke(1.dp, BorderSoftAlt),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = TablerIcons.ArrowRight,
                    contentDescription = "Ir a la sección",
                    tint = BrandBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryBubbleCard(
    category: Category,
    onClick: () -> Unit
) {
    val visuals = remember(category.name) {
        categoryVisuals(category.name)
    }

    Surface(
        modifier = Modifier
            .width(148.dp)
            .height(146.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        color = White,
        border = BorderStroke(1.dp, visuals.border),
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            visuals.cardStart,
                            visuals.cardEnd
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                visuals.iconPlateStart,
                                visuals.iconPlateEnd
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.94f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visuals.icon,
                        contentDescription = null,
                        tint = visuals.iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = category.name,
                color = TextPrimaryAlt,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            )

            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(visuals.accent.copy(alpha = 0.9f))
            )
        }
    }
}

@Composable
private fun HomeWorkerCard(
    worker: WorkerListItemData,
    avatarPainter: Painter?,
    isFavorite: Boolean,
    displayCategoryName: String? = null,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val provinceText = worker.province.ifBlank { "Costa Rica" }
    val categoryName = displayCategoryName
        ?.takeIf { it.isNotBlank() }
        ?: worker.categoryNames.firstOrNull()?.ifBlank { "Servicio" }
        ?: "Servicio"
    val accent = workerAccentColor(categoryName)

    Surface(
        modifier = Modifier
            .width(196.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = White,
        border = BorderStroke(1.dp, BorderSoftAlt),
        shadowElevation = 5.dp
    ) {
        Box {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(34.dp)
                    .clickable(onClick = onFavoriteClick),
                shape = CircleShape,
                color = if (isFavorite) BrandRed.copy(alpha = 0.10f) else White,
                border = BorderStroke(
                    1.dp,
                    if (isFavorite) BrandRed.copy(alpha = 0.18f) else BorderSoftAlt
                ),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = TablerIcons.Heart,
                        contentDescription = if (isFavorite) "Quitar favorito" else "Agregar favorito",
                        tint = if (isFavorite) BrandRed else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WorkerCircularPhoto(
                    imageUrl = worker.profilePictureLink,
                    avatarPainter = avatarPainter,
                    accent = accent
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = worker.name.ifBlank { "Profesional" },
                    color = TextPrimaryAlt,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                WorkerCategoryLabel(
                    text = categoryName,
                    accent = accent
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    WorkerStatPill(
                        text = "⭐ ${round(worker.stars * 10.0) / 10.0}"
                    )

                    WorkerStatPill(
                        text = provinceText,
                        leadingIcon = TablerIcons.MapPin
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (worker.startingPrice > 0.0) "Desde" else "Precio",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (worker.startingPrice > 0.0) {
                            formatPrice(worker.startingPrice)
                        } else {
                            "A consultar"
                        },
                        color = BrandBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkerCircularPhoto(
    imageUrl: String?,
    avatarPainter: Painter?,
    accent: Color
) {
    Box(
        modifier = Modifier.size(92.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.12f))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(White)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(ImageBlueSoftAlt),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        !imageUrl.isNullOrBlank() -> {
                            KamelImage(
                                resource = asyncPainterResource(imageUrl),
                                contentDescription = "Foto del trabajador",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        avatarPainter != null -> {
                            Image(
                                painter = avatarPainter,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        else -> {
                            Text(
                                text = "SV",
                                color = BrandBlue,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerCategoryLabel(
    text: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WorkerStatPill(
    text: String,
    leadingIcon: ImageVector? = null
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFFF8FAFD),
        border = BorderStroke(1.dp, BorderSoftAlt)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }

            Text(
                text = text,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun workerAccentColor(categoryName: String): Color {
    val key = categoryName.normalizedCategoryKey()

    return when {
        key.contains("barber") -> Color(0xFF2E6FD1)
        key.contains("belleza") -> Color(0xFFC026D3)
        key.contains("constru") || key.contains("remodel") -> Color(0xFFF59E0B)
        key.contains("hogar") -> Color(0xFF3B82F6)
        key.contains("automotr") -> Color(0xFF6366F1)
        key.contains("mascota") -> Color(0xFF22C55E)
        key.contains("electric") -> Color(0xFFF59E0B)
        key.contains("jardiner") -> Color(0xFF16A34A)
        key.contains("pintura") -> Color(0xFFF43F5E)
        key.contains("limpieza") -> Color(0xFF06B6D4)
        key.contains("educacion") -> Color(0xFF8B5CF6)
        key.contains("cuidado") || key.contains("acompan") -> Color(0xFFF43F5E)
        key.contains("deporte") || key.contains("entreten") -> Color(0xFFF59E0B)
        key.contains("diseno") || key.contains("creativ") -> Color(0xFF8B5CF6)
        else -> BrandBlueSoft
    }
}

@Composable
private fun HomeMessageCard(
    text: String,
    isError: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = White,
            border = BorderStroke(
                1.dp,
                if (isError) BrandRed.copy(alpha = 0.15f) else BorderSoftAlt
            ),
            shadowElevation = 2.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                color = if (isError) BrandRed else TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun workerBelongsToCategory(
    worker: WorkerListItemData,
    category: Category
): Boolean {
    return worker.categoryIds.any { it == category.id } ||
            worker.categoryNames.any { it.equals(category.name, ignoreCase = true) }
}

private fun categoryVisuals(name: String): HomeCategoryVisuals {
    val key = name.normalizedCategoryKey()

    return when {
        key.contains("barber") -> HomeCategoryVisuals(
            icon = TablerIcons.Scissors,
            cardStart = Color(0xFFF2F7FF),
            cardEnd = White,
            border = Color(0xFFD6E6FF),
            iconPlateStart = Color(0xFFDCEBFF),
            iconPlateEnd = Color(0xFFBDD7FF),
            iconTint = Color(0xFF1459C7),
            accent = Color(0xFF2E6FD1)
        )

        key.contains("belleza") -> HomeCategoryVisuals(
            icon = TablerIcons.Brush,
            cardStart = Color(0xFFFDF2FF),
            cardEnd = White,
            border = Color(0xFFECCFFF),
            iconPlateStart = Color(0xFFF3D7FF),
            iconPlateEnd = Color(0xFFFFD8EA),
            iconTint = Color(0xFF8B3DCC),
            accent = Color(0xFFC026D3)
        )

        key.contains("constru") || key.contains("remodel") -> HomeCategoryVisuals(
            icon = TablerIcons.Building,
            cardStart = Color(0xFFFFF7ED),
            cardEnd = White,
            border = Color(0xFFFFDFC2),
            iconPlateStart = Color(0xFFFFE2BE),
            iconPlateEnd = Color(0xFFFFC98B),
            iconTint = Color(0xFFB45309),
            accent = Color(0xFFF59E0B)
        )

        key.contains("hogar") -> HomeCategoryVisuals(
            icon = TablerIcons.Building,
            cardStart = Color(0xFFF2F8FF),
            cardEnd = White,
            border = Color(0xFFD9E8FF),
            iconPlateStart = Color(0xFFDCEBFF),
            iconPlateEnd = Color(0xFFC5DCFF),
            iconTint = Color(0xFF1D4ED8),
            accent = Color(0xFF3B82F6)
        )

        key.contains("automotr") -> HomeCategoryVisuals(
            icon = TablerIcons.Car,
            cardStart = Color(0xFFF5F7FB),
            cardEnd = White,
            border = Color(0xFFDCE3F0),
            iconPlateStart = Color(0xFFE0E7F5),
            iconPlateEnd = Color(0xFFC7D2FE),
            iconTint = Color(0xFF334155),
            accent = Color(0xFF6366F1)
        )

        key.contains("mascota") -> HomeCategoryVisuals(
            icon = TablerIcons.Camera,
            cardStart = Color(0xFFF0FDF4),
            cardEnd = White,
            border = Color(0xFFD2F5DC),
            iconPlateStart = Color(0xFFD9FBE3),
            iconPlateEnd = Color(0xFFB7F0C6),
            iconTint = Color(0xFF15803D),
            accent = Color(0xFF22C55E)
        )

        key.contains("electric") -> HomeCategoryVisuals(
            icon = TablerIcons.Plug,
            cardStart = Color(0xFFFFFBEB),
            cardEnd = White,
            border = Color(0xFFFDE7A8),
            iconPlateStart = Color(0xFFFDE68A),
            iconPlateEnd = Color(0xFFFCD34D),
            iconTint = Color(0xFFB45309),
            accent = Color(0xFFF59E0B)
        )

        key.contains("jardiner") -> HomeCategoryVisuals(
            icon = TablerIcons.Tree,
            cardStart = Color(0xFFF0FDF4),
            cardEnd = White,
            border = Color(0xFFD4F5DE),
            iconPlateStart = Color(0xFFCFF7DA),
            iconPlateEnd = Color(0xFF9FE3B4),
            iconTint = Color(0xFF166534),
            accent = Color(0xFF16A34A)
        )

        key.contains("pintura") -> HomeCategoryVisuals(
            icon = TablerIcons.Paint,
            cardStart = Color(0xFFFFF1F2),
            cardEnd = White,
            border = Color(0xFFFFD2D8),
            iconPlateStart = Color(0xFFFFD6DC),
            iconPlateEnd = Color(0xFFFFB8C3),
            iconTint = Color(0xFFBE123C),
            accent = Color(0xFFF43F5E)
        )

        key.contains("limpieza") -> HomeCategoryVisuals(
            icon = TablerIcons.Droplet,
            cardStart = Color(0xFFF0FDFF),
            cardEnd = White,
            border = Color(0xFFC9F1F7),
            iconPlateStart = Color(0xFFD3F8FD),
            iconPlateEnd = Color(0xFFB0EEF7),
            iconTint = Color(0xFF0F766E),
            accent = Color(0xFF06B6D4)
        )

        key.contains("educacion") -> HomeCategoryVisuals(
            icon = TablerIcons.Briefcase,
            cardStart = Color(0xFFF5F3FF),
            cardEnd = White,
            border = Color(0xFFE1D9FF),
            iconPlateStart = Color(0xFFE9E2FF),
            iconPlateEnd = Color(0xFFD3C5FF),
            iconTint = Color(0xFF6D28D9),
            accent = Color(0xFF8B5CF6)
        )

        key.contains("cuidado") || key.contains("acompan") -> HomeCategoryVisuals(
            icon = TablerIcons.Heart,
            cardStart = Color(0xFFFFF1F4),
            cardEnd = White,
            border = Color(0xFFFFD4DE),
            iconPlateStart = Color(0xFFFFD9E3),
            iconPlateEnd = Color(0xFFFFB9CA),
            iconTint = Color(0xFFE11D48),
            accent = Color(0xFFF43F5E)
        )

        key.contains("deporte") || key.contains("entreten") -> HomeCategoryVisuals(
            icon = TablerIcons.Camera,
            cardStart = Color(0xFFFFF8E8),
            cardEnd = White,
            border = Color(0xFFFFE7B0),
            iconPlateStart = Color(0xFFFFE9B8),
            iconPlateEnd = Color(0xFFFFD66B),
            iconTint = Color(0xFFB45309),
            accent = Color(0xFFF59E0B)
        )

        key.contains("diseno") || key.contains("creativ") -> HomeCategoryVisuals(
            icon = TablerIcons.Paint,
            cardStart = Color(0xFFF3F0FF),
            cardEnd = White,
            border = Color(0xFFE0D5FF),
            iconPlateStart = Color(0xFFE7DDFF),
            iconPlateEnd = Color(0xFFCDBBFF),
            iconTint = Color(0xFF7C3AED),
            accent = Color(0xFF8B5CF6)
        )

        else -> HomeCategoryVisuals(
            icon = TablerIcons.Apps,
            cardStart = Color(0xFFF4F8FF),
            cardEnd = White,
            border = Color(0xFFD9E7FF),
            iconPlateStart = Color(0xFFE3EEFF),
            iconPlateEnd = Color(0xFFCFE0FF),
            iconTint = BrandBlue,
            accent = BrandBlueSoft
        )
    }
}

private fun String.normalizedCategoryKey(): String {
    return this
        .trim()
        .lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ñ", "n")
}

private fun formatPrice(value: Double): String {
    val rounded = round(value * 100.0) / 100.0
    return if (rounded % 1.0 == 0.0) {
        "₡${rounded.toInt()}"
    } else {
        "₡$rounded"
    }
}