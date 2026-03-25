package com.example.seviya.feature.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AppBackground
import com.example.seviya.core.designsystem.theme.AvatarBlueSoft
import com.example.seviya.core.designsystem.theme.BlueGrayText
import com.example.seviya.core.designsystem.theme.BlueGrayTextDark
import com.example.seviya.core.designsystem.theme.BlueGrayTextDarkAlt
import com.example.seviya.core.designsystem.theme.BlueGrayTextLight
import com.example.seviya.core.designsystem.theme.BlueGrayTextLighter
import com.example.seviya.core.designsystem.theme.BlueGrayTextSoft
import com.example.seviya.core.designsystem.theme.BlueTextMuted
import com.example.seviya.core.designsystem.theme.BorderBlueLight
import com.example.seviya.core.designsystem.theme.BorderBlueLightAlt
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BorderSoftAlt
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.BrandYellow
import com.example.seviya.core.designsystem.theme.DividerSoft
import com.example.seviya.core.designsystem.theme.ImageBlueSoft
import com.example.seviya.core.designsystem.theme.SoftBlueSurfaceAlt
import com.example.seviya.core.designsystem.theme.SoftSurface
import com.example.seviya.core.designsystem.theme.TextMuted
import com.example.seviya.core.designsystem.theme.TextPrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.PortfolioItem
import com.example.shared.domain.entity.ProfessionalProfileData
import com.example.shared.domain.entity.Service
import com.example.shared.domain.entity.WorkerReviewItem
import com.example.shared.domain.entity.WorkerSchedule
import com.example.shared.presentation.professionalProfile.ProfessionalProfileUiState
import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.round

private enum class ProfileTab {
    INFO, SERVICES, REVIEWS, PORTFOLIO
}

private enum class ReviewFilterType {
    ALL, FIVE, FOUR, THREE
}

@Composable
fun ProfessionalProfileRoute(
    clientId: String,
    workerId: String,
    avatarPainter: Painter? = null,
    onBack: () -> Unit = {},
    onProcessAppointment: (ProfessionalProfileData, List<Service>, List<Appointment>) -> Unit = { _, _, _ -> },
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {}
) {
    val viewModel: ProfessionalProfileViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(workerId, clientId) {
        viewModel.loadProfile(workerId)
        viewModel.loadFavoriteStatus(clientId, workerId)
    }

    ProfessionalProfileScreen(
        avatarPainter = avatarPainter,
        state = state,
        onBack = onBack,
        onFavoriteClick = {
            viewModel.toggleFavorite(clientId, workerId)
        },
        onProcessAppointment = onProcessAppointment,
        onBottomServices = onBottomServices,
        onBottomMap = onBottomMap,
        onBottomSearch = onBottomSearch,
        onBottomNotifications = onBottomNotifications,
        onBottomMenu = onBottomMenu
    )
}

