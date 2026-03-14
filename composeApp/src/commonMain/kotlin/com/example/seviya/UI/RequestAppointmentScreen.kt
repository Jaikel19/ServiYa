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
import com.example.shared.domain.entity.AppointmentLocation
import com.example.shared.domain.entity.AppointmentService
import com.example.shared.domain.entity.Service
import com.example.shared.domain.entity.WorkerSchedule
import com.example.shared.presentation.requestAppointment.RequestAppointmentUiState
import com.example.shared.presentation.requestAppointment.RequestAppointmentViewModel
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
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

private val APPOINTMENT_TIME_ZONE = TimeZone.of("America/Costa_Rica")
private const val MONTH_SELECTION_RANGE = 6

data class RequestAppointmentDraft(
    val clientId: String,
    val clientName: String,
    val workerId: String,
    val workerName: String,
    val workerImageUrl: String = "",
    val workerProvince: String = "",
    val selectedServices: List<Service>,
    val schedule: List<WorkerSchedule>,
    val workerAppointments: List<Appointment> = emptyList(),
    val travelTimeMinutes: Int = 0,
    val currentTime: CurrentTimeSnapshot = CurrentTimeSnapshot()
)

private data class AppointmentMonthOption(
    val monthStart: LocalDate,
    val shortLabel: String,
    val yearLabel: String,
    val fullLabel: String
)

private data class AppointmentDayOption(
    val date: LocalDate,
    val topLabel: String,
    val dayNumber: String,
    val fullLabel: String
)

private data class AppointmentTimeOption(
    val hour: Int,
    val minute: Int,
    val label: String,
    val enabled: Boolean = true
)

private data class BusyAppointmentRange(
    val startMinutes: Int,
    val endMinutes: Int
)

