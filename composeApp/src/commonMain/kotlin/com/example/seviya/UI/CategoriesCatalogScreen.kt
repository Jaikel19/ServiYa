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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import compose.icons.tablericons.Bell
import compose.icons.tablericons.Bolt
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.Brush
import compose.icons.tablericons.Building
import compose.icons.tablericons.Bulb
import compose.icons.tablericons.Camera
import compose.icons.tablericons.Car
import compose.icons.tablericons.Droplet
import compose.icons.tablericons.Home
import compose.icons.tablericons.Login
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Paint
import compose.icons.tablericons.Plug
import compose.icons.tablericons.Scissors
import compose.icons.tablericons.Search
import compose.icons.tablericons.Snowflake
import compose.icons.tablericons.Tool
import compose.icons.tablericons.Tools
import compose.icons.tablericons.Tree
import compose.icons.tablericons.UserPlus
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults

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
    onGoHome: () -> Unit,
    onGoServices: () -> Unit,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onCategoryClick: (Category) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    CategoriesCatalogScreen(
        uiState = uiState,
        onGoHome = onGoHome,
        onGoServices = onGoServices,
        onGoLogin = onGoLogin,
        onGoRegister = onGoRegister,
        onCategoryClick = onCategoryClick
    )
}

@Composable
fun CategoriesCatalogScreen(
    uiState: CategoriesUiState,
    onGoHome: () -> Unit,
    onGoServices: () -> Unit,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onCategoryClick: (Category) -> Unit = {}
) {
    Scaffold(
        containerColor = CategoriesUI.Background,
        bottomBar = {
            CategoriesBottomBar(
                onGoHome = onGoHome,
                onGoServices = onGoServices,
                onGoLogin = onGoLogin,
                onGoRegister = onGoRegister
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
                                bottom = 120.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(
                                items = uiState.categories,
                                key = { it.id }
                            ) { category ->
                                CategoryCard(
                                    category = category,
                                    onClick = { onCategoryClick(category) }
                                )
                            }
                        }
                    }
                }
            }
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
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 24.dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 50.dp)
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
    onClick: () -> Unit
) {
    val visuals = categoryVisual(category.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CategoriesUI.CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(visuals.iconBackground.copy(alpha = 0.65f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(visuals.iconBackground)
                        .border(
                            width = 1.dp,
                            color = visuals.iconTint.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visuals.icon,
                        contentDescription = null,
                        tint = visuals.iconTint,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp
                    ),
                    color = CategoriesUI.TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Descubrir opciones",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    ),
                    color = CategoriesUI.TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(visuals.iconBackground)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ver opciones",
                        color = visuals.iconTint,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

private data class CategoryVisuals(
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color
)

private fun categoryVisual(name: String): CategoryVisuals {
    return when (name.normalizedCategoryKey()) {
        "plomeria" -> CategoryVisuals(
            icon = TablerIcons.Droplet,
            iconBackground = Color(0xFFE0F2FE),
            iconTint = Color(0xFF0284C7)
        )

        "electricidad" -> CategoryVisuals(
            icon = TablerIcons.Plug,
            iconBackground = Color(0xFFFFF7D6),
            iconTint = Color(0xFFEAB308)
        )

        "limpieza" -> CategoryVisuals(
            icon = TablerIcons.Brush,
            iconBackground = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A)
        )

        "pintura" -> CategoryVisuals(
            icon = TablerIcons.Paint,
            iconBackground = Color(0xFFF3E8FF),
            iconTint = Color(0xFF9333EA)
        )

        "climatizacion" -> CategoryVisuals(
            icon = TablerIcons.Snowflake,
            iconBackground = Color(0xFFCFFAFE),
            iconTint = Color(0xFF0891B2)
        )

        "jardineria" -> CategoryVisuals(
            icon = TablerIcons.Tree,
            iconBackground = Color(0xFFECFCCB),
            iconTint = Color(0xFF65A30D)
        )

        "albanileria" -> CategoryVisuals(
            icon = TablerIcons.Building,
            iconBackground = Color(0xFFFFEDD5),
            iconTint = Color(0xFFEA580C)
        )

        "carpinteria" -> CategoryVisuals(
            icon = TablerIcons.Tools,
            iconBackground = Color(0xFFE2E8F0),
            iconTint = Color(0xFF475569)
        )

        "automotriz" -> CategoryVisuals(
            icon = TablerIcons.Car,
            iconBackground = Color(0xFFFFE4E6),
            iconTint = Color(0xFFE11D48)
        )

        "fotografia" -> CategoryVisuals(
            icon = TablerIcons.Camera,
            iconBackground = Color(0xFFFCE7F3),
            iconTint = Color(0xFFDB2777)
        )

        "barberia" -> CategoryVisuals(
            icon = TablerIcons.Scissors,
            iconBackground = Color(0xFFEDE9FE),
            iconTint = Color(0xFF7C3AED)
        )

        "electricista" -> CategoryVisuals(
            icon = TablerIcons.Bulb,
            iconBackground = Color(0xFFFEF3C7),
            iconTint = Color(0xFFD97706)
        )

        else -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            iconBackground = Color(0xFFDBEAFE),
            iconTint = Color(0xFF2563EB)
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
private fun androidx.compose.foundation.layout.RowScope.CategoriesNavItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val color = if (active) CategoriesUI.Blue else Color(0xFF98A2B3)

    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (active) Color(0xFFE7EDF5) else Color.Transparent)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
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
    onGoHome: () -> Unit,
    onGoServices: () -> Unit,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .border(1.dp, Color(0xFFE9EDF2))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        CategoriesNavItem(
            label = "Inicio",
            icon = TablerIcons.Home,
            active = false,
            onClick = onGoHome
        )

        CategoriesNavItem(
            label = "Servicios",
            icon = TablerIcons.Search,
            active = true,
            onClick = onGoServices
        )

        CategoriesNavItem(
            label = "Ingresar",
            icon = TablerIcons.Login,
            active = false,
            onClick = onGoLogin
        )

        CategoriesNavItem(
            label = "Registro",
            icon = TablerIcons.UserPlus,
            active = false,
            onClick = onGoRegister
        )
    }
}
