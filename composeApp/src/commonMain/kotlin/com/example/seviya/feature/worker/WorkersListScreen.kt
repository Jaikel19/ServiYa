package com.example.seviya.feature.worker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BorderBlueLight
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.ImageBlueSoftAlt
import com.example.seviya.core.designsystem.theme.Inactive
import com.example.seviya.core.designsystem.theme.TextPrimaryAlt
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.WorkZone
import com.example.shared.domain.entity.WorkerListItemData
import com.example.shared.domain.entity.WorkerSchedule
import com.example.shared.presentation.requestAppointment.CurrentTimeSnapshot
import com.example.shared.presentation.workersList.WorkersListUiState
import com.example.shared.presentation.workersList.WorkersListViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.ChevronRight
import compose.icons.tablericons.Heart
import compose.icons.tablericons.MapPin
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.round

private data class SimpleDateValue(
    val year: Int,
    val month: Int,
    val day: Int
)

private data class CalendarDayCell(
    val date: SimpleDateValue?
)

@Composable
fun WorkersListRoute(
    clientId: String,
    viewModel: WorkersListViewModel,
    currentTime: CurrentTimeSnapshot,
    selectedCategoryId: String? = null,
    selectedCategoryName: String? = null,
    avatarPainter: Painter? = null,
    onWorkerClick: (String) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(clientId) {
        viewModel.loadWorkers()
        viewModel.loadFavoriteWorkerIds(clientId)
        viewModel.loadClientAddresses(clientId)
    }

    WorkersListScreen(
        state = state,
        currentTime = currentTime,
        selectedCategoryId = selectedCategoryId,
        selectedCategoryName = selectedCategoryName,
        avatarPainter = avatarPainter,
        onWorkerClick = onWorkerClick,
        onFavoriteClick = { workerId ->
            viewModel.toggleFavorite(clientId, workerId)
        },
        onSelectAddress = { addressId ->
            viewModel.selectAddress(addressId)
        },
        onFavoritesClick = onFavoritesClick,
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
    currentTime: CurrentTimeSnapshot,
    selectedCategoryId: String? = null,
    selectedCategoryName: String? = null,
    avatarPainter: Painter? = null,
    onWorkerClick: (String) -> Unit = {},
    onFavoriteClick: (String) -> Unit = {},
    onSelectAddress: (String?) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {}
) {
    val todayDateText = remember(
        currentTime.todayYear,
        currentTime.todayMonth,
        currentTime.todayDay
    ) {
        buildDateKeyFromSnapshot(currentTime)
    }

    val todayDate = remember(todayDateText) {
        parseSimpleDate(todayDateText)
    }

    val safeTodayDate = todayDate ?: SimpleDateValue(
        year = if (currentTime.todayYear > 0) currentTime.todayYear else 2000,
        month = if (currentTime.todayMonth in 1..12) currentTime.todayMonth else 1,
        day = if (currentTime.todayDay in 1..31) currentTime.todayDay else 1
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedDateText by remember { mutableStateOf("") }
    var showLocationMenu by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }

    val selectedDate = remember(selectedDateText) {
        parseSimpleDate(selectedDateText.trim())
    }

    var visibleMonthYear by remember(safeTodayDate.year) {
        mutableStateOf(safeTodayDate.year)
    }
    var visibleMonth by remember(safeTodayDate.month) {
        mutableStateOf(safeTodayDate.month)
    }

    LaunchedEffect(selectedDateText) {
        if (selectedDateText.isBlank()) {
            visibleMonthYear = safeTodayDate.year
            visibleMonth = safeTodayDate.month
        } else {
            selectedDate?.let {
                visibleMonthYear = it.year
                visibleMonth = it.month
            }
        }
    }

    val selectedAddress = remember(state.savedAddresses, state.selectedAddressId) {
        state.savedAddresses.firstOrNull { it.id == state.selectedAddressId }
    }

    val visibleWorkers = remember(
        state.workers,
        state.savedAddresses,
        state.selectedAddressId,
        selectedDate,
        searchQuery,
        selectedCategoryId,
        selectedCategoryName,
        currentTime.currentMinutes,
        currentTime.todayYear,
        currentTime.todayMonth,
        currentTime.todayDay
    ) {
        state.workers
            .filter { worker ->
                val matchCategoryId = selectedCategoryId?.let { it in worker.categoryIds } ?: true
                val matchCategoryName = selectedCategoryName?.let { categoryName ->
                    worker.categoryNames.any { it.equals(categoryName, ignoreCase = true) }
                } ?: true
                val matchSearch = matchesWorkerSearch(worker, searchQuery)

                val matchLocation = selectedAddress?.let { address ->
                    workerMatchesAddress(worker, address)
                } ?: true

                val matchAvailability = selectedDate?.let { date ->
                    workerHasAvailabilityOnDate(
                        worker = worker,
                        date = date,
                        currentTime = currentTime
                    )
                } ?: true

                matchCategoryId &&
                        matchCategoryName &&
                        matchSearch &&
                        matchLocation &&
                        matchAvailability
            }
            .sortedByDescending { it.stars }
    }

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
            WorkersHeader(
                selectedCategoryName = selectedCategoryName,
                onFavoritesClick = onFavoritesClick
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-18).dp),
                color = AppBackgroundAlt,
                shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    WorkerSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CompactFilterChip(
                                modifier = Modifier.weight(1f),
                                icon = TablerIcons.MapPin,
                                text = selectedAddress?.let { addressShortLabel(it) } ?: "Ubicación",
                                enabled = state.savedAddresses.isNotEmpty(),
                                onClick = {
                                    showLocationMenu = true
                                    showCalendar = false
                                }
                            ) {
                                StyledLocationDropdown(
                                    expanded = showLocationMenu,
                                    addresses = state.savedAddresses,
                                    selectedAddressId = state.selectedAddressId,
                                    onDismiss = { showLocationMenu = false },
                                    onClear = {
                                        onSelectAddress(null)
                                        showLocationMenu = false
                                    },
                                    onSelect = { address ->
                                        onSelectAddress(address.id)
                                        showLocationMenu = false
                                    }
                                )
                            }

                            CompactFilterChip(
                                modifier = Modifier.weight(1f),
                                icon = TablerIcons.CalendarEvent,
                                text = selectedDate?.let { calendarChipDateLabel(it, safeTodayDate) } ?: "Fecha",
                                enabled = hasUsableCurrentTimeSnapshot(currentTime),
                                onClick = {
                                    showCalendar = !showCalendar
                                    showLocationMenu = false
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    searchQuery = ""
                                    selectedDateText = ""
                                    onSelectAddress(null)
                                    showLocationMenu = false
                                    showCalendar = false
                                    visibleMonthYear = safeTodayDate.year
                                    visibleMonth = safeTodayDate.month
                                }
                            ) {
                                Text(
                                    text = "Limpiar",
                                    color = BrandBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (showCalendar && hasUsableCurrentTimeSnapshot(currentTime)) {
                            CompactCalendarCard(
                                visibleMonthYear = visibleMonthYear,
                                visibleMonth = visibleMonth,
                                selectedDate = selectedDate,
                                todayDate = safeTodayDate,
                                onPreviousMonth = {
                                    val previous = previousMonth(visibleMonthYear, visibleMonth)
                                    visibleMonthYear = previous.first
                                    visibleMonth = previous.second
                                },
                                onNextMonth = {
                                    val next = nextMonth(visibleMonthYear, visibleMonth)
                                    visibleMonthYear = next.first
                                    visibleMonth = next.second
                                },
                                onSelectDate = { chosenDate ->
                                    if (compareDates(chosenDate, safeTodayDate) >= 0) {
                                        selectedDateText = formatDateKey(chosenDate)
                                        showCalendar = false
                                    }
                                }
                            )
                        }
                    }

                    when {
                        state.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Cargando trabajadores...",
                                    color = TextSecondary,
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
                                    color = BrandRed,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
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
                                    text = "No hay trabajadores que coincidan con los filtros seleccionados.",
                                    color = TextSecondary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
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
                                        isFavorite = state.favoriteWorkerIds.contains(worker.workerId),
                                        avatarPainter = avatarPainter,
                                        onClick = { onWorkerClick(worker.workerId) },
                                        onFavoriteClick = { onFavoriteClick(worker.workerId) }
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
private fun StyledLocationDropdown(
    expanded: Boolean,
    addresses: List<Address>,
    selectedAddressId: String?,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onSelect: (Address) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = White,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color(0xFFE8ECF4))
    ) {
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedAddressId == null) BrandBlue else Color(0xFFD9E3F5)
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Todas las ubicaciones",
                            color = TextPrimaryAlt,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Sin aplicar filtro de zona",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            onClick = onClear
        )

        addresses.forEach { address ->
            val isSelected = selectedAddressId == address.id

            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) BrandBlue else Color(0xFFD9E3F5)
                                )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = address.alias.ifBlank { addressShortLabel(address) },
                                color = TextPrimaryAlt,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = addressZoneLabel(address),
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                onClick = { onSelect(address) }
            )
        }
    }
}