data class CurrentTimeSnapshot(
    val epochMillis: Long = 0L,
    val currentDayKey: String = "",
    val currentMinutes: Int = 0,
    val todayYear: Int = 0,
    val todayMonth: Int = 0,
    val todayDay: Int = 0
)

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
        draft.selectedServices.sumOf { it.cost }
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
                monthsAhead = MONTH_SELECTION_RANGE
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
                                    selectedTime = buildAvailableTimes(
                                        date = firstDay?.date,
                                        schedule = draft.schedule,
                                        existingAppointments = draft.workerAppointments,
                                        currentTime = draft.currentTime,
                                        requestedServiceDurationMinutes = totalDurationMinutes,
                                        requestedTravelTimeMinutes = draft.travelTimeMinutes
                                    ).firstOrNull { it.enabled }
                                },
                                availableDays = availableDays,
                                selectedDay = selectedDay,
                                onSelectDay = { day ->
                                    selectedDay = day
                                    selectedTime = buildAvailableTimes(
                                        date = day.date,
                                        schedule = draft.schedule,
                                        existingAppointments = draft.workerAppointments,
                                        currentTime = draft.currentTime,
                                        requestedServiceDurationMinutes = totalDurationMinutes,
                                        requestedTravelTimeMinutes = draft.travelTimeMinutes
                                    ).firstOrNull { it.enabled }
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

                                    val appointmentMillis = toAppointmentEpochMillis(
                                        date = chosenDay.date,
                                        time = chosenTime
                                    )

                                    val appointmentEndEpochMillis = toAppointmentEndEpochMillis(
                                        date = chosenDay.date,
                                        time = chosenTime,
                                        blockingDurationMinutes = blockingDurationMinutes
                                    )

                                    val appointmentEndDateTime = Instant
                                        .fromEpochMilliseconds(appointmentEndEpochMillis)
                                        .toLocalDateTime(APPOINTMENT_TIME_ZONE)

                                    val now = safeEpochMillis(
                                        snapshot = draft.currentTime,
                                        fallback = appointmentMillis
                                    )

                                    onSubmitAppointment(
                                        Appointment(
                                            clientId = draft.clientId,
                                            clientName = draft.clientName,
                                            workerId = draft.workerId,
                                            workerName = draft.workerName,
                                            date = appointmentMillis,
                                            startTime = format24Hour(chosenTime.hour, chosenTime.minute),
                                            endTime = format24Hour(
                                                appointmentEndDateTime.hour,
                                                appointmentEndDateTime.minute
                                            ),
                                            endDate = appointmentEndEpochMillis,
                                            status = "pending",
                                            clientAddressId = chosenAddress.id,
                                            location = AppointmentLocation(
                                                alias = chosenAddress.alias,
                                                province = chosenAddress.province,
                                                district = chosenAddress.district,
                                                canton = chosenAddress.canton,
                                                latitude = chosenAddress.latitude,
                                                longitude = chosenAddress.longitude,
                                                reference = chosenAddress.reference
                                            ),
                                            services = draft.selectedServices.map {
                                                AppointmentService(
                                                    id = it.id,
                                                    name = it.name,
                                                    cost = it.cost
                                                )
                                            },
                                            totalCost = totalCost,
                                            totalDurationMinutes = totalDurationMinutes,
                                            blockingDurationMinutes = blockingDurationMinutes,
                                            travelTimeMinutesSnapshot = draft.travelTimeMinutes,
                                            createdAt = now,
                                            updatedAt = now
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
                text = formatPrice(service.cost),
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

private fun isWorkerAvailable(schedule: List<WorkerSchedule>): Boolean {
    return schedule.any { it.enabled && it.timeBlocks.isNotEmpty() }
}

private fun hasUsableCurrentTime(snapshot: CurrentTimeSnapshot): Boolean {
    if (snapshot.epochMillis > 0L) return true

    val hasExplicitDate =
        snapshot.todayYear in 1..9999 &&
                snapshot.todayMonth in 1..12 &&
                snapshot.todayDay in 1..31

    return hasExplicitDate
}

private fun snapshotLocalDateTime(snapshot: CurrentTimeSnapshot): LocalDateTime? {
    val millis = snapshot.epochMillis.takeIf { it > 0L } ?: return null
    return Instant
        .fromEpochMilliseconds(millis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)
}

private fun resolveToday(snapshot: CurrentTimeSnapshot): LocalDate? {
    snapshotLocalDateTime(snapshot)?.let { localDateTime ->
        return localDateTime.date
    }

    val hasExplicitDate =
        snapshot.todayYear in 1..9999 &&
                snapshot.todayMonth in 1..12 &&
                snapshot.todayDay in 1..31

    if (hasExplicitDate) {
        return LocalDate(
            year = snapshot.todayYear,
            monthNumber = snapshot.todayMonth,
            dayOfMonth = snapshot.todayDay
        )
    }

    return null
}

private fun resolveCurrentMinutes(snapshot: CurrentTimeSnapshot): Int {
    snapshotLocalDateTime(snapshot)?.let { localDateTime ->
        return (localDateTime.hour * 60) + localDateTime.minute
    }

    return snapshot.currentMinutes.coerceAtLeast(0)
}

private fun buildAvailableMonths(
    currentTime: CurrentTimeSnapshot,
    monthsAhead: Int = 6
): List<AppointmentMonthOption> {
    val today = resolveToday(currentTime) ?: return emptyList()
    val currentMonthStart = LocalDate(
        year = today.year,
        monthNumber = today.monthNumber,
        dayOfMonth = 1
    )

    return (0 until monthsAhead.coerceAtLeast(1)).map { index ->
        val monthDate = currentMonthStart.plus(DatePeriod(months = index))
        AppointmentMonthOption(
            monthStart = monthDate,
            shortLabel = shortMonthLabel(monthDate.monthNumber),
            yearLabel = monthDate.year.toString(),
            fullLabel = fullMonthLabel(monthDate.year, monthDate.monthNumber)
        )
    }
}

private fun buildAvailableDays(
    schedule: List<WorkerSchedule>,
    existingAppointments: List<Appointment>,
    currentTime: CurrentTimeSnapshot,
    selectedMonth: AppointmentMonthOption?,
    requestedServiceDurationMinutes: Int,
    requestedTravelTimeMinutes: Int
): List<AppointmentDayOption> {
    val today = resolveToday(currentTime) ?: return emptyList()
    val month = selectedMonth ?: return emptyList()

    val monthStart = month.monthStart
    val monthEnd = lastDayOfMonth(
        year = monthStart.year,
        monthNumber = monthStart.monthNumber
    )

    val startDate = if (
        monthStart.year == today.year &&
        monthStart.monthNumber == today.monthNumber
    ) {
        today
    } else {
        monthStart
    }

    if (startDate > monthEnd) return emptyList()

    val result = mutableListOf<AppointmentDayOption>()
    var cursor = startDate

    while (cursor <= monthEnd) {
        val slotsForDay = buildAvailableTimes(
            date = cursor,
            schedule = schedule,
            existingAppointments = existingAppointments,
            currentTime = currentTime,
            requestedServiceDurationMinutes = requestedServiceDurationMinutes,
            requestedTravelTimeMinutes = requestedTravelTimeMinutes
        )

        val shouldShowDay = slotsForDay.any { it.enabled } || cursor == today

        if (shouldShowDay) {
            result.add(
                AppointmentDayOption(
                    date = cursor,
                    topLabel = topDayLabel(cursor, today),
                    dayNumber = cursor.dayOfMonth.toString(),
                    fullLabel = fullDateLabel(cursor)
                )
            )
        }

        cursor = cursor.plus(DatePeriod(days = 1))
    }

    return result
}

private fun safeEpochMillis(
    snapshot: CurrentTimeSnapshot,
    fallback: Long
): Long {
    return snapshot.epochMillis.takeIf { it > 0L } ?: fallback
}

private fun buildAvailableTimes(
    date: LocalDate?,
    schedule: List<WorkerSchedule>,
    existingAppointments: List<Appointment>,
    currentTime: CurrentTimeSnapshot,
    requestedServiceDurationMinutes: Int,
    requestedTravelTimeMinutes: Int
): List<AppointmentTimeOption> {
    if (date == null) return emptyList()

    val scheduleForDay = findScheduleForDate(date, schedule) ?: return emptyList()
    if (!scheduleForDay.enabled || scheduleForDay.timeBlocks.isEmpty()) return emptyList()

    val validBlocks = scheduleForDay.timeBlocks.filter {
        it.start.isNotBlank() && it.end.isNotBlank()
    }
    if (validBlocks.isEmpty()) return emptyList()

    val today = resolveToday(currentTime)
    val isToday = today != null && date == today
    val currentMinutes = resolveCurrentMinutes(currentTime)

    val busyRanges = buildBusyRangesForDate(
        date = date,
        appointments = existingAppointments
    )

    val serviceDuration = requestedServiceDurationMinutes.coerceAtLeast(30)
    val requestedTravel = requestedTravelTimeMinutes.coerceAtLeast(0)

    val slots = mutableListOf<AppointmentTimeOption>()

    validBlocks.forEach { block ->
        val blockStart = parseMinutes(block.start) ?: return@forEach
        val blockEnd = parseMinutes(block.end) ?: return@forEach
        if (blockEnd <= blockStart) return@forEach

        var cursor = blockStart

        while (cursor < blockEnd) {
            val serviceStart = cursor
            val serviceEnd = serviceStart + serviceDuration

            val candidateBusyStart = (serviceStart - requestedTravel).coerceAtLeast(0)
            val candidateBusyEnd = serviceEnd + requestedTravel

            val isFutureTime = if (isToday) {
                serviceStart > currentMinutes
            } else {
                true
            }

            val fitsInsideWorkerSchedule =
                candidateBusyStart >= blockStart && candidateBusyEnd <= blockEnd

            val overlapsExistingAppointment = busyRanges.any { busy ->
                rangesOverlap(
                    startA = candidateBusyStart,
                    endA = candidateBusyEnd,
                    startB = busy.startMinutes,
                    endB = busy.endMinutes
                )
            }

            if (isFutureTime && fitsInsideWorkerSchedule && !overlapsExistingAppointment) {
                slots.add(
                    AppointmentTimeOption(
                        hour = serviceStart / 60,
                        minute = serviceStart % 60,
                        label = formatAmPm(serviceStart / 60, serviceStart % 60),
                        enabled = true
                    )
                )
            }

            cursor += 30
        }
    }

    return slots
        .distinctBy { "${it.hour}:${it.minute}" }
        .sortedWith(compareBy<AppointmentTimeOption> { it.hour }.thenBy { it.minute })
}

private fun buildBusyRangesForDate(
    date: LocalDate,
    appointments: List<Appointment>
): List<BusyAppointmentRange> {
    return appointments
        .filter { appointment ->
            appointmentBlocksSchedule(appointment.status) &&
                    appointmentBelongsToDate(appointment, date)
        }
        .mapNotNull { appointment ->
            val startMinutes = resolveAppointmentStartMinutes(appointment) ?: return@mapNotNull null
            val busyEndMinutes = resolveAppointmentBusyEndMinutes(appointment, startMinutes) ?: return@mapNotNull null
            val travelBuffer = appointment.travelTimeMinutesSnapshot.coerceAtLeast(0)

            val busyStart = (startMinutes - travelBuffer).coerceAtLeast(0)
            val busyEnd = busyEndMinutes.coerceAtMost(24 * 60)

            if (busyEnd <= busyStart) {
                null
            } else {
                BusyAppointmentRange(
                    startMinutes = busyStart,
                    endMinutes = busyEnd
                )
            }
        }
        .distinctBy { "${it.startMinutes}-${it.endMinutes}" }
        .sortedBy { it.startMinutes }
}

private fun resolveAppointmentBusyEndMinutes(
    appointment: Appointment,
    startMinutes: Int
): Int? {
    if (appointment.blockingDurationMinutes > 0) {
        return startMinutes + appointment.blockingDurationMinutes
    }

    if (appointment.endTime.isNotBlank()) {
        return parseMinutes(appointment.endTime)
    }

    if (appointment.endDate > 0L) {
        val endMinutes = epochMillisToMinutesOfDay(appointment.endDate)
        if (endMinutes != null) return endMinutes
    }

    if (appointment.totalDurationMinutes > 0) {
        return startMinutes + appointment.totalDurationMinutes
    }

    return startMinutes + 30
}

private fun appointmentBelongsToDate(
    appointment: Appointment,
    selectedDate: LocalDate
): Boolean {
    appointment.startTime
        .takeIf { it.isNotBlank() }
        ?.let { startTimeText ->
            extractLocalDateFromText(startTimeText)?.let { parsedDate ->
                return parsedDate == selectedDate
            }
        }

    val appointmentDate = epochMillisToLocalDate(appointment.date) ?: return false
    return appointmentDate == selectedDate
}

private fun extractLocalDateFromText(value: String): LocalDate? {
    val text = value.trim()
    val datePart = text.substringBefore("T").substringBefore(" ")

    val parts = datePart.split("-")
    if (parts.size != 3) return null

    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].toIntOrNull() ?: return null

    return try {
        LocalDate(
            year = year,
            monthNumber = month,
            dayOfMonth = day
        )
    } catch (e: Exception) {
        null
    }
}

private fun resolveAppointmentStartMinutes(
    appointment: Appointment
): Int? {
    return when {
        appointment.startTime.isNotBlank() -> parseMinutes(appointment.startTime)
        else -> epochMillisToMinutesOfDay(appointment.date)
    }
}

private fun resolveAppointmentEndMinutes(
    appointment: Appointment,
    startMinutes: Int
): Int? {
    if (appointment.endTime.isNotBlank()) {
        return parseMinutes(appointment.endTime)
    }

    if (appointment.endDate > 0L) {
        val endMinutes = epochMillisToMinutesOfDay(appointment.endDate)
        if (endMinutes != null) return endMinutes
    }

    if (appointment.totalDurationMinutes > 0) {
        return startMinutes + appointment.totalDurationMinutes
    }

    if (appointment.blockingDurationMinutes > 0) {
        return startMinutes + appointment.blockingDurationMinutes
    }

    return startMinutes + 30
}

private fun appointmentBlocksSchedule(status: String): Boolean {
    return when (status.trim().lowercase()) {
        "cancelled", "canceled", "rejected", "completed", "complete" -> false
        else -> true
    }
}

private fun rangesOverlap(
    startA: Int,
    endA: Int,
    startB: Int,
    endB: Int
): Boolean {
    return startA < endB && endA > startB
}

private fun epochMillisToLocalDate(epochMillis: Long): LocalDate? {
    if (epochMillis <= 0L) return null
    return Instant
        .fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)
        .date
}

private fun epochMillisToMinutesOfDay(epochMillis: Long): Int? {
    if (epochMillis <= 0L) return null
    val localDateTime = Instant
        .fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(APPOINTMENT_TIME_ZONE)
    return (localDateTime.hour * 60) + localDateTime.minute
}

private fun findScheduleForDate(
    date: LocalDate,
    schedule: List<WorkerSchedule>
): WorkerSchedule? {
    val expectedDayKey = dayKeyFromWeekday(date.dayOfWeek)
    val expectedDayNumber = dayNumberFromWeekday(date.dayOfWeek)

    return schedule.firstOrNull {
        it.enabled && (
                it.dayKey.trim().lowercase() == expectedDayKey ||
                        it.dayNumber == expectedDayNumber
                )
    }
}

private fun dayNumberFromWeekday(dayOfWeek: DayOfWeek): Int {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 7
    }
}

