package com.example.seviya.UI

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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.entity.ProfessionalProfileData
import com.example.shared.domain.entity.Service
import com.example.shared.presentation.professionalProfile.ProfessionalProfileUiState
import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel
import com.example.shared.domain.entity.CancellationPolicy

import kotlin.math.round
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.graphicsLayer
import com.example.shared.domain.entity.PortfolioItem
import com.example.shared.domain.entity.WorkerReviewItem
import com.example.shared.domain.entity.WorkerSchedule
import kotlin.time.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// ✅ Tabler Icons (compose-icons)
import compose.icons.TablerIcons
import compose.icons.tablericons.*

private object ProfileWFColors {
    val BrandBlue = Color(0xFF004AAD)
    val BrandBlueDark = Color(0xFF083C8B)
    val BrandBlueSoft = Color(0xFF2E6FD1)
    val Surface = Color(0xFFF5F6F8)
    val SurfaceCard = Color(0xFFFFFFFF)
    val SurfaceSoft = Color(0xFFF1F4F8)
    val Border = Color(0xFFE6EAF0)
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF64748B)
    val TabInactive = Color(0xFFA2AEC2)
    val Yellow = Color(0xFFF9D33D)
    val Red = Color(0xFFEF4444)
    val White = Color(0xFFFFFFFF)
}

private enum class ProfileTab {
    INFO, SERVICES, REVIEWS, PORTFOLIO
}

private enum class ReviewFilterType {
    ALL, FIVE, FOUR, THREE
}

