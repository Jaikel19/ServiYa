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

import kotlin.math.round
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.graphicsLayer

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

@Composable
fun ProfessionalProfileRoute(
    workerId: String,
    viewModel: ProfessionalProfileViewModel,
    avatarPainter: Painter? = null,
    onBack: () -> Unit = {},
    onOpenChat: () -> Unit = {},
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
        onOpenChat = onOpenChat,
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
    onOpenChat: () -> Unit = {},
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
        floatingActionButton = {
            if (!showProcessAppointmentButton) {
                FloatingActionButton(
                    onClick = onOpenChat,
                    containerColor = ProfileWFColors.BrandBlue,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 5.dp
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(14.dp, RoundedCornerShape(20.dp), clip = false)
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Icon(
                        imageVector = TablerIcons.Message,
                        contentDescription = "Chat",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
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
                                        profile.services.firstOrNull()?.description?.takeIf { it.isNotBlank() }
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
                                            ProfileInfoCard(
                                                email = profile.email,
                                                phone = profile.phone,
                                                status = profile.status,
                                                travelTime = profile.travelTime,
                                                trustScore = profile.trustScore
                                            )

                                            Spacer(Modifier.height(16.dp))
                                            CancellationPoliciesCard()
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
                                            SectionTitle(title = "Reseñas")
                                            Spacer(Modifier.height(12.dp))
                                            Text(
                                                text = "Aún no hay reseñas disponibles.",
                                                color = ProfileWFColors.TextSecondary,
                                                fontSize = 14.sp
                                            )
                                        }

                                        ProfileTab.PORTFOLIO -> {
                                            SectionTitle(title = "Portafolio")
                                            Spacer(Modifier.height(12.dp))
                                            Text(
                                                text = "Aún no hay elementos en el portafolio.",
                                                color = ProfileWFColors.TextSecondary,
                                                fontSize = 14.sp
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileWFColors.BrandBlue)
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp, bottom = 28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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

            ServiYaPill()

            Spacer(modifier = Modifier.size(56.dp))
        }

        Spacer(Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                imageUrl = profile?.profilePictureLink,
                avatarPainter = avatarPainter
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingPill(rating = profile?.stars ?: 0.0)
                    Spacer(Modifier.width(8.dp))
                    ProfessionPill(text = profession)
                }
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
private fun ServiYaPill() {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 5.dp
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = ProfileWFColors.BrandBlue,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("SERVI")
                }
                withStyle(
                    SpanStyle(
                        color = ProfileWFColors.Red,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("YA")
                }
            },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 11.dp),
            fontSize = 17.sp
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SELECCIONAR SERVICIOS",
                    color = ProfileWFColors.TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Compara y selecciona para tu presupuesto",
                    color = ProfileWFColors.TextSecondary.copy(alpha = 0.75f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = TablerIcons.AdjustmentsHorizontal,
                contentDescription = "Ordenar servicios",
                tint = Color(0xFFC3CBD8),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

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
                    Spacer(Modifier.height(14.dp))
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFF8FBFF) else Color.White
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (selected) ProfileWFColors.BrandBlue else Color(0xFFE9EEF5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 1.dp else 2.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpanded)
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                ServiceSelectorBox(
                    selected = selected,
                    onClick = onToggleSelected
                )

                Spacer(Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = service.name,
                        color = ProfileWFColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = TablerIcons.Clock,
                            contentDescription = null,
                            tint = Color(0xFF6F7F98),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = service.duration,
                            color = Color(0xFF6F7F98),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatPrice(service.cost),
                        color = ProfileWFColors.BrandBlue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Icon(
                        imageVector = TablerIcons.ChevronDown,
                        contentDescription = if (expanded) "Contraer" else "Expandir",
                        tint = Color(0xFF97A4B8),
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer {
                                rotationZ = if (expanded) 180f else 0f
                            }
                    )
                }
            }

            if (expanded && service.description.isNotBlank()) {
                Spacer(Modifier.height(14.dp))

                Divider(
                    color = Color(0xFFEAEFF5),
                    thickness = 1.dp
                )

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
        modifier = Modifier
            .size(112.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF6B4C22))
            .border(
                width = 2.5.dp,
                color = Color.White.copy(alpha = 0.24f),
                shape = RoundedCornerShape(24.dp)
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
                    modifier = Modifier.fillMaxSize(),
                    onFailure = {
                        if (avatarPainter != null) {
                            Image(
                                painter = avatarPainter,
                                contentDescription = "Foto del trabajador",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = "👷",
                                fontSize = 52.sp
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
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Text(
                    text = "👷",
                    fontSize = 52.sp
                )
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
private fun CancellationPoliciesCard() {
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

            BulletLine("Cancelación gratuita hasta 24 horas antes del servicio.")
            Spacer(Modifier.height(14.dp))
            BulletLine("50% de cargo por cancelaciones con menos de 12 horas.")
        }
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
private fun ProfileInfoCard(
    email: String,
    phone: String,
    status: String,
    travelTime: Int,
    trustScore: Int
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
            Text(
                text = "Información",
                color = ProfileWFColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))
            BulletLine("Correo: $email")
            Spacer(Modifier.height(12.dp))
            BulletLine("Teléfono: $phone")
            Spacer(Modifier.height(12.dp))
            BulletLine("Estado: $status")
            Spacer(Modifier.height(12.dp))
            BulletLine("Tiempo de traslado: $travelTime min")
            Spacer(Modifier.height(12.dp))
            BulletLine("Trust score: $trustScore")
        }
    }
}

@Composable
private fun ReviewToggleChips() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        ToggleChip(
            text = "Recibidas (48)",
            selected = true
        )
        Spacer(Modifier.width(10.dp))
        ToggleChip(
            text = "Realizadas (12)",
            selected = false
        )
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
private fun ReviewCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F3F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3E8F0))
                    )

                    Spacer(Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "María López",
                            color = ProfileWFColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hace 2 días",
                            color = ProfileWFColors.TextSecondary.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }

                Row {
                    repeat(5) {
                        Icon(
                            imageVector = TablerIcons.Star,
                            contentDescription = null,
                            tint = ProfileWFColors.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "\"Excelente profesional, muy puntual y dejó todo funcionando perfectamente. Muy recomendado.\"",
                color = ProfileWFColors.TextSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium
            )
        }
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