private fun parseMinutes(value: String): Int? {
    val normalized = value
        .trim()
        .substringAfterLast("T")
        .substringAfterLast(" ")

    val match = Regex("""^(\d{1,2}):(\d{2})$""").find(normalized) ?: return null

    val hour = match.groupValues[1].toIntOrNull() ?: return null
    val minute = match.groupValues[2].toIntOrNull() ?: return null

    if (hour !in 0..23 || minute !in 0..59) return null

    return hour * 60 + minute
}

private fun formatAmPm(hour24: Int, minute: Int): String {
    val suffix = if (hour24 >= 12) "PM" else "AM"
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val minuteText = minute.toString().padStart(2, '0')
    return "${hour12.toString().padStart(2, '0')}:$minuteText $suffix"
}

private fun format24Hour(hour24: Int, minute: Int): String {
    return "${hour24.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

private fun dayKeyFromWeekday(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "monday"
        DayOfWeek.TUESDAY -> "tuesday"
        DayOfWeek.WEDNESDAY -> "wednesday"
        DayOfWeek.THURSDAY -> "thursday"
        DayOfWeek.FRIDAY -> "friday"
        DayOfWeek.SATURDAY -> "saturday"
        DayOfWeek.SUNDAY -> "sunday"
    }
}

private fun topDayLabel(
    date: LocalDate,
    today: LocalDate
): String {
    return when {
        date == today -> "HOY"
        else -> when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "LUN"
            DayOfWeek.TUESDAY -> "MAR"
            DayOfWeek.WEDNESDAY -> "MIÉ"
            DayOfWeek.THURSDAY -> "JUE"
            DayOfWeek.FRIDAY -> "VIE"
            DayOfWeek.SATURDAY -> "SÁB"
            DayOfWeek.SUNDAY -> "DOM"
        }
    }
}

