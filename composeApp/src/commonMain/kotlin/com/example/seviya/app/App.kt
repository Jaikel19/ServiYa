package com.example.seviya.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.seviya.app.navigation.AppNavGraph
import com.example.seviya.core.designsystem.components.ClientTab
import com.example.seviya.core.designsystem.components.GuestBottomBar
import com.example.seviya.core.designsystem.components.GuestTab
import com.example.seviya.core.designsystem.components.WorkerTab
import com.example.seviya.core.designsystem.theme.AppTheme
import com.example.seviya.core.navigation.Landing
import com.example.seviya.core.navigation.RoleAdmissionCatalog
import com.example.seviya.core.navigation.RoleCatalog
import com.example.seviya.feature.client.navigation.ClientScaffold
import com.example.seviya.feature.client.navigation.isClientScaffoldDestination
import com.example.seviya.feature.worker.navigation.WorkerScaffold
import com.example.seviya.feature.worker.navigation.isWorkerScaffoldDestination
import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.AppointmentLocation
import com.example.shared.domain.entity.AppointmentService
import com.example.shared.domain.entity.Service
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.notifications.NotificationsViewModel
import com.example.shared.presentation.requestAppointment.CurrentTimeSnapshot
import com.example.shared.presentation.requestAppointment.RequestAppointmentDraft
import com.example.shared.utils.DateTimeUtils
import org.koin.compose.viewmodel.koinViewModel

enum class SessionRole {
    GUEST,
    CLIENT,
    WORKER,
}

