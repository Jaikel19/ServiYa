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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.theme.AppBackground
import com.example.seviya.theme.AvatarBlueSoft
import com.example.seviya.theme.BlueGrayText
import com.example.seviya.theme.BlueGrayTextLight
import com.example.seviya.theme.BlueGrayTextSoft
import com.example.seviya.theme.BorderBlueLight
import com.example.seviya.theme.BorderSoft
import com.example.seviya.theme.BorderSoftAlt
import com.example.seviya.theme.BrandBlue
import com.example.seviya.theme.BrandRed
import com.example.seviya.theme.CardSurface
import com.example.seviya.theme.DividerSoft
import com.example.seviya.theme.StatusPendingBackground
import com.example.seviya.theme.StatusPendingText
import com.example.seviya.theme.Success
import com.example.seviya.theme.TextPrimary
import com.example.seviya.theme.TextSecondary
import com.example.seviya.theme.White
import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.Service
import com.example.shared.domain.entity.WorkerSchedule
import com.example.shared.presentation.requestAppointment.AppointmentDayOption
import com.example.shared.presentation.requestAppointment.AppointmentMonthOption
import com.example.shared.presentation.requestAppointment.AppointmentTimeOption
import com.example.shared.presentation.requestAppointment.CurrentTimeSnapshot
import com.example.shared.presentation.requestAppointment.RequestAppointmentUiState
import com.example.shared.presentation.requestAppointment.RequestAppointmentViewModel
import com.example.shared.presentation.requestAppointment.REQUEST_APPOINTMENT_MONTH_SELECTION_RANGE
import com.example.shared.presentation.requestAppointment.RequestAppointmentDraft
import com.example.shared.presentation.requestAppointment.buildAvailableDays
import com.example.shared.presentation.requestAppointment.buildAvailableMonths
import com.example.shared.presentation.requestAppointment.buildAvailableTimes
import com.example.shared.presentation.requestAppointment.buildPendingAppointment
import com.example.shared.presentation.requestAppointment.calculateTotalServiceDurationMinutes
import com.example.shared.presentation.requestAppointment.firstEnabledAppointmentTime
import com.example.shared.presentation.requestAppointment.fullDateTimeLabel
import com.example.shared.presentation.requestAppointment.hasUsableCurrentTime
import com.example.shared.presentation.requestAppointment.isWorkerAvailable
import compose.icons.TablerIcons
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.Check
import compose.icons.tablericons.ChevronRight
import compose.icons.tablericons.Clock
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Menu2
import compose.icons.tablericons.User
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.round

@Composable
fun RequestAppointmentRoute(
    draft: RequestAppointmentDraft,
    viewModel: RequestAppointmentViewModel,
    avatarPainter: Painter? = null,
    onBack: () -> Unit = {},
    onOpenRequests: () -> Unit = {},
    onOpenHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(draft.clientId) {
        if (draft.clientId.isNotBlank()) {
            viewModel.loadClientAddresses(draft.clientId)
        }
    }

    RequestAppointmentScreen(
        draft = draft,
        uiState = uiState,
        avatarPainter = avatarPainter,
        onBack = onBack,
        onSelectAddress = { viewModel.selectAddress(it) },
        onSubmitAppointment = { appointment ->
            viewModel.createAppointment(appointment)
        },
        onOpenRequests = onOpenRequests,
        onOpenHome = onOpenHome
    )
}