private fun fullDateLabel(date: LocalDate): String {
    val weekday = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "Lunes"
        DayOfWeek.TUESDAY -> "Martes"
        DayOfWeek.WEDNESDAY -> "Miércoles"
        DayOfWeek.THURSDAY -> "Jueves"
        DayOfWeek.FRIDAY -> "Viernes"
        DayOfWeek.SATURDAY -> "Sábado"
        DayOfWeek.SUNDAY -> "Domingo"
    }

    val month = when (date.monthNumber) {
        1 -> "enero"
        2 -> "febrero"
        3 -> "marzo"
        4 -> "abril"
        5 -> "mayo"
        6 -> "junio"
        7 -> "julio"
        8 -> "agosto"
        9 -> "septiembre"
        10 -> "octubre"
        11 -> "noviembre"
        12 -> "diciembre"
        else -> ""
    }

    return "$weekday, ${date.dayOfMonth} $month"
}

private fun fullDateTimeLabel(
    date: LocalDate,
    time: AppointmentTimeOption
): String {
    return "${fullDateLabel(date)} • ${time.label}"
}

private fun toAppointmentEpochMillis(
    date: LocalDate,
    time: AppointmentTimeOption
): Long {
    val localDateTime = LocalDateTime(
        year = date.year,
        monthNumber = date.monthNumber,
        dayOfMonth = date.dayOfMonth,
        hour = time.hour,
        minute = time.minute,
        second = 0,
        nanosecond = 0
    )

    return localDateTime
        .toInstant(APPOINTMENT_TIME_ZONE)
        .toEpochMilliseconds()
}