@Composable
private fun CompactFilterChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    menuContent: @Composable (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick),
            shape = RoundedCornerShape(999.dp),
            color = White,
            border = BorderStroke(1.dp, Color(0xFFE8ECF4)),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) TextPrimaryAlt else TextSecondary.copy(alpha = 0.60f),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    modifier = Modifier.weight(1f),
                    color = if (enabled) TextPrimaryAlt else TextSecondary.copy(alpha = 0.60f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    imageVector = TablerIcons.ChevronRight,
                    contentDescription = null,
                    tint = if (enabled) TextSecondary else TextSecondary.copy(alpha = 0.45f),
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(90f)
                )
            }
        }

        menuContent?.invoke()
    }
}

@Composable
private fun CompactCalendarCard(
    visibleMonthYear: Int,
    visibleMonth: Int,
    selectedDate: SimpleDateValue?,
    todayDate: SimpleDateValue,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (SimpleDateValue) -> Unit
) {
    val monthCells = remember(visibleMonthYear, visibleMonth) {
        buildCalendarMonthCells(visibleMonthYear, visibleMonth)
    }

    val canGoToPreviousMonth = remember(
        visibleMonthYear,
        visibleMonth,
        todayDate.year,
        todayDate.month
    ) {
        compareYearMonth(
            yearA = visibleMonthYear,
            monthA = visibleMonth,
            yearB = todayDate.year,
            monthB = todayDate.month
        ) > 0
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(22.dp),
        color = White,
        border = BorderStroke(1.dp, Color(0xFFE8ECF4)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarArrowButton(
                    rotate = 180f,
                    enabled = canGoToPreviousMonth,
                    onClick = onPreviousMonth
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${monthNameSpanish(visibleMonth)} $visibleMonthYear",
                    modifier = Modifier.weight(1f),
                    color = TextPrimaryAlt,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(8.dp))

                CalendarArrowButton(
                    rotate = 0f,
                    enabled = true,
                    onClick = onNextMonth
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            monthCells.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { cell ->
                        CalendarDayButton(
                            cell = cell,
                            selectedDate = selectedDate,
                            todayDate = todayDate,
                            onSelectDate = onSelectDate
                        )
                    }
                }

                if (rowIndex < monthCells.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarArrowButton(
    rotate: Float,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(
                if (enabled) Color(0xFFF5F7FB)
                else Color(0xFFF5F7FB).copy(alpha = 0.55f)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = TablerIcons.ChevronRight,
            contentDescription = null,
            tint = if (enabled) TextPrimaryAlt else TextSecondary.copy(alpha = 0.45f),
            modifier = Modifier
                .size(18.dp)
                .rotate(rotate)
        )
    }
}

@Composable
private fun CalendarDayButton(
    cell: CalendarDayCell,
    selectedDate: SimpleDateValue?,
    todayDate: SimpleDateValue,
    onSelectDate: (SimpleDateValue) -> Unit
) {
    val date = cell.date
    val isSelected = date != null && selectedDate == date
    val isToday = date != null && todayDate == date
    val isPastDate = date != null && compareDates(date, todayDate) < 0
    val isEnabled = date != null && !isPastDate

    Box(
        modifier = Modifier.padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected -> BrandBlue
                        isPastDate -> Color(0xFFF6F7FA)
                        else -> Color.Transparent
                    }
                )
                .border(
                    width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                    color = if (isToday && !isSelected) BrandBlue else Color.Transparent,
                    shape = CircleShape
                )
                .clickable(enabled = isEnabled) {
                    if (date != null && !isPastDate) {
                        onSelectDate(date)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date?.day?.toString().orEmpty(),
                color = when {
                    date == null -> Color.Transparent
                    isSelected -> White
                    isPastDate -> TextSecondary.copy(alpha = 0.45f)
                    else -> TextPrimaryAlt
                },
                fontSize = 14.sp,
                fontWeight = if (isToday || isSelected) FontWeight.ExtraBold else FontWeight.Medium
            )
        }
    }
}

private fun compareDates(
    first: SimpleDateValue,
    second: SimpleDateValue
): Int {
    return when {
        first.year != second.year -> first.year - second.year
        first.month != second.month -> first.month - second.month
        else -> first.day - second.day
    }
}

private fun compareYearMonth(
    yearA: Int,
    monthA: Int,
    yearB: Int,
    monthB: Int
): Int {
    return when {
        yearA != yearB -> yearA - yearB
        else -> monthA - monthB
    }
}

private fun matchesWorkerSearch(
    worker: WorkerListItemData,
    query: String
): Boolean {
    if (query.isBlank()) return true

    val normalized = normalizeText(query)

    return normalizeText(worker.name).contains(normalized) ||
            worker.categoryNames.any { normalizeText(it).contains(normalized) } ||
            normalizeText(workerProvinceText(worker)).contains(normalized)
}

private fun workerMatchesAddress(
    worker: WorkerListItemData,
    address: Address
): Boolean {
    val selectedProvince = normalizeText(address.province)
    val selectedCanton = normalizeText(address.canton)
    val selectedDistrict = normalizeText(address.district)

    if (selectedProvince.isBlank()) return true

    val workZones = workerWorkZones(worker)

    if (workZones.isEmpty()) {
        return normalizeText(workerProvinceText(worker)) == selectedProvince
    }

    val provinceZones = workZones.filter {
        normalizeText(it.province) == selectedProvince
    }

    if (provinceZones.isEmpty()) return false

    val districtZone = provinceZones.firstOrNull { zone ->
        normalizeText(zone.canton) == selectedCanton &&
                normalizeText(zone.district) == selectedDistrict
    }

    if (districtZone != null) {
        return !districtZone.blocked
    }

    val cantonZone = provinceZones.firstOrNull { zone ->
        normalizeText(zone.canton) == selectedCanton &&
                zone.district.isBlank()
    }

    if (cantonZone != null) {
        return !cantonZone.blocked
    }

    val provinceZone = provinceZones.firstOrNull { zone ->
        zone.canton.isBlank() && zone.district.isBlank()
    }

    if (provinceZone != null) {
        return !provinceZone.blocked
    }

    return false
}

@Composable
private fun WorkerSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp),
        singleLine = true,
        shape = RoundedCornerShape(999.dp),
        placeholder = {
            Text(
                text = $$"Buscar trabajador",
                color = TextSecondary
            )
        }
    )
}