@Composable
fun App() {
    val navController = rememberNavController()

    var sessionRole by rememberSaveable { mutableStateOf(SessionRole.GUEST) }
    var currentWorkerId by rememberSaveable { mutableStateOf("worker_demo_001") }
    var currentClientId by rememberSaveable { mutableStateOf("client_demo_001") }
    var currentClientName by rememberSaveable { mutableStateOf("Cliente Demo") }

    var requestAppointmentDraft by remember { mutableStateOf<RequestAppointmentDraft?>(null) }

    var clientMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var workerMenuExpanded by rememberSaveable { mutableStateOf(false) }

    var currentClientTab by rememberSaveable { mutableStateOf(ClientTab.SERVICES) }
    var currentWorkerTab by rememberSaveable { mutableStateOf(WorkerTab.DASHBOARD) }

    val monthlyCalendarViewModel: MonthlyCalendarViewModel = koinViewModel()
    val notificationsViewModel: NotificationsViewModel = koinViewModel()

    val unreadAlertsCount by notificationsViewModel.unreadCount.collectAsState()

    LaunchedEffect(sessionRole, currentClientId, currentWorkerId) {
        when (sessionRole) {
            SessionRole.CLIENT -> notificationsViewModel.start(currentClientId)
            SessionRole.WORKER -> notificationsViewModel.start(currentWorkerId)
            SessionRole.GUEST -> Unit
        }
    }

    val clientUnreadAlertsCount =
        if (sessionRole == SessionRole.CLIENT) unreadAlertsCount else 0

    val workerUnreadAlertsCount =
        if (sessionRole == SessionRole.WORKER) unreadAlertsCount else 0

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showGuestBottomBar =
        sessionRole == SessionRole.GUEST &&
                (currentDestination.isRoute<Landing>() ||
                        currentDestination.isRoute<RoleCatalog>() ||
                        currentDestination.isRoute<RoleAdmissionCatalog>())

    val showClientBottomBar =
        sessionRole == SessionRole.CLIENT && currentDestination.isClientScaffoldDestination()

    val showWorkerBottomBar =
        sessionRole == SessionRole.WORKER && currentDestination.isWorkerScaffoldDestination()

    val guestCurrentTab =
        when {
            currentDestination.isRoute<Landing>() -> GuestTab.HOME
            currentDestination.isRoute<RoleAdmissionCatalog>() -> GuestTab.LOGIN
            currentDestination.isRoute<RoleCatalog>() -> GuestTab.REGISTER
            else -> GuestTab.HOME
        }

    AppTheme {
        when {
            showClientBottomBar -> {
                ClientScaffold(
                    navController = navController,
                    currentTab = currentClientTab,
                    menuExpanded = clientMenuExpanded,
                    unreadAlertsCount = clientUnreadAlertsCount,
                    onCurrentTabChange = { currentClientTab = it },
                    onClientMenuExpandedChange = { clientMenuExpanded = it },
                    onWorkerMenuExpandedChange = { workerMenuExpanded = it },
                    onLogout = {
                        clientMenuExpanded = false
                        workerMenuExpanded = false
                        sessionRole = SessionRole.GUEST
                        currentClientTab = ClientTab.SERVICES
                        requestAppointmentDraft = null
                        navController.navigateToLandingClearingStack()
                    },
                ) { innerPadding ->
                    AppNavigationContent(
                        innerPadding = innerPadding,
                        navController = navController,
                        currentWorkerId = currentWorkerId,
                        onCurrentWorkerIdChange = { currentWorkerId = it },
                        currentClientId = currentClientId,
                        onCurrentClientIdChange = { currentClientId = it },
                        currentClientName = currentClientName,
                        onCurrentClientNameChange = { currentClientName = it },
                        requestAppointmentDraft = requestAppointmentDraft,
                        onRequestAppointmentDraftChange = { requestAppointmentDraft = it },
                        onSessionRoleChange = { sessionRole = it },
                        onCurrentClientTabChange = { currentClientTab = it },
                        onCurrentWorkerTabChange = { currentWorkerTab = it },
                        onClientMenuExpandedChange = { clientMenuExpanded = it },
                        onWorkerMenuExpandedChange = { workerMenuExpanded = it },
                        monthlyCalendarViewModel = monthlyCalendarViewModel,
                    )
                }
            }

            showWorkerBottomBar -> {
                WorkerScaffold(
                    navController = navController,
                    currentWorkerId = currentWorkerId,
                    currentTab = currentWorkerTab,
                    menuExpanded = workerMenuExpanded,
                    unreadAlertsCount = workerUnreadAlertsCount,
                    onCurrentTabChange = { currentWorkerTab = it },
                    onClientMenuExpandedChange = { clientMenuExpanded = it },
                    onWorkerMenuExpandedChange = { workerMenuExpanded = it },
                    onLogout = {
                        workerMenuExpanded = false
                        clientMenuExpanded = false
                        sessionRole = SessionRole.GUEST
                        currentWorkerTab = WorkerTab.DASHBOARD
                        requestAppointmentDraft = null
                        navController.navigateToLandingClearingStack()
                    },
                ) { innerPadding ->
                    AppNavigationContent(
                        innerPadding = innerPadding,
                        navController = navController,
                        currentWorkerId = currentWorkerId,
                        onCurrentWorkerIdChange = { currentWorkerId = it },
                        currentClientId = currentClientId,
                        onCurrentClientIdChange = { currentClientId = it },
                        currentClientName = currentClientName,
                        onCurrentClientNameChange = { currentClientName = it },
                        requestAppointmentDraft = requestAppointmentDraft,
                        onRequestAppointmentDraftChange = { requestAppointmentDraft = it },
                        onSessionRoleChange = { sessionRole = it },
                        onCurrentClientTabChange = { currentClientTab = it },
                        onCurrentWorkerTabChange = { currentWorkerTab = it },
                        onClientMenuExpandedChange = { clientMenuExpanded = it },
                        onWorkerMenuExpandedChange = { workerMenuExpanded = it },
                        monthlyCalendarViewModel = monthlyCalendarViewModel,
                    )
                }
            }

            else -> {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (showGuestBottomBar) {
                            GuestBottomBar(
                                currentTab = guestCurrentTab,
                                onHome = {
                                    sessionRole = SessionRole.GUEST
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    requestAppointmentDraft = null
                                    navController.navigateSingleTop(Landing)
                                },
                                onLogin = {
                                    sessionRole = SessionRole.GUEST
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    navController.navigateSingleTop(RoleAdmissionCatalog)
                                },
                                onRegister = {
                                    sessionRole = SessionRole.GUEST
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    navController.navigateSingleTop(RoleCatalog)
                                },
                            )
                        }
                    },
                ) { innerPadding ->
                    AppNavigationContent(
                        innerPadding = innerPadding,
                        navController = navController,
                        currentWorkerId = currentWorkerId,
                        onCurrentWorkerIdChange = { currentWorkerId = it },
                        currentClientId = currentClientId,
                        onCurrentClientIdChange = { currentClientId = it },
                        currentClientName = currentClientName,
                        onCurrentClientNameChange = { currentClientName = it },
                        requestAppointmentDraft = requestAppointmentDraft,
                        onRequestAppointmentDraftChange = { requestAppointmentDraft = it },
                        onSessionRoleChange = { sessionRole = it },
                        onCurrentClientTabChange = { currentClientTab = it },
                        onCurrentWorkerTabChange = { currentWorkerTab = it },
                        onClientMenuExpandedChange = { clientMenuExpanded = it },
                        onWorkerMenuExpandedChange = { workerMenuExpanded = it },
                        monthlyCalendarViewModel = monthlyCalendarViewModel,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppNavigationContent(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    navController: NavHostController,
    currentWorkerId: String,
    onCurrentWorkerIdChange: (String) -> Unit,
    currentClientId: String,
    onCurrentClientIdChange: (String) -> Unit,
    currentClientName: String,
    onCurrentClientNameChange: (String) -> Unit,
    requestAppointmentDraft: RequestAppointmentDraft?,
    onRequestAppointmentDraftChange: (RequestAppointmentDraft?) -> Unit,
    onSessionRoleChange: (SessionRole) -> Unit,
    onCurrentClientTabChange: (ClientTab) -> Unit,
    onCurrentWorkerTabChange: (WorkerTab) -> Unit,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onWorkerMenuExpandedChange: (Boolean) -> Unit,
    monthlyCalendarViewModel: MonthlyCalendarViewModel,
) {
    AppNavGraph(
        navController = navController,
        modifier = Modifier.padding(innerPadding),
        currentWorkerId = currentWorkerId,
        onCurrentWorkerIdChange = onCurrentWorkerIdChange,
        currentClientId = currentClientId,
        onCurrentClientIdChange = onCurrentClientIdChange,
        currentClientName = currentClientName,
        onCurrentClientNameChange = onCurrentClientNameChange,
        requestAppointmentDraft = requestAppointmentDraft,
        onRequestAppointmentDraftChange = onRequestAppointmentDraftChange,
        onSessionRoleChange = onSessionRoleChange,
        onCurrentClientTabChange = onCurrentClientTabChange,
        onCurrentWorkerTabChange = onCurrentWorkerTabChange,
        onClientMenuExpandedChange = onClientMenuExpandedChange,
        onWorkerMenuExpandedChange = onWorkerMenuExpandedChange,
        monthlyCalendarViewModel = monthlyCalendarViewModel,
    )
}

@Composable
internal fun FeaturePlaceholder(title: String, subtitle: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineMedium)

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private inline fun <reified T : Any> NavDestination?.isRoute(): Boolean {
    return this?.hasRoute<T>() == true
}

internal fun NavHostController.navigateSingleTop(route: Any) {
    navigate(route) { launchSingleTop = true }
}

private fun NavHostController.navigateToLandingClearingStack() {
    navigate(Landing) {
        popUpTo(graph.findStartDestination().id) { inclusive = false }
        launchSingleTop = true
        restoreState = false
    }
}

internal fun buildCurrentTimeSnapshot(): CurrentTimeSnapshot {
    val isoNow = DateTimeUtils.nowIsoMinute()
    val parsed = parseIsoMinute(isoNow) ?: return CurrentTimeSnapshot()

    return CurrentTimeSnapshot(
        epochMillis = 0L,
        currentDayKey = appDayKeyFromDate(year = parsed.year, month = parsed.month, day = parsed.day),
        currentMinutes = (parsed.hour * 60) + parsed.minute,
        todayYear = parsed.year,
        todayMonth = parsed.month,
        todayDay = parsed.day,
    )
}

private data class ParsedIsoMinute(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
)

private fun parseIsoMinute(value: String): ParsedIsoMinute? {
    val trimmed = value.trim()
    if (trimmed.length < 16) return null

    val parts = trimmed.split("T")
    if (parts.size != 2) return null

    val dateParts = parts[0].split("-")
    val timeParts = parts[1].split(":")

    if (dateParts.size != 3 || timeParts.size < 2) return null

    val year = dateParts[0].toIntOrNull() ?: return null
    val month = dateParts[1].toIntOrNull() ?: return null
    val day = dateParts[2].toIntOrNull() ?: return null
    val hour = timeParts[0].toIntOrNull() ?: return null
    val minute = timeParts[1].toIntOrNull() ?: return null

    return ParsedIsoMinute(year = year, month = month, day = day, hour = hour, minute = minute)
}

private fun appDayKeyFromDate(year: Int, month: Int, day: Int): String {
    return when (dayOfWeekIndex(year, month, day)) {
        0 -> "sunday"
        1 -> "monday"
        2 -> "tuesday"
        3 -> "wednesday"
        4 -> "thursday"
        5 -> "friday"
        else -> "saturday"
    }
}

/** 0 = Sunday, 1 = Monday, ..., 6 = Saturday */
private fun dayOfWeekIndex(year: Int, month: Int, day: Int): Int {
    val monthOffsets = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    var y = year
    if (month < 3) y -= 1
    return (y + y / 4 - y / 100 + y / 400 + monthOffsets[month - 1] + day) % 7
}