@Composable
fun ProfessionalProfileScreen(
    modifier: Modifier = Modifier,
    avatarPainter: Painter? = null,
    state: ProfessionalProfileUiState,
    onBack: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onProcessAppointment: (ProfessionalProfileData, List<Service>, List<Appointment>) -> Unit = { _, _, _ -> },
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {}
) {
    val profile = state.profile
    var selectedTab by remember { mutableStateOf(ProfileTab.INFO) }

    var selectedServiceIds by remember { mutableStateOf(setOf<String>()) }
    var expandedServiceIds by remember { mutableStateOf(setOf<String>()) }

    val availableServices = profile?.services ?: emptyList()
    val selectedServices = availableServices.filter { it.id in selectedServiceIds }
    val selectedServicesCount = selectedServices.size
    val selectedServicesTotal = selectedServices.fold(0.0) { acc, service -> acc + service.cost }

    val showProcessAppointmentButton =
        selectedTab == ProfileTab.SERVICES &&
                selectedServices.isNotEmpty() &&
                profile != null

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBlue,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBlue)
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderSection(
                    avatarPainter = avatarPainter,
                    onBack = onBack,
                    onFavoriteClick = onFavoriteClick,
                    isFavorite = state.isFavorite,
                    profile = profile
                )

                Surface(
                    color = AppBackground,
                    shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-18).dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                                .padding(bottom = if (showProcessAppointmentButton) 170.dp else 100.dp)
                        ) {
                            when {
                                state.isLoading -> {
                                    Text(
                                        text = "Cargando perfil...",
                                        color = TextSecondary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                state.errorMessage != null -> {
                                    Text(
                                        text = "Error: ${state.errorMessage}",
                                        color = BrandRed,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                profile == null -> {
                                    Text(
                                        text = "No se encontró la información del profesional.",
                                        color = TextSecondary,
                                        fontSize = 15.sp
                                    )
                                }

                                else -> {
                                    val descriptionText =
                                        profile.description.takeIf { it.isNotBlank() }
                                            ?: "Profesional verificado dentro de la plataforma."

                                    Text(
                                        text = descriptionText,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 15.sp,
                                            lineHeight = 20.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = BlueTextMuted
                                        )
                                    )

                                    Spacer(Modifier.height(14.dp))

                                    if (profile.categoryNames.isNotEmpty()) {
                                        CategoryChips(profile.categoryNames)
                                        Spacer(Modifier.height(18.dp))
                                    }

                                    ProfileTabs(
                                        selectedTab = selectedTab,
                                        onTabSelected = { selectedTab = it }
                                    )

                                    Spacer(Modifier.height(16.dp))

                                    when (selectedTab) {
                                        ProfileTab.INFO -> {
                                            CancellationPoliciesCard(
                                                policy = profile.cancellationPolicy
                                            )

                                            Spacer(Modifier.height(16.dp))

                                            ProfileScheduleCard(
                                                schedule = profile.schedule
                                            )
                                        }

                                        ProfileTab.SERVICES -> {
                                            ServicesSelectionSection(
                                                services = profile.services,
                                                selectedServiceIds = selectedServiceIds,
                                                expandedServiceIds = expandedServiceIds,
                                                onToggleSelected = { serviceId ->
                                                    selectedServiceIds =
                                                        if (serviceId in selectedServiceIds) {
                                                            selectedServiceIds - serviceId
                                                        } else {
                                                            selectedServiceIds + serviceId
                                                        }
                                                },
                                                onToggleExpanded = { serviceId ->
                                                    expandedServiceIds =
                                                        if (serviceId in expandedServiceIds) {
                                                            expandedServiceIds - serviceId
                                                        } else {
                                                            expandedServiceIds + serviceId
                                                        }
                                                }
                                            )
                                        }

                                        ProfileTab.REVIEWS -> {
                                            ReviewsSection(
                                                reviews = profile.reviews
                                            )
                                        }

                                        ProfileTab.PORTFOLIO -> {
                                            PortfolioSection(
                                                portfolios = profile.portfolios,
                                                categoryLabel = profile.categoryNames.firstOrNull()
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(20.dp))
                                }
                            }
                        }

                        if (showProcessAppointmentButton && profile != null) {
                            ProcessAppointmentBar(
                                selectedCount = selectedServicesCount,
                                total = selectedServicesTotal,
                                onClick = { onProcessAppointment(profile, selectedServices, state.workerAppointments) },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    avatarPainter: Painter?,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean,
    profile: ProfessionalProfileData?
) {
    val name = profile?.name?.takeIf { it.isNotBlank() } ?: "Profesional"
    val subtitle = profile?.locationProvince?.takeIf { it.isNotBlank() }
        ?: "Ubicación no disponible"
    val profession = profile?.categoryNames?.firstOrNull()?.uppercase() ?: "SERVICIO"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 2.dp, bottom = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        ) {
            SquareGlassButton(
                onClick = onBack,
                content = {
                    Icon(
                        imageVector = TablerIcons.ChevronRight,
                        contentDescription = "Volver",
                        tint = White,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(180f)
                    )
                }
            )

            FavoriteGlassButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(
                imageUrl = profile?.profilePictureLink,
                avatarPainter = avatarPainter
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = name,
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = TablerIcons.MapPin,
                    contentDescription = null,
                    tint = White.copy(alpha = 0.88f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = subtitle,
                    color = White.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RatingPill(rating = profile?.stars ?: 0.0)
                Spacer(Modifier.width(8.dp))
                ProfessionPill(text = profession)
            }
        }
    }
}

@Composable
private fun SquareGlassButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(White.copy(alpha = 0.12f))
            .border(
                width = 1.5.dp,
                color = White.copy(alpha = 0.16f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun FavoriteGlassButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isFavorite) White.copy(alpha = 0.18f)
                else White.copy(alpha = 0.12f)
            )
            .border(
                width = 1.5.dp,
                color = if (isFavorite) {
                    BrandRed.copy(alpha = 0.55f)
                } else {
                    White.copy(alpha = 0.16f)
                },
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = TablerIcons.Heart,
            contentDescription = if (isFavorite) {
                "Quitar de favoritos"
            } else {
                "Agregar a favoritos"
            },
            tint = if (isFavorite) BrandRed else White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ServicesSelectionSection(
    services: List<Service>,
    selectedServiceIds: Set<String>,
    expandedServiceIds: Set<String>,
    onToggleSelected: (String) -> Unit,
    onToggleExpanded: (String) -> Unit
) {
    Column {
        Column {
            Text(
                text = "Servicios Disponibles",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "Selecciona los que deseas incluir en tu cita",
                color = TextSecondary.copy(alpha = 0.82f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(12.dp))

        if (services.isEmpty()) {
            Text(
                text = "Este profesional no tiene servicios registrados.",
                color = TextSecondary,
                fontSize = 14.sp
            )
        } else {
            services.forEachIndexed { index, service ->
                SelectableServiceCard(
                    service = service,
                    selected = service.id in selectedServiceIds,
                    expanded = service.id in expandedServiceIds,
                    onToggleSelected = { onToggleSelected(service.id) },
                    onToggleExpanded = { onToggleExpanded(service.id) }
                )

                if (index < services.lastIndex) {
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
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
private fun SelectableServiceCard(
    service: Service,
    selected: Boolean,
    expanded: Boolean,
    onToggleSelected: () -> Unit,
    onToggleExpanded: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFF7FAFF) else White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) BrandBlue.copy(alpha = 0.35f) else BorderSoftAlt
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 1.dp else 1.5.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpanded)
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ServiceSelectorBox(
                    selected = selected,
                    onClick = onToggleSelected
                )

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = service.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = TablerIcons.Clock,
                            contentDescription = null,
                            tint = BlueGrayText,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = service.duration,
                            color = BlueGrayText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formatPrice(service.cost),
                        color = BrandBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selected) BrandBlue.copy(alpha = 0.10f)
                                else Color(0xFFF4F7FA)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = TablerIcons.ChevronDown,
                            contentDescription = if (expanded) "Contraer" else "Expandir",
                            tint = BlueGrayTextSoft,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer {
                                    rotationZ = if (expanded) 180f else 0f
                                }
                        )
                    }
                }
            }

            if (expanded && service.description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))

                Divider(
                    color = DividerSoft,
                    thickness = 1.dp
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = service.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun ServiceSelectorBox(
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) BrandBlue else Color.Transparent)
            .border(
                width = if (selected) 0.dp else 2.dp,
                color = if (selected) Color.Transparent else BorderBlueLightAlt,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = TablerIcons.Check,
                contentDescription = "Seleccionado",
                tint = White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ProcessAppointmentBar(
    selectedCount: Int,
    total: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(18.dp, RoundedCornerShape(26.dp), clip = false)
            .clickable(onClick = onClick),
        color = BrandBlue,
        shape = RoundedCornerShape(26.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedCount.toString(),
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(Modifier.width(14.dp))

                Text(
                    text = "Procesar cita",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = formatPrice(total),
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    imageUrl: String?,
    avatarPainter: Painter?
) {
    Box(
        modifier = Modifier.size(126.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.22f))
                .border(
                    width = 2.dp,
                    color = White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.14f))
                    .border(
                        width = 1.5.dp,
                        color = White.copy(alpha = 0.30f),
                        shape = CircleShape
                    ),
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
                                    Text(
                                        text = "👷",
                                        fontSize = 48.sp
                                    )
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
                        Text(
                            text = "👷",
                            fontSize = 48.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingPill(rating: Double) {
    val ratingText = remember(rating) {
        val rounded = round(rating * 10) / 10.0
        if (rounded % 1.0 == 0.0) "${rounded.toInt()}.0" else rounded.toString()
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BrandYellow)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = TablerIcons.Star,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = ratingText,
            color = TextPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ProfessionPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(White.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileTabs(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileTabItem(
                title = "Información",
                selected = selectedTab == ProfileTab.INFO,
                onClick = { onTabSelected(ProfileTab.INFO) }
            )
            ProfileTabItem(
                title = "Servicios",
                selected = selectedTab == ProfileTab.SERVICES,
                onClick = { onTabSelected(ProfileTab.SERVICES) }
            )
            ProfileTabItem(
                title = "Reseñas",
                selected = selectedTab == ProfileTab.REVIEWS,
                onClick = { onTabSelected(ProfileTab.REVIEWS) }
            )
            ProfileTabItem(
                title = "Portafolio",
                selected = selectedTab == ProfileTab.PORTFOLIO,
                onClick = { onTabSelected(ProfileTab.PORTFOLIO) }
            )
        }

        Spacer(Modifier.height(12.dp))

        Divider(color = BorderSoft, thickness = 1.dp)
    }
}

@Composable
private fun ProfileTabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = if (selected) BrandBlue else TextMuted,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .height(3.dp)
                .width(94.dp)
                .background(
                    if (selected) BrandBlue else Color.Transparent
                )
        )
    }
}

@Composable
private fun CancellationPoliciesCard(
    policy: CancellationPolicy?
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        border = BorderStroke(1.dp, BorderSoft),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = TablerIcons.ShieldCheck,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Políticas de cancelación",
                    color = BrandBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            if (policy == null) {
                Text(
                    text = "No hay políticas de cancelación registradas.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            } else {
                BulletLine("Más de 7 días antes: ${policy.before7DaysOrMore}%")
                Spacer(Modifier.height(14.dp))
                BulletLine("Entre 3 y 6 días antes: ${policy.between3And6Days}%")
                Spacer(Modifier.height(14.dp))
                BulletLine("48 horas antes: ${policy.before48h}%")
                Spacer(Modifier.height(14.dp))
                BulletLine("24 horas antes: ${policy.before24h}%")
                Spacer(Modifier.height(14.dp))
                BulletLine("El mismo día o con menos de 24 horas: ${policy.sameDayOrLess24h}%")
            }
        }
    }
}

@Composable
private fun PortfolioSection(
    portfolios: List<PortfolioItem>,
    categoryLabel: String?
) {
    Column {
        SectionTitle(title = "Portafolio")
        Spacer(Modifier.height(12.dp))

        if (portfolios.isEmpty()) {
            Text(
                text = "Aún no hay elementos en el portafolio.",
                color = TextSecondary,
                fontSize = 14.sp
            )
        } else {
            portfolios.forEachIndexed { index, item ->
                PortfolioCard(
                    item = item,
                    categoryLabel = categoryLabel
                )

                if (index < portfolios.lastIndex) {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun PortfolioCard(
    item: PortfolioItem,
    categoryLabel: String?
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(Color(0xFFF2F4F8))
            ) {
                PortfolioImage(
                    imageUrl = item.image
                )

                if (!categoryLabel.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(BrandBlue)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = categoryLabel.uppercase(),
                            color = White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = item.services.firstOrNull() ?: "Trabajo realizado",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = item.description,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Normal
                )

                if (item.services.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.services.forEach { service ->
                            PortfolioServiceChip(service)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioImage(
    imageUrl: String?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            !imageUrl.isNullOrBlank() -> {
                val painterResource = asyncPainterResource(data = imageUrl)

                KamelImage(
                    resource = painterResource,
                    contentDescription = "Imagen del portafolio",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onFailure = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ImageBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = TablerIcons.Photo,
                                contentDescription = null,
                                tint = BlueGrayTextLight,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ImageBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.Photo,
                        contentDescription = null,
                        tint = BlueGrayTextLight,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioServiceChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(BrandBlue.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = BrandBlue.copy(alpha = 0.16f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BulletLine(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(BrandRed)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ReviewsSection(
    reviews: List<WorkerReviewItem>
) {
    var selectedFilter by remember { mutableStateOf(ReviewFilterType.ALL) }

    val averageRating = remember(reviews) {
        if (reviews.isEmpty()) 0.0 else reviews.map { it.rating.toDouble() }.average()
    }

    val filteredReviews = remember(reviews, selectedFilter) {
        when (selectedFilter) {
            ReviewFilterType.ALL -> reviews
            ReviewFilterType.FIVE -> reviews.filter { it.rating == 5 }
            ReviewFilterType.FOUR -> reviews.filter { it.rating == 4 }
            ReviewFilterType.THREE -> reviews.filter { it.rating == 3 }
        }
    }

    Column {
        ReviewsSummaryCard(
            reviews = reviews,
            averageRating = averageRating
        )

        Spacer(Modifier.height(14.dp))

        ReviewFiltersRow(
            selectedFilter = selectedFilter,
            onSelectedFilter = { selectedFilter = it }
        )

        Spacer(Modifier.height(16.dp))

        if (filteredReviews.isEmpty()) {
            Text(
                text = "Aún no hay reseñas disponibles.",
                color = TextSecondary,
                fontSize = 14.sp
            )
        } else {
            filteredReviews.forEachIndexed { index, review ->
                WorkerReviewCard(review = review)

                if (index < filteredReviews.lastIndex) {
                    Spacer(Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun ReviewsSummaryCard(
    reviews: List<WorkerReviewItem>,
    averageRating: Double
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderSoft),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatAverageRating(averageRating),
                        color = TextPrimary,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.width(14.dp))

                    Column {
                        ReviewStarsRow(averageRating)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${reviews.size} RESEÑAS",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(BrandRed)
                        .clickable { }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = "Escribir Reseña",
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            ReviewDistributionRow(
                label = "5",
                percentage = reviewPercentage(reviews, 5)
            )
            Spacer(Modifier.height(8.dp))
            ReviewDistributionRow(
                label = "4",
                percentage = reviewPercentage(reviews, 4)
            )
            Spacer(Modifier.height(8.dp))
            ReviewDistributionRow(
                label = "3",
                percentage = reviewPercentage(reviews, 3)
            )
        }
    }
}

@Composable
private fun ReviewStarsRow(rating: Double) {
    val fullStars = rating.toInt().coerceIn(0, 5)

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Text(
                text = "★",
                color = if (index < fullStars) BrandYellow else Color(0xFFD7DEE8),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReviewDistributionRow(
    label: String,
    percentage: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(16.dp)
        )

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFE6EBF3))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(BrandBlue)
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = "$percentage%",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(34.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ReviewFiltersRow(
    selectedFilter: ReviewFilterType,
    onSelectedFilter: (ReviewFilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ReviewFilterChip(
            text = "Todas",
            selected = selectedFilter == ReviewFilterType.ALL,
            onClick = { onSelectedFilter(ReviewFilterType.ALL) }
        )
        ReviewFilterChip(
            text = "5 ★",
            selected = selectedFilter == ReviewFilterType.FIVE,
            onClick = { onSelectedFilter(ReviewFilterType.FIVE) }
        )
        ReviewFilterChip(
            text = "4 ★",
            selected = selectedFilter == ReviewFilterType.FOUR,
            onClick = { onSelectedFilter(ReviewFilterType.FOUR) }
        )
        ReviewFilterChip(
            text = "3 ★",
            selected = selectedFilter == ReviewFilterType.THREE,
            onClick = { onSelectedFilter(ReviewFilterType.THREE) }
        )
    }
}

@Composable
private fun ReviewFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) BrandBlue else White
            )
            .border(
                width = 1.dp,
                color = if (selected) BrandBlue else Color(0xFFE3E8F0),
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            color = if (selected) White else TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WorkerReviewCard(
    review: WorkerReviewItem
) {
    val clientName = review.clientName.ifBlank { "Cliente" }
    val serviceName = review.serviceSummary.name.trim()
    val commentText = review.comment.trim()
    val firstImage = review.images.firstOrNull()?.takeIf { it.isNotBlank() }

    val hasComment = commentText.isNotBlank()
    val hasImage = firstImage != null
    val hasServiceName = serviceName.isNotBlank()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Color(0xFFF0F3F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReviewAvatar(initials = initialsFromName(clientName))

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = clientName,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = if (hasServiceName) serviceName else "Calificación registrada",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                ReviewStarsCompact(rating = review.rating)
            }

            if (hasComment) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = commentText,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            if (hasImage) {
                Spacer(Modifier.height(12.dp))
                ReviewImagePreview(imageUrl = firstImage!!)
            }

            if (hasServiceName) {
                Spacer(Modifier.height(12.dp))
                ReviewServiceTag(text = serviceName)
            }
        }
    }
}

@Composable
private fun ReviewAvatar(
    initials: String
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(AvatarBlueSoft),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun ReviewStarsCompact(
    rating: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Text(
                text = "★",
                color = if (index < rating) BrandYellow else Color(0xFFD7DEE8),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReviewImagePreview(
    imageUrl: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF2F4F8)),
        contentAlignment = Alignment.Center
    ) {
        val painterResource = asyncPainterResource(data = imageUrl)

        KamelImage(
            resource = painterResource,
            contentDescription = "Imagen de la reseña",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            onFailure = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ImageBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.Photo,
                        contentDescription = null,
                        tint = BlueGrayTextLight,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        )
    }
}

@Composable
private fun ReviewServiceTag(
    text: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(BrandBlue.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = BrandBlue.copy(alpha = 0.16f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatAverageRating(value: Double): String {
    val rounded = round(value * 10) / 10.0
    return if (rounded % 1.0 == 0.0) "${rounded.toInt()}.0" else rounded.toString()
}

private fun reviewPercentage(
    reviews: List<WorkerReviewItem>,
    rating: Int
): Int {
    if (reviews.isEmpty()) return 0
    val count = reviews.count { it.rating == rating }
    return ((count.toFloat() / reviews.size.toFloat()) * 100f).toInt()
}

private fun initialsFromName(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "${parts[0].first()}${parts[1].first()}".uppercase()
    }
}

@Composable
private fun SectionTitleWithAction(
    title: String,
    action: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = action,
            color = BrandBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun FeaturedServiceCard(service: Service) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Duración: ${service.duration}",
                        color = TextSecondary.copy(alpha = 0.75f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₡${service.cost}",
                        color = BrandBlue,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Base",
                        color = TextSecondary.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (service.description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = service.description,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CategoryChips(categories: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        categories.forEachIndexed { index, category ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(BrandBlue.copy(alpha = 0.10f))
                    .border(
                        width = 1.dp,
                        color = BrandBlue.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = BrandBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            if (index < categories.lastIndex) {
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun ProfileScheduleCard(
    schedule: List<WorkerSchedule>
) {
    val sortedSchedule = remember(schedule) {
        schedule.sortedBy { it.dayNumber }
    }

    val hasEnabledSchedule = remember(schedule) {
        schedule.any { it.enabled && it.timeBlocks.isNotEmpty() }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderSoft),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Horario de Atención",
                    color = BrandBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (hasEnabledSchedule) BrandBlue.copy(alpha = 0.10f)
                            else SoftSurface
                        )
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (hasEnabledSchedule) BrandBlue
                                    else BlueGrayTextLight
                                )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (hasEnabledSchedule) "HORARIO DISPONIBLE" else "SIN HORARIO",
                            color = if (hasEnabledSchedule) BrandBlue else Color(0xFF7C8798),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            if (sortedSchedule.isEmpty()) {
                Text(
                    text = "Este trabajador no tiene horario registrado.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            } else {
                sortedSchedule.forEachIndexed { index, item ->
                    ScheduleDayRow(
                        item = item,
                        highlighted = false
                    )

                    if (index < sortedSchedule.lastIndex) {
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleDayRow(
    item: WorkerSchedule,
    highlighted: Boolean
) {
    val dayLabel = dayLabelFromKey(item.dayKey)
    val timeText = formatDayTimeBlocks(item)

    val backgroundColor = if (highlighted) SoftBlueSurfaceAlt else Color(0xFFF8FAFC)
    val borderColor = if (highlighted) BorderBlueLight else BorderSoftAlt
    val dayTextColor = if (highlighted) BrandBlue else BlueGrayTextDark
    val timeColor = if (item.enabled && item.timeBlocks.isNotEmpty()) {
        if (highlighted) BrandBlue else BlueGrayTextDarkAlt
    } else {
        BlueGrayTextLighter
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (highlighted) 1.dp else 0.6.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dayLabel,
                color = dayTextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = timeText,
                color = timeColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

private fun formatDayTimeBlocks(item: WorkerSchedule): String {
    if (!item.enabled || item.timeBlocks.isEmpty()) return "CERRADO"

    return item.timeBlocks
        .filter { it.start.isNotBlank() && it.end.isNotBlank() }
        .joinToString(" / ") { "${it.start} - ${it.end}" }
        .ifBlank { "CERRADO" }
}

private fun dayLabelFromKey(dayKey: String): String {
    return when (dayKey.trim().lowercase()) {
        "monday" -> "Lunes"
        "tuesday" -> "Martes"
        "wednesday" -> "Miércoles"
        "thursday" -> "Jueves"
        "friday" -> "Viernes"
        "saturday" -> "Sábado"
        "sunday" -> "Domingo"
        else -> dayKey.replaceFirstChar { it.uppercase() }
    }
}

@Composable
private fun ToggleChip(
    text: String,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) BrandBlue.copy(alpha = 0.10f)
                else Color(0xFFF0F3F7)
            )
            .border(
                width = 1.dp,
                color = if (selected) BrandBlue.copy(alpha = 0.16f)
                else Color(0xFFE3E7EE),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) BrandBlue else TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}