@Composable
private fun WorkersHeader(
    selectedCategoryName: String?,
    onFavoritesClick: () -> Unit
) {
    val subtitle = selectedCategoryName?.takeIf { it.isNotBlank() }?.let {
        "Explora profesionales en $it"
    } ?: "Encuentra el experto ideal para hoy"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 4.dp)
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = White.copy(alpha = 0.08f)
            ) {}

            Surface(
                modifier = Modifier
                    .size(38.dp)
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
                ServiYaHeaderBadge()

                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .clickable(onClick = onFavoritesClick),
                    shape = RoundedCornerShape(18.dp),
                    color = White.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, White.copy(alpha = 0.18f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = TablerIcons.Heart,
                            contentDescription = "Favoritos",
                            tint = White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Profesionales Disponibles",
                color = White,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = White.copy(alpha = 0.82f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ServiYaHeaderBadge() {
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
private fun WorkerGridCard(
    worker: WorkerListItemData,
    isFavorite: Boolean,
    avatarPainter: Painter?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val locationText = buildWorkerLocationLine(worker).ifBlank { "Ubicación no disponible" }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp), clip = false)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = White,
        border = BorderStroke(1.dp, Color(0xFFE4ECF7))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFavorite) BrandRed.copy(alpha = 0.14f)
                        else Color(0xFFF7F8FA)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isFavorite) BrandRed.copy(alpha = 0.25f) else Color(0xFFE4EAF2),
                        shape = CircleShape
                    )
                    .clickable(onClick = onFavoriteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TablerIcons.Heart,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (isFavorite) BrandRed else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

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
                    color = TextPrimaryAlt,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                WorkerProvinceInline(
                    province = locationText
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
            tint = Inactive,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = province,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
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
                .background(White)
                .border(
                    width = 4.dp,
                    color = BorderBlueLight,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(ImageBlueSoftAlt),
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
            color = TextSecondary,
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
        color = BrandBlue,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

private fun formatPrice(value: Double): String {
    val rounded = round(value * 100) / 100.0
    return if (rounded % 1.0 == 0.0) "₡${rounded.toInt()}" else "₡$rounded"
}

private fun normalizeText(value: String): String {
    return value
        .trim()
        .lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ä", "a")
        .replace("ë", "e")
        .replace("ï", "i")
        .replace("ö", "o")
        .replace("ü", "u")
        .replace("ñ", "n")
}

private fun addressShortLabel(address: Address): String {
    return when {
        address.alias.isNotBlank() -> address.alias
        address.district.isNotBlank() -> address.district
        address.canton.isNotBlank() -> address.canton
        address.province.isNotBlank() -> address.province
        else -> "Ubicación"
    }
}

private fun addressZoneLabel(address: Address): String {
    return listOf(
        address.district,
        address.canton,
        address.province
    ).filter { it.isNotBlank() }.joinToString(", ")
}

private fun calendarChipDateLabel(
    selectedDate: SimpleDateValue,
    todayDate: SimpleDateValue
): String {
    return if (selectedDate == todayDate) {
        "Hoy"
    } else {
        "${selectedDate.day} ${monthShortNameSpanish(selectedDate.month)}"
    }
}

private fun hasUsableCurrentTimeSnapshot(snapshot: CurrentTimeSnapshot): Boolean {
    return snapshot.todayYear > 0 &&
            snapshot.todayMonth in 1..12 &&
            snapshot.todayDay in 1..31
}

private fun buildDateKeyFromSnapshot(snapshot: CurrentTimeSnapshot): String {
    if (!hasUsableCurrentTimeSnapshot(snapshot)) return ""

    val monthText = snapshot.todayMonth.toString().padStart(2, '0')
    val dayText = snapshot.todayDay.toString().padStart(2, '0')

    return "${snapshot.todayYear}-$monthText-$dayText"
}

private fun buildWorkerLocationLine(worker: WorkerListItemData): String {
    return workerProvinceText(worker)
}

private fun workerProvinceText(worker: WorkerListItemData): String = worker.province.trim()
private fun workerCantonText(worker: WorkerListItemData): String = worker.canton.trim()
private fun workerDistrictText(worker: WorkerListItemData): String = worker.district.trim()
private fun workerSchedules(worker: WorkerListItemData): List<WorkerSchedule> = worker.schedule
private fun workerAppointments(worker: WorkerListItemData): List<Appointment> = worker.appointments
private fun workerWorkZones(worker: WorkerListItemData): List<WorkZone> = worker.workZones

private fun parseSimpleDate(value: String): SimpleDateValue? {
    if (value.isBlank()) return null

    val parts = value.split("-")
    if (parts.size != 3) return null

    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].toIntOrNull() ?: return null

    if (month !in 1..12) return null

    val maxDay = daysInMonth(year, month)
    if (day !in 1..maxDay) return null

    return SimpleDateValue(year, month, day)
}

private fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)
}

