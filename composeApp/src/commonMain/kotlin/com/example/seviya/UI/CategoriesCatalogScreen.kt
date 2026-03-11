package com.example.seviya.UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.entity.Category
import com.example.shared.presentation.categories.CategoriesUiState
import com.example.shared.presentation.categories.CategoriesViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

private object CategoriesUI {
    val Blue = Color(0xFF004AAD)
    val BlueDark = Color(0xFF022E6F)
    val BlueSoft = Color(0xFFEAF2FF)
    val Red = Color(0xFFEF4444)

    val Background = Color(0xFFF6F8FC)
    val BackgroundTop = Color(0xFFEFF5FF)

    val CardBorder = Color(0xFFE7ECF5)
    val CardShadow = Color(0x14000000)
    val Inactive = Color(0xFF97A3B6)

    val TextDark = Color(0xFF14213D)
    val TextMuted = Color(0xFF6B7280)
    val SubtitleOnBlue = Color(0xFFD7E3FF)
}

@Composable
fun CategoriesCatalogRoute(
    viewModel: CategoriesViewModel,
    onGoServices: () -> Unit,
    onGoMap: () -> Unit,
    onGoSearch: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoAgenda: () -> Unit,
    onGoProfile: () -> Unit,
    onGoConfiguration: () -> Unit,
    onGoMessages: () -> Unit,
    onGoDashboard: () -> Unit,
    onGoSettings: () -> Unit,
    onCategoryClick: (Category) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    CategoriesCatalogScreen(
        uiState = uiState,
        onGoServices = onGoServices,
        onGoMap = onGoMap,
        onGoSearch = onGoSearch,
        onGoAlerts = onGoAlerts,
        onGoAgenda = onGoAgenda,
        onGoProfile = onGoProfile,
        onGoConfiguration = onGoConfiguration,
        onGoMessages = onGoMessages,
        onGoDashboard = onGoDashboard,
        onGoSettings = onGoSettings,
        onCategoryClick = onCategoryClick
    )
}

@Composable
fun CategoriesCatalogScreen(
    uiState: CategoriesUiState,
    onGoServices: () -> Unit,
    onGoMap: () -> Unit,
    onGoSearch: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoAgenda: () -> Unit,
    onGoProfile: () -> Unit,
    onGoConfiguration: () -> Unit,
    onGoMessages: () -> Unit,
    onGoDashboard: () -> Unit,
    onGoSettings: () -> Unit,
    onCategoryClick: (Category) -> Unit = {}
) {
    val menuExpanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = CategoriesUI.Background,
            contentWindowInsets = WindowInsets.systemBars,
            bottomBar = {
                CategoriesBottomBar(
                    menuActive = menuExpanded.value,
                    onGoServices = onGoServices,
                    onGoMap = onGoMap,
                    onGoSearch = onGoSearch,
                    onGoAlerts = onGoAlerts,
                    onGoMenu = { menuExpanded.value = true }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CategoriesUI.BackgroundTop,
                                CategoriesUI.Background
                            )
                        )
                    )
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CategoriesHeader()

                    val errorMessage = uiState.errorMessage

                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = CategoriesUI.Blue,
                                    strokeWidth = 3.5.dp
                                )
                            }
                        }

                        errorMessage != null && uiState.categories.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(24.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, CategoriesUI.CardBorder),
                                    shadowElevation = 6.dp
                                ) {
                                    Text(
                                        text = errorMessage,
                                        modifier = Modifier.padding(20.dp),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = CategoriesUI.TextDark,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = 10.dp,
                                    bottom = 110.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(18.dp),
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                itemsIndexed(
                                    items = uiState.categories,
                                    key = { _, item -> item.id }
                                ) { index, category ->
                                    CategoryCard(
                                        category = category,
                                        index = index,
                                        onClick = { onCategoryClick(category) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = menuExpanded.value,
            enter = fadeIn(animationSpec = tween(220)) +
                    slideInVertically(
                        animationSpec = tween(280),
                        initialOffsetY = { it / 10 }
                    ),
            exit = fadeOut(animationSpec = tween(180)) +
                    slideOutVertically(
                        animationSpec = tween(220),
                        targetOffsetY = { it / 12 }
                    )
        ) {
            CategoriesFullScreenMenu(
                onDismiss = { menuExpanded.value = false },
                onGoAgenda = {
                    menuExpanded.value = false
                    onGoAgenda()
                },
                onGoProfile = {
                    menuExpanded.value = false
                    onGoProfile()
                },
                onGoConfiguration = {
                    menuExpanded.value = false
                    onGoConfiguration()
                },
                onGoMessages = {
                    menuExpanded.value = false
                    onGoMessages()
                },
                onGoDashboard = {
                    menuExpanded.value = false
                    onGoDashboard()
                },
                onGoSettings = {
                    menuExpanded.value = false
                    onGoSettings()
                }
            )
        }
    }
}

@Composable
private fun CategoriesHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CategoriesUI.Blue,
                        CategoriesUI.BlueDark
                    )
                )
            )
    ) {
        DecorativeHeaderBubbles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandLogo()
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Encuentra el servicio\nideal para ti",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    lineHeight = 36.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Explora categorías y conecta con profesionales confiables cerca de ti.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 21.sp
                ),
                color = CategoriesUI.SubtitleOnBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = TablerIcons.Search,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Selecciona una categoría para comenzar",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        HeaderWave(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(58.dp)
        )
    }
}