@Composable
fun RequestAppointmentScreen(
    draft: RequestAppointmentDraft,
    uiState: RequestAppointmentUiState,
    avatarPainter: Painter? = null,
    onBack: () -> Unit = {},
    onSelectAddress: (String) -> Unit,
    onSubmitAppointment: (Appointment) -> Unit,
    onOpenRequests: () -> Unit = {},
    onOpenHome: () -> Unit = {}
) {
    val totalCost = remember(draft.selectedServices) {
        draft.selectedServices.sumOf { it.cost.toDouble() }
    }

    val totalDurationMinutes = remember(draft.selectedServices) {
        calculateTotalServiceDurationMinutes(draft.selectedServices)
    }

    val blockingDurationMinutes = remember(draft.selectedServices, draft.travelTimeMinutes) {
        totalDurationMinutes + draft.travelTimeMinutes
    }

    val hasValidCurrentTime = remember(draft.currentTime) {
        hasUsableCurrentTime(draft.currentTime)
    }

    val availableMonths = remember(draft.currentTime, hasValidCurrentTime) {
        if (!hasValidCurrentTime) {
            emptyList()
        } else {
            buildAvailableMonths(
                currentTime = draft.currentTime,
                monthsAhead = REQUEST_APPOINTMENT_MONTH_SELECTION_RANGE
            )
        }
    }

    var selectedMonth by remember(availableMonths) {
        mutableStateOf(availableMonths.firstOrNull())
    }

    val availableDays = remember(
        draft.schedule,
        draft.workerAppointments,
        draft.currentTime,
        selectedMonth,
        totalDurationMinutes,
        draft.travelTimeMinutes,
        hasValidCurrentTime
    ) {
        if (!hasValidCurrentTime) {
            emptyList()
        } else {
            buildAvailableDays(
                schedule = draft.schedule,
                existingAppointments = draft.workerAppointments,
                currentTime = draft.currentTime,
                selectedMonth = selectedMonth,
                requestedServiceDurationMinutes = totalDurationMinutes,
                requestedTravelTimeMinutes = draft.travelTimeMinutes
            )
        }
    }

    var selectedDay by remember(availableDays) {
        mutableStateOf(availableDays.firstOrNull())
    }

    val availableTimes = remember(
        selectedDay,
        draft.schedule,
        draft.workerAppointments,
        draft.currentTime,
        totalDurationMinutes,
        draft.travelTimeMinutes,
        hasValidCurrentTime
    ) {
        if (!hasValidCurrentTime) {
            emptyList()
        } else {
            buildAvailableTimes(
                date = selectedDay?.date,
                schedule = draft.schedule,
                existingAppointments = draft.workerAppointments,
                currentTime = draft.currentTime,
                requestedServiceDurationMinutes = totalDurationMinutes,
                requestedTravelTimeMinutes = draft.travelTimeMinutes
            )
        }
    }

    var selectedTime by remember(availableTimes) {
        mutableStateOf(availableTimes.firstOrNull { it.enabled })
    }

    val selectedAddress = remember(uiState.savedAddresses, uiState.selectedAddressId) {
        uiState.savedAddresses.firstOrNull { it.id == uiState.selectedAddressId }
            ?: uiState.savedAddresses.firstOrNull { it.isDefault }
    }

    val formValid = draft.clientId.isNotBlank() &&
            draft.workerId.isNotBlank() &&
            selectedMonth != null &&
            selectedDay != null &&
            selectedTime != null &&
            selectedAddress != null &&
            selectedTime?.enabled == true &&
            hasValidCurrentTime

    val createdDateLabel = remember(selectedDay, selectedTime) {
        if (selectedDay != null && selectedTime != null) {
            fullDateTimeLabel(selectedDay!!.date, selectedTime!!)
        } else {
            ""
        }
    }

    val createdAddressLabel = remember(selectedAddress) {
        selectedAddress?.let {
            listOf(it.alias, it.district, it.canton, it.province, it.reference)
                .filter { value -> value.isNotBlank() }
                .joinToString(", ")
        }.orEmpty()
    }

    if (uiState.isCreated) {
        RequestAppointmentSuccessContent(
            mainService = draft.selectedServices.firstOrNull()?.name ?: "Servicio",
            dateTime = createdDateLabel,
            address = createdAddressLabel,
            onOpenRequests = onOpenRequests,
            onOpenHome = onOpenHome
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                RequestAppointmentHeader(
                    onBack = onBack
                )

                Surface(
                    color = AppBackground,
                    shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 18.dp, vertical = 18.dp)
                            .navigationBarsPadding()
                    ) {
                        StepCard(number = "1") {
                            ProfessionalStepContent(
                                draft = draft,
                                avatarPainter = avatarPainter,
                                totalCost = totalCost,
                                totalDurationMinutes = totalDurationMinutes,
                                blockingDurationMinutes = blockingDurationMinutes
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        StepCard(number = "2") {
                            AvailabilityStepContent(
                                hasValidCurrentTime = hasValidCurrentTime,
                                availableMonths = availableMonths,
                                selectedMonth = selectedMonth,
                                onSelectMonth = { month ->
                                    selectedMonth = month

                                    val monthDays = buildAvailableDays(
                                        schedule = draft.schedule,
                                        existingAppointments = draft.workerAppointments,
                                        currentTime = draft.currentTime,
                                        selectedMonth = month,
                                        requestedServiceDurationMinutes = totalDurationMinutes,
                                        requestedTravelTimeMinutes = draft.travelTimeMinutes
                                    )

                                    val firstDay = monthDays.firstOrNull()
                                    selectedDay = firstDay
                                    selectedTime = firstEnabledAppointmentTime(buildAvailableTimes(
                                        date = firstDay?.date,
                                        schedule = draft.schedule,
                                        existingAppointments = draft.workerAppointments,
                                        currentTime = draft.currentTime,
                                        requestedServiceDurationMinutes = totalDurationMinutes,
                                        requestedTravelTimeMinutes = draft.travelTimeMinutes
                                    ))
                                },
                                availableDays = availableDays,
                                selectedDay = selectedDay,
                                onSelectDay = { day ->
                                    selectedDay = day
                                    selectedTime = firstEnabledAppointmentTime(buildAvailableTimes(
                                        date = day.date,
                                        schedule = draft.schedule,
                                        existingAppointments = draft.workerAppointments,
                                        currentTime = draft.currentTime,
                                        requestedServiceDurationMinutes = totalDurationMinutes,
                                        requestedTravelTimeMinutes = draft.travelTimeMinutes
                                    ))
                                },
                                availableTimes = availableTimes,
                                selectedTime = selectedTime,
                                onSelectTime = { selectedTime = it }
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        StepCard(number = "3") {
                            LocationStepContent(
                                addresses = uiState.savedAddresses,
                                selectedAddressId = uiState.selectedAddressId,
                                selectedAddress = selectedAddress,
                                isLoadingAddresses = uiState.isLoadingAddresses,
                                onSelectAddress = onSelectAddress
                            )

                            Spacer(Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val chosenDay = selectedDay ?: return@Button
                                    val chosenTime = selectedTime ?: return@Button
                                    val chosenAddress = selectedAddress ?: return@Button

                                    if (!chosenTime.enabled) return@Button

                                    onSubmitAppointment(
                                        buildPendingAppointment(
                                            draft = draft,
                                            selectedDate = chosenDay.date,
                                            selectedTime = chosenTime,
                                            selectedAddress = chosenAddress
                                        )
                                    )
                                },
                                enabled = formValid && !uiState.isCreating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandBlue,
                                    contentColor = White,
                                    disabledContainerColor = BrandBlue.copy(alpha = 0.45f),
                                    disabledContentColor = White.copy(alpha = 0.70f)
                                )
                            ) {
                                if (uiState.isCreating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.2.dp,
                                        color = White
                                    )
                                } else {
                                    Text(
                                        text = "Enviar Solicitud",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            uiState.errorMessage?.let {
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = it,
                                    color = BrandRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestAppointmentHeader(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            RoundHeaderButton(
                onClick = onBack,
                icon = {
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
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Solicitud de Cita",
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "PREPARACIÓN DEL SERVICIO",
                color = White.copy(alpha = 0.80f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp
            )
        }

        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            RoundHeaderButton(
                onClick = {},
                icon = {
                    Icon(
                        imageVector = TablerIcons.Menu2,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun RoundHeaderButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(White.copy(alpha = 0.14f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun StepCard(
    number: String,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(BrandBlue)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            border = BorderStroke(1.dp, BorderSoft),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ProfessionalStepContent(
    draft: RequestAppointmentDraft,
    avatarPainter: Painter?,
    totalCost: Double,
    totalDurationMinutes: Int,
    blockingDurationMinutes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "DATOS DEL PROFESIONAL",
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.1.sp
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Success.copy(alpha = 0.10f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isWorkerAvailable(draft.schedule)) "DISPONIBLE" else "SIN HORARIO",
                color = if (isWorkerAvailable(draft.schedule)) Success else BlueGrayTextSoft,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        WorkerAvatar(
            imageUrl = draft.workerImageUrl,
            avatarPainter = avatarPainter
        )

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ESPECIALISTA",
                color = BlueGrayTextLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = draft.workerName,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (draft.workerProvince.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = TablerIcons.MapPin,
                        contentDescription = null,
                        tint = BlueGrayText,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.6.dp))
                    Text(
                        text = draft.workerProvince,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(18.dp))

    Text(
        text = "SERVICIOS SELECCIONADOS",
        color = BlueGrayTextLight,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(10.dp))

    draft.selectedServices.forEachIndexed { index, service ->
        ServiceSummaryItem(service = service)
        if (index < draft.selectedServices.lastIndex) {
            Spacer(Modifier.height(10.dp))
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppBackground)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "TOTAL",
                color = BlueGrayTextLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "${draft.selectedServices.size} servicio(s)",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = formatPrice(totalCost),
            color = BrandBlue,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }

    Spacer(Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Duración total:",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$totalDurationMinutes min",
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(Modifier.height(6.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Bloqueo en agenda:",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$blockingDurationMinutes min",
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }

    if (draft.travelTimeMinutes > 0) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = TablerIcons.Clock,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Tiempo de traslado registrado por el trabajador: ${draft.travelTimeMinutes} min",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun WorkerAvatar(
    imageUrl: String,
    avatarPainter: Painter?
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(AvatarBlueSoft),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUrl.isNotBlank() -> {
                val painterResource = asyncPainterResource(data = imageUrl)
                KamelImage(
                    resource = painterResource,
                    contentDescription = "Foto del profesional",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onFailure = {
                        if (avatarPainter != null) {
                            Image(
                                painter = avatarPainter,
                                contentDescription = "Foto del profesional",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = TablerIcons.User,
                                contentDescription = null,
                                tint = BrandBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                )
            }

            avatarPainter != null -> {
                Image(
                    painter = avatarPainter,
                    contentDescription = "Foto del profesional",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Icon(
                    imageVector = TablerIcons.User,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun ServiceSummaryItem(
    service: Service
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, BorderSoftAlt),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandBlue.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TablerIcons.Briefcase,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if (service.duration.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = service.duration,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = formatPrice(service.cost.toDouble()),
                color = BrandBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun AvailabilityStepContent(
    hasValidCurrentTime: Boolean,
    availableMonths: List<AppointmentMonthOption>,
    selectedMonth: AppointmentMonthOption?,
    onSelectMonth: (AppointmentMonthOption) -> Unit,
    availableDays: List<AppointmentDayOption>,
    selectedDay: AppointmentDayOption?,
    onSelectDay: (AppointmentDayOption) -> Unit,
    availableTimes: List<AppointmentTimeOption>,
    selectedTime: AppointmentTimeOption?,
    onSelectTime: (AppointmentTimeOption) -> Unit
) {
    Text(
        text = "SELECCIONAR FECHA Y HORA",
        color = BrandBlue,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.1.sp
    )

    Spacer(Modifier.height(16.dp))

    if (!hasValidCurrentTime) {
        Text(
            text = "No se pudo obtener la fecha actual. Debes enviar un CurrentTimeSnapshot válido desde la vista anterior.",
            color = BrandRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        return
    }

    if (availableMonths.isEmpty()) {
        Text(
            text = "No hay meses disponibles para mostrar.",
            color = BrandRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        return
    }

    Text(
        text = "Selecciona un mes",
        color = BlueGrayTextLight,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(10.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        availableMonths.forEach { month ->
            MonthOptionCard(
                option = month,
                selected = selectedMonth?.monthStart == month.monthStart,
                onClick = { onSelectMonth(month) }
            )
        }
    }

    Spacer(Modifier.height(18.dp))

    Text(
        text = "Selecciona un día",
        color = BlueGrayTextLight,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(10.dp))

    if (availableDays.isEmpty()) {
        Text(
            text = "No hay días disponibles para este mes.",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        availableDays.forEach { day ->
            DayOptionCard(
                option = day,
                selected = selectedDay?.date == day.date,
                onClick = { onSelectDay(day) }
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    if (availableTimes.isEmpty()) {
        Text(
            text = "No hay horarios disponibles para este día.",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    } else {
        FlowRowTimes(
            times = availableTimes,
            selectedTime = selectedTime,
            onSelectTime = onSelectTime
        )
    }
}

@Composable
private fun MonthOptionCard(
    option: AppointmentMonthOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) BrandBlue.copy(alpha = 0.08f) else White
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) BrandBlue else BorderSoft
        ),
        modifier = Modifier
            .width(92.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = option.shortLabel,
                color = if (selected) BrandBlue else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = option.yearLabel,
                color = if (selected) BrandBlue else BlueGrayTextSoft,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DayOptionCard(
    option: AppointmentDayOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) BrandBlue.copy(alpha = 0.08f) else White
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) BrandBlue else BorderSoft
        ),
        modifier = Modifier
            .width(78.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = option.topLabel,
                color = if (selected) BrandBlue else BlueGrayTextSoft,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = option.dayNumber,
                color = if (selected) BrandBlue else TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun FlowRowTimes(
    times: List<AppointmentTimeOption>,
    selectedTime: AppointmentTimeOption?,
    onSelectTime: (AppointmentTimeOption) -> Unit
) {
    Column {
        var rowItems = 0
        var currentRow = mutableListOf<AppointmentTimeOption>()

        times.forEachIndexed { index, item ->
            currentRow.add(item)
            rowItems++

            val isLast = index == times.lastIndex
            if (rowItems == 3 || isLast) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentRow.forEach { slot ->
                        TimeOptionButton(
                            option = slot,
                            selected = selectedTime?.hour == slot.hour &&
                                    selectedTime?.minute == slot.minute,
                            onClick = { onSelectTime(slot) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    repeat(3 - currentRow.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                if (!isLast) {
                    Spacer(Modifier.height(10.dp))
                }

                currentRow = mutableListOf()
                rowItems = 0
            }
        }
    }
}

@Composable
private fun TimeOptionButton(
    option: AppointmentTimeOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    !option.enabled -> AppBackground
                    selected -> BrandBlue
                    else -> White
                }
            )
            .border(
                width = if (selected || !option.enabled) 0.dp else 1.dp,
                color = BorderSoft,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = option.enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = option.label,
            color = when {
                !option.enabled -> BlueGrayTextLight
                selected -> White
                else -> TextPrimary
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun LocationStepContent(
    addresses: List<Address>,
    selectedAddressId: String,
    selectedAddress: Address?,
    isLoadingAddresses: Boolean,
    onSelectAddress: (String) -> Unit
) {
    Text(
        text = "UBICACIÓN DEL SERVICIO",
        color = BrandBlue,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.1.sp
    )

    Spacer(Modifier.height(16.dp))

    when {
        isLoadingAddresses -> {
            Text(
                text = "Cargando ubicaciones guardadas...",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        addresses.isEmpty() -> {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AppBackground),
                border = BorderStroke(1.dp, BorderSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "No tienes ubicaciones registradas.",
                        color = BrandRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Debes registrar al menos una dirección guardada para poder solicitar la cita.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        else -> {
            selectedAddress?.let { address ->
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = AppBackground),
                    border = BorderStroke(1.dp, BorderSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = TablerIcons.MapPin,
                                contentDescription = null,
                                tint = BrandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = address.alias.ifBlank { "Sin alias" },
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = listOf(
                                        address.district,
                                        address.canton,
                                        address.province,
                                        address.reference
                                    ).filter { it.isNotBlank() }
                                        .joinToString(", "),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
            }

            Text(
                text = "Selecciona una ubicación guardada",
                color = BlueGrayTextLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            addresses.forEachIndexed { index, address ->
                SavedAddressOptionCard(
                    address = address,
                    selected = address.id == selectedAddressId,
                    onClick = { onSelectAddress(address.id) }
                )

                if (index < addresses.lastIndex) {
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun SavedAddressOptionCard(
    address: Address,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) BrandBlue.copy(alpha = 0.08f) else White
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) BrandBlue else BorderSoftAlt
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (selected) BrandBlue else White)
                    .border(
                        width = 2.dp,
                        color = if (selected) BrandBlue else BorderSoft,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(White)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.alias.ifBlank { "Ubicación" },
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = listOf(
                        address.district,
                        address.canton,
                        address.province
                    ).filter { it.isNotBlank() }.joinToString(", "),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )

                if (address.reference.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = address.reference,
                        color = BlueGrayText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestAppointmentSuccessContent(
    mainService: String,
    dateTime: String,
    address: String,
    onOpenRequests: () -> Unit,
    onOpenHome: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BrandBlue,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBlue)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandBlue)
                    .systemBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Confirmación",
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Surface(
                color = White,
                shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(BrandBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = TablerIcons.Check,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(Modifier.height(22.dp))

                    Text(
                        text = "¡Solicitud Enviada!",
                        color = TextPrimary,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(StatusPendingBackground)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(StatusPendingText)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Estado: Solicitada",
                            color = StatusPendingText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.height(26.dp))

                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = BorderStroke(1.dp, BorderSoft),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
                        ) {
                            Text(
                                text = "DETALLES DE LA CITA",
                                color = BrandBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.1.sp
                            )

                            Spacer(Modifier.height(18.dp))

                            SuccessDetailRow(
                                icon = {
                                    Icon(
                                        imageVector = TablerIcons.Briefcase,
                                        contentDescription = null,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = "SERVICIO",
                                value = mainService
                            )

                            Spacer(Modifier.height(14.dp))
                            Divider(color = DividerSoft, thickness = 1.dp)
                            Spacer(Modifier.height(14.dp))

                            SuccessDetailRow(
                                icon = {
                                    Icon(
                                        imageVector = TablerIcons.CalendarEvent,
                                        contentDescription = null,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = "FECHA Y HORA",
                                value = dateTime
                            )

                            Spacer(Modifier.height(14.dp))
                            Divider(color = DividerSoft, thickness = 1.dp)
                            Spacer(Modifier.height(14.dp))

                            SuccessDetailRow(
                                icon = {
                                    Icon(
                                        imageVector = TablerIcons.MapPin,
                                        contentDescription = null,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = "DIRECCIÓN",
                                value = address
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandRed.copy(alpha = 0.05f)),
                        border = BorderStroke(1.dp, BrandRed.copy(alpha = 0.10f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = TablerIcons.Check,
                                contentDescription = null,
                                tint = BrandRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "La cita se creó con estado pending y con snapshot de la dirección seleccionada.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onOpenRequests,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = "Ir a mis solicitudes",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onOpenHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = BrandBlue
                        ),
                        border = BorderStroke(1.dp, BorderBlueLight)
                    ) {
                        Text(
                            text = "Volver al inicio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessDetailRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BrandBlue.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = BlueGrayTextLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 24.sp
            )
        }
    }
}

private fun formatPrice(value: Double): String {
    val rounded = round(value * 100) / 100.0
    return if (rounded % 1.0 == 0.0) "₡${rounded.toInt()}" else "₡$rounded"
}