private fun formatDateKey(date: SimpleDateValue): String {
    val monthText = date.month.toString().padStart(2, '0')
    val dayText = date.day.toString().padStart(2, '0')
    return "${date.year}-$monthText-$dayText"
}

private fun dayOfWeekNumber(date: SimpleDateValue): Int {
    val offsets = listOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    var year = date.year
    val month = date.month
    val day = date.day

    if (month < 3) year -= 1

    val sundayZero = (
            year +
                    year / 4 -
                    year / 100 +
                    year / 400 +
                    offsets[month - 1] +
                    day
            ) % 7

    return when (sundayZero) {
        0 -> 7
        1 -> 1
        2 -> 2
        3 -> 3
        4 -> 4
        5 -> 5
        6 -> 6
        else -> 0
    }
}

private fun previousMonth(year: Int, month: Int): Pair<Int, Int> {
    return if (month == 1) (year - 1) to 12 else year to (month - 1)
}

private fun nextMonth(year: Int, month: Int): Pair<Int, Int> {
    return if (month == 12) (year + 1) to 1 else year to (month + 1)
}

private fun monthNameSpanish(month: Int): String {
    return when (month) {
        1 -> "Enero"
        2 -> "Febrero"
        3 -> "Marzo"
        4 -> "Abril"
        5 -> "Mayo"
        6 -> "Junio"
        7 -> "Julio"
        8 -> "Agosto"
        9 -> "Septiembre"
        10 -> "Octubre"
        11 -> "Noviembre"
        12 -> "Diciembre"
        else -> ""
    }
}