@Composable
private fun BrandLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.MapPin,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) { append("Servi") }

                withStyle(
                    SpanStyle(
                        color = CategoriesUI.Red,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) { append("Ya") }
            },
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
        )
    }
}

@Composable
private fun DecorativeHeaderBubbles() {
    val infinite = rememberInfiniteTransition(label = "headerBubbles")

    val bubbleTopOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleTopOffset"
    )

    val bubbleMiddleOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleMiddleOffset"
    )

    val bubbleBottomOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleBottomOffset"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 24.dp)
                .offset(y = bubbleTopOffset.dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .offset(y = bubbleMiddleOffset.dp)
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 50.dp)
                .offset(y = bubbleBottomOffset.dp)
                .size(90.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
    }
}

@Composable
private fun HeaderWave(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(0f, h * 0.30f)
            quadraticBezierTo(
                w * 0.22f,
                h * 1.05f,
                w * 0.50f,
                h * 0.55f
            )
            quadraticBezierTo(
                w * 0.78f,
                0f,
                w,
                h * 0.35f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = path,
            color = CategoriesUI.Background
        )
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    index: Int,
    onClick: () -> Unit
) {
    val visuals = categoryVisual(category.id)

    val entered = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 55L)
        entered.value = true
    }

    val cardAlpha by animateFloatAsState(
        targetValue = if (entered.value) 1f else 0f,
        animationSpec = tween(450),
        label = "cardAlpha"
    )

    val cardTranslationY by animateFloatAsState(
        targetValue = if (entered.value) 0f else 42f,
        animationSpec = tween(500),
        label = "cardTranslationY"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (entered.value) 1f else 0.95f,
        animationSpec = tween(500),
        label = "cardScale"
    )

    val infinite = rememberInfiniteTransition(label = "categoryCard")

    val bubbleScale by infinite.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleScale"
    )

    val iconFloat by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconFloat"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(214.dp)
            .graphicsLayer {
                alpha = cardAlpha
                translationY = cardTranslationY
                scaleX = cardScale
                scaleY = cardScale
            }
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(30.dp),
                clip = false
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CategoriesUI.CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            visuals.cardTint,
                            Color.White
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                visuals.accent,
                                visuals.accentSecondary
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .size(42.dp)
                    .graphicsLayer {
                        scaleX = bubbleScale
                        scaleY = bubbleScale
                    }
                    .clip(CircleShape)
                    .background(visuals.accent.copy(alpha = 0.08f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = iconFloat
                            }
                            .size(66.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        visuals.iconPlateStart,
                                        visuals.iconPlateEnd
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = visuals.accent.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(visuals.iconInner),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = visuals.icon,
                                contentDescription = null,
                                tint = visuals.iconTint,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = CategoriesUI.TextDark
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Explora profesionales",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        ),
                        color = CategoriesUI.TextMuted
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(50.dp))
                        .background(visuals.buttonBackground)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ver opciones",
                        color = visuals.buttonText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = TablerIcons.ChevronRight,
                        contentDescription = null,
                        tint = visuals.buttonText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private data class CategoryVisuals(
    val icon: ImageVector,
    val accent: Color,
    val accentSecondary: Color,
    val cardTint: Color,
    val iconPlateStart: Color,
    val iconPlateEnd: Color,
    val iconInner: Color,
    val iconTint: Color,
    val buttonBackground: Color,
    val buttonText: Color
)

private fun categoryVisual(categoryId: String): CategoryVisuals {
    return when (categoryId.normalizedCategoryKey()) {
        "automotriz" -> CategoryVisuals(
            icon = TablerIcons.Car,
            accent = Color(0xFFE25578),
            accentSecondary = Color(0xFFF08CA1),
            cardTint = Color(0xFFFFF6F8),
            iconPlateStart = Color(0xFFFFE0E8),
            iconPlateEnd = Color(0xFFFFCFDA),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFD24166),
            buttonBackground = Color(0xFFFFEAF0),
            buttonText = Color(0xFFD24166)
        )

        "barberia" -> CategoryVisuals(
            icon = TablerIcons.Scissors,
            accent = Color(0xFF6C63D9),
            accentSecondary = Color(0xFF9A92F2),
            cardTint = Color(0xFFF8F7FF),
            iconPlateStart = Color(0xFFE5E2FF),
            iconPlateEnd = Color(0xFFD8D3FF),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF5C54C7),
            buttonBackground = Color(0xFFEFEDFF),
            buttonText = Color(0xFF5C54C7)
        )

        "belleza" -> CategoryVisuals(
            icon = TablerIcons.Scissors,
            accent = Color(0xFFD85D9E),
            accentSecondary = Color(0xFFF1A5C7),
            cardTint = Color(0xFFFFF7FB),
            iconPlateStart = Color(0xFFF9E1EC),
            iconPlateEnd = Color(0xFFF4CFE0),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFBF4B89),
            buttonBackground = Color(0xFFFDEAF3),
            buttonText = Color(0xFFBF4B89)
        )

        "construccion-y-remodelacion" -> CategoryVisuals(
            icon = TablerIcons.Building,
            accent = Color(0xFFD66A4A),
            accentSecondary = Color(0xFFF0A07E),
            cardTint = Color(0xFFFFF7F4),
            iconPlateStart = Color(0xFFFFE5DB),
            iconPlateEnd = Color(0xFFFFD3C4),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFC15739),
            buttonBackground = Color(0xFFFFEEE7),
            buttonText = Color(0xFFC15739)
        )

        "cuidado-y-acompanamiento" -> CategoryVisuals(
            icon = TablerIcons.User,
            accent = Color(0xFF8C7AE6),
            accentSecondary = Color(0xFFB6A7F6),
            cardTint = Color(0xFFF9F7FF),
            iconPlateStart = Color(0xFFEAE5FF),
            iconPlateEnd = Color(0xFFDDD5FF),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF7564D6),
            buttonBackground = Color(0xFFF1EDFF),
            buttonText = Color(0xFF7564D6)
        )

        "deportes-y-entrenamiento" -> CategoryVisuals(
            icon = TablerIcons.Apps,
            accent = Color(0xFF3FAE74),
            accentSecondary = Color(0xFF84D3A1),
            cardTint = Color(0xFFF4FCF7),
            iconPlateStart = Color(0xFFDFF5E6),
            iconPlateEnd = Color(0xFFCDEED9),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF2F8E5A),
            buttonBackground = Color(0xFFEAF7EF),
            buttonText = Color(0xFF2F8E5A)
        )

        "diseno-y-creatividad" -> CategoryVisuals(
            icon = TablerIcons.Paint,
            accent = Color(0xFF9B5DE5),
            accentSecondary = Color(0xFFC89AF4),
            cardTint = Color(0xFFFBF7FF),
            iconPlateStart = Color(0xFFF0E2FF),
            iconPlateEnd = Color(0xFFE4D0FF),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF8347C8),
            buttonBackground = Color(0xFFF4EBFF),
            buttonText = Color(0xFF8347C8)
        )

        "educacion" -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            accent = Color(0xFF4B8FD8),
            accentSecondary = Color(0xFF8EC0F5),
            cardTint = Color(0xFFF5FAFF),
            iconPlateStart = Color(0xFFE2F0FF),
            iconPlateEnd = Color(0xFFD2E7FF),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF3B78BC),
            buttonBackground = Color(0xFFEBF5FF),
            buttonText = Color(0xFF3B78BC)
        )

        "electricidad" -> CategoryVisuals(
            icon = TablerIcons.Plug,
            accent = Color(0xFFE2B100),
            accentSecondary = Color(0xFFF0C94D),
            cardTint = Color(0xFFFFFCF1),
            iconPlateStart = Color(0xFFFFF4C6),
            iconPlateEnd = Color(0xFFFFEAA2),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFB88900),
            buttonBackground = Color(0xFFFFF6D8),
            buttonText = Color(0xFFB88900)
        )

        "eventos" -> CategoryVisuals(
            icon = TablerIcons.CalendarEvent,
            accent = Color(0xFFE06C5F),
            accentSecondary = Color(0xFFF3A297),
            cardTint = Color(0xFFFFF7F5),
            iconPlateStart = Color(0xFFFFE5E0),
            iconPlateEnd = Color(0xFFFFD5CE),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFC85A4E),
            buttonBackground = Color(0xFFFFEEE9),
            buttonText = Color(0xFFC85A4E)
        )

        "fotografia-y-video" -> CategoryVisuals(
            icon = TablerIcons.Camera,
            accent = Color(0xFFC95C9A),
            accentSecondary = Color(0xFFE59BC1),
            cardTint = Color(0xFFFFF7FB),
            iconPlateStart = Color(0xFFF8E0EC),
            iconPlateEnd = Color(0xFFF3D1E2),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFB24A86),
            buttonBackground = Color(0xFFFBEAF3),
            buttonText = Color(0xFFB24A86)
        )

        "gastronomia" -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            accent = Color(0xFFE08A3A),
            accentSecondary = Color(0xFFF5B26B),
            cardTint = Color(0xFFFFFAF5),
            iconPlateStart = Color(0xFFFFECD8),
            iconPlateEnd = Color(0xFFFFDFC0),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFC97528),
            buttonBackground = Color(0xFFFFF1E3),
            buttonText = Color(0xFFC97528)
        )

        "hogar" -> CategoryVisuals(
            icon = TablerIcons.Building,
            accent = Color(0xFF5D8AA8),
            accentSecondary = Color(0xFF9BBFD2),
            cardTint = Color(0xFFF6FBFD),
            iconPlateStart = Color(0xFFE4F1F7),
            iconPlateEnd = Color(0xFFD5E8F2),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF4E7691),
            buttonBackground = Color(0xFFEDF6FA),
            buttonText = Color(0xFF4E7691)
        )

        "idiomas" -> CategoryVisuals(
            icon = TablerIcons.Globe,
            accent = Color(0xFF2FA7B7),
            accentSecondary = Color(0xFF79D1DB),
            cardTint = Color(0xFFF3FCFD),
            iconPlateStart = Color(0xFFDFF7FA),
            iconPlateEnd = Color(0xFFCBEFF4),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF248C99),
            buttonBackground = Color(0xFFE9F8FA),
            buttonText = Color(0xFF248C99)
        )

        "jardineria" -> CategoryVisuals(
            icon = TablerIcons.Tree,
            accent = Color(0xFF72A83B),
            accentSecondary = Color(0xFFA2C96A),
            cardTint = Color(0xFFF8FDF3),
            iconPlateStart = Color(0xFFE8F5D4),
            iconPlateEnd = Color(0xFFD9EFB8),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF5F8E2F),
            buttonBackground = Color(0xFFF0F7E2),
            buttonText = Color(0xFF5F8E2F)
        )

        "limpieza" -> CategoryVisuals(
            icon = TablerIcons.Brush,
            accent = Color(0xFF38A169),
            accentSecondary = Color(0xFF68C08F),
            cardTint = Color(0xFFF4FCF7),
            iconPlateStart = Color(0xFFDDF4E6),
            iconPlateEnd = Color(0xFFCDEDD9),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF2F855A),
            buttonBackground = Color(0xFFEAF7EF),
            buttonText = Color(0xFF2F855A)
        )

        "mascotas" -> CategoryVisuals(
            icon = TablerIcons.User,
            accent = Color(0xFF4CB5AE),
            accentSecondary = Color(0xFF8ADAD1),
            cardTint = Color(0xFFF4FCFB),
            iconPlateStart = Color(0xFFDDF6F3),
            iconPlateEnd = Color(0xFFCDEEE9),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF379A93),
            buttonBackground = Color(0xFFEAF8F6),
            buttonText = Color(0xFF379A93)
        )

        "moda-y-confeccion" -> CategoryVisuals(
            icon = TablerIcons.Scissors,
            accent = Color(0xFFD86A9A),
            accentSecondary = Color(0xFFF0A9C7),
            cardTint = Color(0xFFFFF7FB),
            iconPlateStart = Color(0xFFF9E2EC),
            iconPlateEnd = Color(0xFFF3D1E0),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFBF5686),
            buttonBackground = Color(0xFFFDEBF3),
            buttonText = Color(0xFFBF5686)
        )

        "mudanzas-y-transporte" -> CategoryVisuals(
            icon = TablerIcons.Car,
            accent = Color(0xFFE07A3A),
            accentSecondary = Color(0xFFF4B178),
            cardTint = Color(0xFFFFF8F3),
            iconPlateStart = Color(0xFFFFE8D8),
            iconPlateEnd = Color(0xFFFFD9C3),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFFC9672C),
            buttonBackground = Color(0xFFFFF0E5),
            buttonText = Color(0xFFC9672C)
        )

        "musica-y-arte" -> CategoryVisuals(
            icon = TablerIcons.Paint,
            accent = Color(0xFF8F67D8),
            accentSecondary = Color(0xFFC0A2F0),
            cardTint = Color(0xFFF9F7FF),
            iconPlateStart = Color(0xFFEAE3FF),
            iconPlateEnd = Color(0xFFDDD2FF),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF7852C1),
            buttonBackground = Color(0xFFF1EDFF),
            buttonText = Color(0xFF7852C1)
        )

        "salud-y-bienestar" -> CategoryVisuals(
            icon = TablerIcons.User,
            accent = Color(0xFF49A99A),
            accentSecondary = Color(0xFF8AD5C8),
            cardTint = Color(0xFFF4FCFA),
            iconPlateStart = Color(0xFFDDF5F0),
            iconPlateEnd = Color(0xFFCAEDE5),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF358C7F),
            buttonBackground = Color(0xFFEAF8F4),
            buttonText = Color(0xFF358C7F)
        )

        "tecnologia" -> CategoryVisuals(
            icon = TablerIcons.Settings,
            accent = Color(0xFF5B7CFA),
            accentSecondary = Color(0xFF93A8FF),
            cardTint = Color(0xFFF6F8FF),
            iconPlateStart = Color(0xFFE4E9FF),
            iconPlateEnd = Color(0xFFD5DDFF),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF4968DE),
            buttonBackground = Color(0xFFEDF1FF),
            buttonText = Color(0xFF4968DE)
        )

        "tramites-y-gestiones" -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            accent = Color(0xFF6B7A99),
            accentSecondary = Color(0xFFA1AEC8),
            cardTint = Color(0xFFF7F9FC),
            iconPlateStart = Color(0xFFE8EDF5),
            iconPlateEnd = Color(0xFFDCE4F0),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF596780),
            buttonBackground = Color(0xFFEEF2F7),
            buttonText = Color(0xFF596780)
        )

        "plomeria" -> CategoryVisuals(
            icon = TablerIcons.Droplet,
            accent = Color(0xFF16A6B6),
            accentSecondary = Color(0xFF4FC3D9),
            cardTint = Color(0xFFF2FCFD),
            iconPlateStart = Color(0xFFDDF7FA),
            iconPlateEnd = Color(0xFFC8EFF4),
            iconInner = Color.White.copy(alpha = 0.92f),
            iconTint = Color(0xFF138A99),
            buttonBackground = Color(0xFFE8F8FA),
            buttonText = Color(0xFF138A99)
        )

        else -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            accent = Color(0xFF6B7A99),
            accentSecondary = Color(0xFFA1AEC8),
            cardTint = Color(0xFFF7F9FC),
            iconPlateStart = Color(0xFFE8EDF5),
            iconPlateEnd = Color(0xFFDCE4F0),
            iconInner = Color.White.copy(alpha = 0.94f),
            iconTint = Color(0xFF596780),
            buttonBackground = Color(0xFFEEF2F7),
            buttonText = Color(0xFF596780)
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