@Composable
fun ProfessionalProfileRoute(
    workerId: String,
    viewModel: ProfessionalProfileViewModel,
    avatarPainter: Painter? = null,
    onBack: () -> Unit = {},
    onProcessAppointment: (List<Service>) -> Unit = {},
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(workerId) {
        viewModel.loadProfile(workerId)
    }

    ProfessionalProfileScreen(
        avatarPainter = avatarPainter,
        state = state,
        onBack = onBack,
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
    onProcessAppointment: (List<Service>) -> Unit = {},
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
        selectedTab == ProfileTab.SERVICES && selectedServices.isNotEmpty()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProfileWFColors.BrandBlue,
        bottomBar = {
            ProfessionalProfileBottomBar(
                onServices = onBottomServices,
                onMap = onBottomMap,
                onSearch = onBottomSearch,
                onNotifications = onBottomNotifications,
                onMenu = onBottomMenu
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfileWFColors.BrandBlue)
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderSection(
                    avatarPainter = avatarPainter,
                    onBack = onBack,
                    profile = profile
                )

                Surface(
                    color = ProfileWFColors.Surface,
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
                                        color = ProfileWFColors.TextSecondary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                state.errorMessage != null -> {
                                    Text(
                                        text = "Error: ${state.errorMessage}",
                                        color = ProfileWFColors.Red,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                profile == null -> {
                                    Text(
                                        text = "No se encontró la información del profesional.",
                                        color = ProfileWFColors.TextSecondary,
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
                                            color = Color(0xFF44556F)
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

                        if (showProcessAppointmentButton) {
                            ProcessAppointmentBar(
                                selectedCount = selectedServicesCount,
                                total = selectedServicesTotal,
                                onClick = { onProcessAppointment(selectedServices) },
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
    profile: ProfessionalProfileData?
) {
    val name = profile?.name?.takeIf { it.isNotBlank() } ?: "Profesional"
    val subtitle = profile?.locationProvince?.takeIf { it.isNotBlank() }
        ?: "Ubicación no disponible"
    val profession = profile?.categoryNames?.firstOrNull()?.uppercase() ?: "SERVICIO"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileWFColors.BrandBlue)
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 2.dp, bottom = 6.dp)
    ) {
        SquareGlassButton(
            onClick = onBack,
            content = {
                Icon(
                    imageVector = TablerIcons.ChevronRight,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(180f)
                )
            }
        )

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
                color = Color.White,
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
                    tint = Color.White.copy(alpha = 0.88f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.88f),
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
            .background(Color.White.copy(alpha = 0.12f))
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.16f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
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
                color = ProfileWFColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "Selecciona los que deseas incluir en tu cita",
                color = ProfileWFColors.TextSecondary.copy(alpha = 0.82f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(12.dp))

        if (services.isEmpty()) {
            Text(
                text = "Este profesional no tiene servicios registrados.",
                color = ProfileWFColors.TextSecondary,
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
            containerColor = if (selected) Color(0xFFF7FAFF) else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) ProfileWFColors.BrandBlue.copy(alpha = 0.35f) else Color(0xFFE7EDF5)
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
                        color = ProfileWFColors.TextPrimary,
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
                            tint = Color(0xFF7B8AA0),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = service.duration,
                            color = Color(0xFF7B8AA0),
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
                        color = ProfileWFColors.BrandBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selected) ProfileWFColors.BrandBlue.copy(alpha = 0.10f)
                                else Color(0xFFF4F7FA)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = TablerIcons.ChevronDown,
                            contentDescription = if (expanded) "Contraer" else "Expandir",
                            tint = Color(0xFF8C9AAF),
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
                    color = Color(0xFFEAEFF5),
                    thickness = 1.dp
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = service.description,
                    color = ProfileWFColors.TextSecondary,
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
            .background(if (selected) ProfileWFColors.BrandBlue else Color.Transparent)
            .border(
                width = if (selected) 0.dp else 2.dp,
                color = if (selected) Color.Transparent else Color(0xFFC7D0DE),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = TablerIcons.Check,
                contentDescription = "Seleccionado",
                tint = Color.White,
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
        color = ProfileWFColors.BrandBlue,
        shape = RoundedCornerShape(26.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedCount.toString(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(Modifier.width(14.dp))

                Text(
                    text = "Procesar cita",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = formatPrice(total),
                color = Color.White,
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
                .background(Color.White.copy(alpha = 0.22f))
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = 0.30f),
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
            .background(ProfileWFColors.Yellow)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = TablerIcons.Star,
            contentDescription = null,
            tint = ProfileWFColors.TextPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = ratingText,
            color = ProfileWFColors.TextPrimary,
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
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
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

        Divider(color = ProfileWFColors.Border, thickness = 1.dp)
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
            color = if (selected) ProfileWFColors.BrandBlue else ProfileWFColors.TabInactive,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .height(3.dp)
                .width(94.dp)
                .background(
                    if (selected) ProfileWFColors.BrandBlue else Color.Transparent
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
        colors = CardDefaults.cardColors(containerColor = ProfileWFColors.SurfaceSoft),
        border = BorderStroke(1.dp, ProfileWFColors.Border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = TablerIcons.ShieldCheck,
                    contentDescription = null,
                    tint = ProfileWFColors.BrandBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Políticas de cancelación",
                    color = ProfileWFColors.BrandBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            if (policy == null) {
                Text(
                    text = "No hay políticas de cancelación registradas.",
                    color = ProfileWFColors.TextSecondary,
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
                color = ProfileWFColors.TextSecondary,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ProfileWFColors.Border),
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
                            .background(ProfileWFColors.BrandBlue)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = categoryLabel.uppercase(),
                            color = Color.White,
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
                    color = ProfileWFColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = item.description,
                    color = ProfileWFColors.TextSecondary,
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
                                .background(Color(0xFFEFF3F8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = TablerIcons.Photo,
                                contentDescription = null,
                                tint = Color(0xFF9AA6B2),
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
                        .background(Color(0xFFEFF3F8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.Photo,
                        contentDescription = null,
                        tint = Color(0xFF9AA6B2),
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
            .background(ProfileWFColors.BrandBlue.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = ProfileWFColors.BrandBlue.copy(alpha = 0.16f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = ProfileWFColors.BrandBlue,
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
                .background(ProfileWFColors.Red)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = text,
            color = ProfileWFColors.TextSecondary,
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
                color = ProfileWFColors.TextSecondary,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ProfileWFColors.Border),
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
                        color = ProfileWFColors.TextPrimary,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.width(14.dp))

                    Column {
                        ReviewStarsRow(averageRating)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${reviews.size} RESEÑAS",
                            color = ProfileWFColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(ProfileWFColors.Red)
                        .clickable { }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = "Escribir Reseña",
                        color = Color.White,
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
                color = if (index < fullStars) ProfileWFColors.Yellow else Color(0xFFD7DEE8),
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
            color = ProfileWFColors.TextSecondary,
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
                    .background(ProfileWFColors.BrandBlue)
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = "$percentage%",
            color = ProfileWFColors.TextSecondary,
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
                if (selected) ProfileWFColors.BrandBlue else Color.White
            )
            .border(
                width = 1.dp,
                color = if (selected) ProfileWFColors.BrandBlue else Color(0xFFE3E8F0),
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else ProfileWFColors.TextSecondary,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            color = ProfileWFColors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = if (hasServiceName) serviceName else "Calificación registrada",
                            color = ProfileWFColors.TextSecondary,
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
                    color = ProfileWFColors.TextSecondary,
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
            .background(Color(0xFFE8F0FF)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = ProfileWFColors.BrandBlue,
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
                color = if (index < rating) ProfileWFColors.Yellow else Color(0xFFD7DEE8),
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
                        .background(Color(0xFFEFF3F8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.Photo,
                        contentDescription = null,
                        tint = Color(0xFF9AA6B2),
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
            .background(ProfileWFColors.BrandBlue.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = ProfileWFColors.BrandBlue.copy(alpha = 0.16f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = ProfileWFColors.BrandBlue,
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
            color = ProfileWFColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = action,
            color = ProfileWFColors.BrandBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = ProfileWFColors.TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun FeaturedServiceCard(service: Service) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ProfileWFColors.Border),
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
                        color = ProfileWFColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Duración: ${service.duration}",
                        color = ProfileWFColors.TextSecondary.copy(alpha = 0.75f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₡${service.cost}",
                        color = ProfileWFColors.BrandBlue,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Base",
                        color = ProfileWFColors.TextSecondary.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (service.description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = service.description,
                    color = ProfileWFColors.TextSecondary,
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
                    .background(ProfileWFColors.BrandBlue.copy(alpha = 0.10f))
                    .border(
                        width = 1.dp,
                        color = ProfileWFColors.BrandBlue.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = ProfileWFColors.BrandBlue,
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

    val currentDayKey = remember { currentDayKey() }
    val isOpenNow = remember(schedule) { isWorkerOpenNow(schedule) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ProfileWFColors.Border),
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
                    color = ProfileWFColors.BrandBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (isOpenNow) ProfileWFColors.BrandBlue.copy(alpha = 0.10f)
                            else Color(0xFFF1F4F8)
                        )
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isOpenNow) ProfileWFColors.BrandBlue
                                    else Color(0xFF9AA6B2)
                                )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isOpenNow) "ABIERTO AHORA" else "CERRADO",
                            color = if (isOpenNow) ProfileWFColors.BrandBlue else Color(0xFF7C8798),
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
                    color = ProfileWFColors.TextSecondary,
                    fontSize = 14.sp
                )
            } else {
                sortedSchedule.forEachIndexed { index, item ->
                    ScheduleDayRow(
                        item = item,
                        highlighted = item.dayKey.equals(currentDayKey, ignoreCase = true)
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

    val backgroundColor = if (highlighted) Color(0xFFF4F8FF) else Color(0xFFF8FAFC)
    val borderColor = if (highlighted) Color(0xFFBFD6FF) else Color(0xFFE7ECF3)
    val dayTextColor = if (highlighted) ProfileWFColors.BrandBlue else Color(0xFF5C6778)
    val timeColor = if (item.enabled && item.timeBlocks.isNotEmpty()) {
        if (highlighted) ProfileWFColors.BrandBlue else Color(0xFF4B5565)
    } else {
        Color(0xFFB7C0CC)
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

@OptIn(kotlin.time.ExperimentalTime::class)
private fun currentDayKey(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    return when (now.date.dayOfWeek) {
        DayOfWeek.MONDAY -> "monday"
        DayOfWeek.TUESDAY -> "tuesday"
        DayOfWeek.WEDNESDAY -> "wednesday"
        DayOfWeek.THURSDAY -> "thursday"
        DayOfWeek.FRIDAY -> "friday"
        DayOfWeek.SATURDAY -> "saturday"
        DayOfWeek.SUNDAY -> "sunday"
    }
}

@OptIn(kotlin.time.ExperimentalTime::class)
private fun isWorkerOpenNow(schedule: List<WorkerSchedule>): Boolean {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentMinutes = now.time.hour * 60 + now.time.minute
    val dayKey = currentDayKey()

    val todaySchedule = schedule.firstOrNull {
        it.dayKey.equals(dayKey, ignoreCase = true)
    } ?: return false

    if (!todaySchedule.enabled || todaySchedule.timeBlocks.isEmpty()) return false

    return todaySchedule.timeBlocks.any { block ->
        val startMinutes = parseHourToMinutes(block.start)
        val endMinutes = parseHourToMinutes(block.end)

        startMinutes != null &&
                endMinutes != null &&
                currentMinutes in startMinutes..endMinutes
    }
}

private fun parseHourToMinutes(value: String): Int? {
    val parts = value.split(":")
    if (parts.size != 2) return null

    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null

    return hour * 60 + minute
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
                if (selected) ProfileWFColors.BrandBlue.copy(alpha = 0.10f)
                else Color(0xFFF0F3F7)
            )
            .border(
                width = 1.dp,
                color = if (selected) ProfileWFColors.BrandBlue.copy(alpha = 0.16f)
                else Color(0xFFE3E7EE),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) ProfileWFColors.BrandBlue else ProfileWFColors.TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ProfessionalProfileBottomBar(
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
                    label = "SERVICIOS",
                    selected = true,
                    onClick = onServices,
                    content = {
                        Icon(
                            imageVector = TablerIcons.Apps,
                            contentDescription = null,
                            tint = ProfileWFColors.BrandBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                BottomNavItem(
                    label = "Mapa",
                    selected = false,
                    onClick = onMap,
                    content = {
                        Icon(
                            imageVector = TablerIcons.MapPin,
                            contentDescription = null,
                            tint = ProfileWFColors.TabInactive,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                BottomNavItem(
                    label = "Buscar",
                    selected = false,
                    onClick = onSearch,
                    content = {
                        Icon(
                            imageVector = TablerIcons.Search,
                            contentDescription = null,
                            tint = ProfileWFColors.TabInactive,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                BottomNavItem(
                    label = "Notif.",
                    selected = false,
                    onClick = onNotifications,
                    showDot = true,
                    content = {
                        Icon(
                            imageVector = TablerIcons.Mail,
                            contentDescription = null,
                            tint = ProfileWFColors.TabInactive,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                BottomNavItem(
                    label = "Menu",
                    selected = false,
                    onClick = onMenu,
                    content = {
                        Icon(
                            imageVector = TablerIcons.User,
                            contentDescription = null,
                            tint = ProfileWFColors.TabInactive,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    showDot: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            content()

            if (showDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 3.dp, y = (-2).dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(ProfileWFColors.Red)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = label,
            color = if (selected) ProfileWFColors.BrandBlue else ProfileWFColors.TabInactive,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
            maxLines = 1
        )
    }
}