private fun monthShortNameSpanish(month: Int): String {
    return when (month) {
        1 -> "ene"
        2 -> "feb"
        3 -> "mar"
        4 -> "abr"
        5 -> "may"
        6 -> "jun"
        7 -> "jul"
        8 -> "ago"
        9 -> "sep"
        10 -> "oct"
        11 -> "nov"
        12 -> "dic"
        else -> ""
    }
}

private fun buildCalendarMonthCells(
    year: Int,
    month: Int
): List<List<CalendarDayCell>> {
    val cells = mutableListOf<CalendarDayCell>()
    val firstDayOfWeek = dayOfWeekNumber(SimpleDateValue(year, month, 1))
    val leadingBlanks = (firstDayOfWeek - 1).coerceAtLeast(0)

    repeat(leadingBlanks) {
        cells.add(CalendarDayCell(date = null))
    }

    val totalDays = daysInMonth(year, month)
    for (day in 1..totalDays) {
        cells.add(CalendarDayCell(date = SimpleDateValue(year, month, day)))
    }

    while (cells.size % 7 != 0) {
        cells.add(CalendarDayCell(date = null))
    }

    return cells.chunked(7)
}

private fun findScheduleForDate(
    date: SimpleDateValue,
    schedule: List<WorkerSchedule>
): WorkerSchedule? {
    val expectedDayNumber = dayOfWeekNumber(date)
    val expectedDayKey = dayKeyFromNumber(expectedDayNumber)

    return schedule.firstOrNull {
        it.enabled && (
                it.dayNumber == expectedDayNumber ||
                        it.dayKey.trim().lowercase() == expectedDayKey
                )
    }
}