@Composable
private fun RowScope.BottomItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val color = if (active) CategoriesUI.Blue else Color(0xFF98A2B3)
    val bg = if (active) Color(0xFFEAF1FB) else Color.Transparent

    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(bg)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        label = {
            Text(
                text = label,
                color = color,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium
                )
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun CategoriesBottomBar(
    menuActive: Boolean,
    onGoServices: () -> Unit,
    onGoMap: () -> Unit,
    onGoSearch: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoMenu: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .border(1.dp, Color(0xFFE9EDF2))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        BottomItem(
            label = "Servicios",
            icon = TablerIcons.Apps,
            active = !menuActive,
            onClick = onGoServices
        )

        BottomItem(
            label = "Mapa",
            icon = TablerIcons.Globe,
            active = false,
            onClick = onGoMap
        )

        BottomItem(
            label = "Buscar",
            icon = TablerIcons.Search,
            active = false,
            onClick = onGoSearch
        )

        BottomItem(
            label = "Alertas",
            icon = TablerIcons.Bell,
            active = false,
            onClick = onGoAlerts
        )

        BottomItem(
            label = "Menú",
            icon = TablerIcons.Menu2,
            active = menuActive,
            onClick = onGoMenu
        )
    }
}

@Composable
private fun CategoriesFullScreenMenu(
    onDismiss: () -> Unit,
    onGoAgenda: () -> Unit,
    onGoProfile: () -> Unit,
    onGoConfiguration: () -> Unit,
    onGoMessages: () -> Unit,
    onGoDashboard: () -> Unit,
    onGoSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF7F7F8)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F7F8))
                    .padding(start = 10.dp, end = 18.dp, top = 10.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.ChevronLeft,
                        contentDescription = null,
                        tint = CategoriesUI.TextDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Menú",
                    color = Color(0xFF1F1F1F),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        letterSpacing = 0.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 6.dp, bottom = 20.dp)
            ) {
                SettingsStyleMenuItem(
                    icon = TablerIcons.CalendarEvent,
                    iconColor = Color(0xFF5FA8D3),
                    title = "Agenda",
                    subtitle = "Citas, disponibilidad y calendario",
                    onClick = onGoAgenda
                )

                SettingsListDivider()
                MenuSectionSpacer()

                SettingsStyleMenuItem(
                    icon = TablerIcons.User,
                    iconColor = Color(0xFF8E7CC3),
                    title = "Perfil",
                    subtitle = "Datos personales e información pública",
                    onClick = onGoProfile
                )

                SettingsListDivider()
                MenuSectionSpacer()

                SettingsStyleMenuItem(
                    icon = TablerIcons.Message,
                    iconColor = Color(0xFF67B99A),
                    title = "Mensajes",
                    subtitle = "Chats y conversaciones con clientes",
                    onClick = onGoMessages
                )

                SettingsListDivider()
                MenuSectionSpacer()

                SettingsStyleMenuItem(
                    icon = TablerIcons.Dashboard,
                    iconColor = Color(0xFFE29C7A),
                    title = "Dashboard",
                    subtitle = "Resumen general, métricas y actividad",
                    onClick = onGoDashboard
                )

                SettingsListDivider()
                MenuSectionSpacer()

                SettingsStyleMenuItem(
                    icon = TablerIcons.Settings,
                    iconColor = Color(0xFF9BB85D),
                    title = "Configuración",
                    subtitle = "Opciones principales de la aplicación",
                    onClick = onGoConfiguration
                )

                SettingsListDivider()
                MenuSectionSpacer()

                SettingsStyleMenuItem(
                    icon = TablerIcons.Adjustments,
                    iconColor = Color(0xFFD7B85A),
                    title = "Ajustes",
                    subtitle = "Preferencias, personalización y control",
                    onClick = onGoSettings
                )
            }
        }
    }
}

@Composable
private fun SettingsStyleMenuItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(54.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color(0xFF202124),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    letterSpacing = 0.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color(0xFF8A8F98),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
            )
        }
    }
}

@Composable
private fun SettingsListDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 72.dp, end = 18.dp)
            .height(1.dp)
            .background(Color(0xFFE9E9EC))
    )
}

@Composable
private fun MenuSectionSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
    )
}