private fun toAppointmentEndEpochMillis(
    date: LocalDate,
    time: AppointmentTimeOption,
    blockingDurationMinutes: Int
): Long {
    val startEpochMillis = toAppointmentEpochMillis(date, time)
    val durationMillis = blockingDurationMinutes.coerceAtLeast(0) * 60_000L
    return startEpochMillis + durationMillis
}

private fun calculateTotalServiceDurationMinutes(services: List<Service>): Int {
    return services.sumOf { parseDurationToMinutes(it.duration) }
}

private fun parseDurationToMinutes(duration: String): Int {
    val text = duration.trim().lowercase()

    if (text.isBlank()) return 0

    val hourRegex = Regex("""(\d+)\s*(h|hora|horas)""")
    val minuteRegex = Regex("""(\d+)\s*(m|min|mins|minuto|minutos)""")

    val hours = hourRegex.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
    val minutes = minuteRegex.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0

    if (hours > 0 || minutes > 0) {
        return (hours * 60) + minutes
    }

    return text.toIntOrNull() ?: 0
}

private fun lastDayOfMonth(
    year: Int,
    monthNumber: Int
): LocalDate {
    val firstDay = LocalDate(
        year = year,
        monthNumber = monthNumber,
        dayOfMonth = 1
    )

    return firstDay.plus(DatePeriod(months = 1, days = -1))
}

private fun shortMonthLabel(monthNumber: Int): String {
    return when (monthNumber) {
        1 -> "ENE"
        2 -> "FEB"
        3 -> "MAR"
        4 -> "ABR"
        5 -> "MAY"
        6 -> "JUN"
        7 -> "JUL"
        8 -> "AGO"
        9 -> "SEP"
        10 -> "OCT"
        11 -> "NOV"
        12 -> "DIC"
        else -> ""
    }
}

private fun fullMonthLabel(
    year: Int,
    monthNumber: Int
): String {
    val month = when (monthNumber) {
        1 -> "enero"
        2 -> "febrero"
        3 -> "marzo"
        4 -> "abril"
        5 -> "mayo"
        6 -> "junio"
        7 -> "julio"
        8 -> "agosto"
        9 -> "septiembre"
        10 -> "octubre"
        11 -> "noviembre"
        12 -> "diciembre"
        else -> ""
    }

    return "$month $year"
}