private fun dayKeyFromNumber(dayNumber: Int): String {
    return when (dayNumber) {
        1 -> "monday"
        2 -> "tuesday"
        3 -> "wednesday"
        4 -> "thursday"
        5 -> "friday"
        6 -> "saturday"
        7 -> "sunday"
        else -> ""
    }
}

private fun workerHasAvailabilityOnDate(
    worker: WorkerListItemData,
    date: SimpleDateValue,
    currentTime: CurrentTimeSnapshot
): Boolean {
    val scheduleForDay = findScheduleForDate(date, workerSchedules(worker)) ?: return false
    if (!scheduleForDay.enabled || scheduleForDay.timeBlocks.isEmpty()) return false

    val validBlocks = scheduleForDay.timeBlocks.filter {
        it.start.isNotBlank() && it.end.isNotBlank()
    }
    if (validBlocks.isEmpty()) return false

    val busyRanges = buildBusyRangesForDate(
        date = date,
        appointments = workerAppointments(worker)
    )

    val isToday = hasUsableCurrentTimeSnapshot(currentTime) &&
            date.year == currentTime.todayYear &&
            date.month == currentTime.todayMonth &&
            date.day == currentTime.todayDay

    validBlocks.forEach { block ->
        val blockStart = parseMinutes(block.start) ?: return@forEach
        val blockEnd = parseMinutes(block.end) ?: return@forEach
        if (blockEnd <= blockStart) return@forEach

        var cursor = blockStart

        while (cursor + 30 <= blockEnd) {
            val serviceStart = cursor
            val serviceEnd = serviceStart + 30

            val overlapsExistingAppointment = busyRanges.any { busy ->
                rangesOverlap(
                    startA = serviceStart,
                    endA = serviceEnd,
                    startB = busy.first,
                    endB = busy.second
                )
            }

            val isFutureSlot = if (isToday) {
                serviceStart > currentTime.currentMinutes
            } else {
                true
            }

            if (isFutureSlot && !overlapsExistingAppointment) {
                return true
            }

            cursor += 30
        }
    }

    return false
}

private fun buildBusyRangesForDate(
    date: SimpleDateValue,
    appointments: List<Appointment>
): List<Pair<Int, Int>> {
    return appointments
        .filter { appointment ->
            appointmentBlocksSchedule(appointment.status) &&
                    appointmentBelongsToDate(appointment, date)
        }
        .mapNotNull { appointment ->
            resolveBusyRangeForAppointment(appointment)
        }
        .sortedBy { it.first }
}

private fun resolveBusyRangeForAppointment(
    appointment: Appointment
): Pair<Int, Int>? {
    val blockedStart = appointment.blockedStartAt
        .takeIf { it.isNotBlank() }
        ?.let { parseMinutes(it) }

    val blockedEnd = appointment.blockedEndAt
        .takeIf { it.isNotBlank() }
        ?.let { parseMinutes(it) }

    if (blockedStart != null && blockedEnd != null && blockedEnd > blockedStart) {
        return blockedStart.coerceAtLeast(0) to blockedEnd.coerceAtMost(24 * 60)
    }

    val serviceStart = appointment.serviceStartAt
        .takeIf { it.isNotBlank() }
        ?.let { parseMinutes(it) }
        ?: return null

    val serviceEnd = when {
        appointment.serviceEndAt.isNotBlank() -> parseMinutes(appointment.serviceEndAt)
        appointment.serviceDurationMinutes > 0 -> serviceStart + appointment.serviceDurationMinutes
        else -> serviceStart + 30
    } ?: return null

    if (serviceEnd <= serviceStart) return null

    val busyStart = (serviceStart - appointment.bufferBeforeMinutes.coerceAtLeast(0))
        .coerceAtLeast(0)

    val busyEnd = (serviceEnd + appointment.bufferAfterMinutes.coerceAtLeast(0))
        .coerceAtMost(24 * 60)

    return busyStart to busyEnd
}

private fun appointmentBelongsToDate(
    appointment: Appointment,
    selectedDate: SimpleDateValue
): Boolean {
    if (appointment.dateKey.isNotBlank()) {
        return appointment.dateKey == formatDateKey(selectedDate)
    }

    appointment.serviceStartAt
        .takeIf { it.isNotBlank() }
        ?.let { startText ->
            extractSimpleDateFromText(startText)?.let { parsedDate ->
                return parsedDate == selectedDate
            }
        }

    appointment.blockedStartAt
        .takeIf { it.isNotBlank() }
        ?.let { startText ->
            extractSimpleDateFromText(startText)?.let { parsedDate ->
                return parsedDate == selectedDate
            }
        }

    return false
}

private fun extractSimpleDateFromText(value: String): SimpleDateValue? {
    val text = value.trim()
    val datePart = text.substringBefore("T").substringBefore(" ")
    return parseSimpleDate(datePart)
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

private fun parseMinutes(value: String): Int? {
    val normalized = value
        .trim()
        .substringAfterLast("T")
        .substringAfterLast(" ")

    val parts = normalized.split(":")
    if (parts.size < 2) return null

    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].take(2).toIntOrNull() ?: return null

    if (hour !in 0..23 || minute !in 0..59) return null

    return hour * 60 + minute
}