package com.example.seviya.app


//esto se agrego para que sirva drante la migracion de booking
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.seviya.core.designsystem.components.ClientBottomBar
import com.example.seviya.core.designsystem.components.ClientTab
import com.example.seviya.core.designsystem.components.FullScreenMenu
import com.example.seviya.core.designsystem.components.GuestBottomBar
import com.example.seviya.core.designsystem.components.GuestTab
import com.example.seviya.core.designsystem.components.MenuOption
import com.example.seviya.core.designsystem.components.WorkerBottomBar
import com.example.seviya.core.designsystem.components.WorkerTab
import com.example.seviya.core.navigation.CategoriesCatalog
import com.example.seviya.core.navigation.ClientAgenda
import com.example.seviya.core.navigation.ClientAlerts
import com.example.seviya.core.navigation.ClientConfiguration
import com.example.seviya.core.navigation.ClientDashboard
import com.example.seviya.core.navigation.ClientFavorites
import com.example.seviya.core.navigation.ClientMap
import com.example.seviya.core.navigation.ClientMessages
import com.example.seviya.core.navigation.ClientPaymentUpload
import com.example.seviya.core.navigation.ClientProfile
import com.example.seviya.core.navigation.ClientRequests
import com.example.seviya.core.navigation.ClientSearch
import com.example.seviya.core.navigation.ClientSettings
import com.example.seviya.core.navigation.Landing
import com.example.seviya.core.navigation.ProfessionalProfile
import com.example.seviya.core.navigation.RequestAppointment
import com.example.seviya.core.navigation.RoleAdmissionCatalog
import com.example.seviya.core.navigation.RoleCatalog
import com.example.seviya.core.navigation.TravelTimeConfig
import com.example.seviya.core.navigation.WorkerAgenda
import com.example.seviya.core.navigation.WorkerAlerts
import com.example.seviya.core.navigation.WorkerAppointmentDetail
import com.example.seviya.core.navigation.WorkerConfiguration
import com.example.seviya.core.navigation.WorkerDashboard
import com.example.seviya.core.navigation.WorkerMessages
import com.example.seviya.core.navigation.ClientHome
import com.example.seviya.core.navigation.WorkerPortfolio
import com.example.seviya.core.navigation.WorkerProfile
import com.example.seviya.core.navigation.WorkerReports
import com.example.seviya.core.navigation.WorkerRequests
import com.example.seviya.core.navigation.WorkerCategories
import com.example.seviya.core.navigation.WorkerSchedule
import com.example.seviya.core.navigation.WorkerServices
import com.example.seviya.core.navigation.WorkerSettings
import com.example.seviya.core.navigation.WorkersList
import com.example.shared.presentation.requestAppointment.CurrentTimeSnapshot
import com.example.shared.presentation.requestAppointment.RequestAppointmentDraft
import com.example.seviya.core.designsystem.theme.AppTheme
import com.example.shared.domain.entity.Address
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.AppointmentLocation
import com.example.shared.domain.entity.AppointmentService
import com.example.shared.domain.entity.Booking
import com.example.shared.domain.entity.Service
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.seviya.core.navigation.WorkerDailyAppointments
import com.example.seviya.app.navigation.AppNavGraph
import com.example.seviya.core.navigation.ClientLocationCatalog
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import com.example.shared.utils.DateTimeUtils
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import org.koin.compose.viewmodel.koinViewModel

enum class SessionRole {
    GUEST,
    CLIENT,
    WORKER
}

@Composable
fun App() {
    val navController = rememberNavController()

    var sessionRole by rememberSaveable { mutableStateOf(SessionRole.GUEST) }
    var currentWorkerId by rememberSaveable { mutableStateOf("worker_demo_001") }
    var currentClientId by rememberSaveable { mutableStateOf("client_demo_001") }
    var currentClientName by rememberSaveable { mutableStateOf("Cliente Demo") }

    var requestAppointmentDraft by remember {
        mutableStateOf<RequestAppointmentDraft?>(null)
    }

    var clientMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var workerMenuExpanded by rememberSaveable { mutableStateOf(false) }

    var currentClientTab by rememberSaveable { mutableStateOf(ClientTab.SERVICES) }
    var currentWorkerTab by rememberSaveable { mutableStateOf(WorkerTab.DASHBOARD) }

    val monthlyCalendarViewModel: MonthlyCalendarViewModel = koinViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showGuestBottomBar =
        sessionRole == SessionRole.GUEST &&
                (
                        currentDestination.isRoute<Landing>() ||
                                currentDestination.isRoute<RoleCatalog>() ||
                                currentDestination.isRoute<RoleAdmissionCatalog>()
                        )

    val showClientBottomBar =
        sessionRole == SessionRole.CLIENT &&
                (
                        currentDestination.isRoute<ClientHome>() ||
                                currentDestination.isRoute<CategoriesCatalog>() ||
                                currentDestination.isRoute<WorkersList>() ||
                                currentDestination.isRoute<ProfessionalProfile>() ||
                                currentDestination.isRoute<ClientMap>() ||
                                currentDestination.isRoute<ClientSearch>() ||
                                currentDestination.isRoute<ClientAlerts>() ||
                                currentDestination.isRoute<ClientDashboard>() ||
                                currentDestination.isRoute<ClientAgenda>() ||
                                currentDestination.isRoute<ClientProfile>() ||
                                currentDestination.isRoute<ClientMessages>() ||
                                currentDestination.isRoute<ClientConfiguration>() ||
                                currentDestination.isRoute<ClientSettings>() ||
                                currentDestination.isRoute<ClientFavorites>() ||
                                currentDestination.isRoute<RequestAppointment>()||
                                currentDestination.isRoute<ClientRequests>() ||
                                currentDestination.isRoute<ClientPaymentUpload>() ||
                                currentDestination.isRoute<ClientLocationCatalog>()
                        )

    val showWorkerBottomBar =
        sessionRole == SessionRole.WORKER &&
                (
                        currentDestination.isRoute<WorkerDashboard>() ||
                                currentDestination.isRoute<WorkerAgenda>() ||
                                currentDestination.isRoute<WorkerRequests>() ||
                                currentDestination.isRoute<WorkerAlerts>() ||
                                currentDestination.isRoute<WorkerProfile>() ||
                                currentDestination.isRoute<WorkerMessages>() ||
                                currentDestination.isRoute<WorkerConfiguration>() ||
                                currentDestination.isRoute<WorkerSettings>() ||
                                currentDestination.isRoute<WorkerReports>() ||
                                currentDestination.isRoute<WorkerPortfolio>() ||
                                currentDestination.isRoute<WorkerServices>() ||
                                currentDestination.isRoute<WorkerSchedule>() ||

                                currentDestination.isRoute<WorkerCategories>() ||
                                currentDestination.isRoute<TravelTimeConfig>() ||
                                currentDestination.isRoute<WorkerAppointmentDetail>() ||
                                currentDestination.isRoute<WorkerDailyAppointments>()

                        )

    val guestCurrentTab =
        when {
            currentDestination.isRoute<Landing>() -> GuestTab.HOME
            currentDestination.isRoute<RoleAdmissionCatalog>() -> GuestTab.LOGIN
            currentDestination.isRoute<RoleCatalog>() -> GuestTab.REGISTER
            else -> GuestTab.HOME
        }

    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    when {
                        showGuestBottomBar -> {
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
                                }
                            )
                        }

                        showClientBottomBar -> {
                            ClientBottomBar(
                                currentTab = currentClientTab,
                                menuActive = clientMenuExpanded,
                                onGoServices = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    currentClientTab = ClientTab.SERVICES
                                    navController.navigateSingleTop(ClientHome)
                                },
                                onGoMap = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    currentClientTab = ClientTab.MAP
                                    navController.navigateSingleTop(ClientMap)
                                },
                                onGoSearch = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    currentClientTab = ClientTab.SEARCH
                                    navController.navigateSingleTop(ClientSearch)
                                },
                                onGoAlerts = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    currentClientTab = ClientTab.ALERTS
                                    navController.navigateSingleTop(ClientAlerts)
                                },
                                onGoMenu = {
                                    workerMenuExpanded = false
                                    clientMenuExpanded = !clientMenuExpanded
                                }
                            )
                        }

                        showWorkerBottomBar -> {
                            WorkerBottomBar(
                                currentTab = currentWorkerTab,
                                menuActive = workerMenuExpanded,
                                onGoDashboard = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    currentWorkerTab = WorkerTab.DASHBOARD
                                    navController.navigateSingleTop(WorkerDashboard)
                                },
                                onGoAgenda = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    currentWorkerTab = WorkerTab.AGENDA
                                    navController.navigateSingleTop(WorkerAgenda)
                                },
                                onGoRequests = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    currentWorkerTab = WorkerTab.REQUESTS
                                    navController.navigateSingleTop(WorkerRequests)
                                },
                                onGoAlerts = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = false
                                    currentWorkerTab = WorkerTab.ALERTS
                                    navController.navigateSingleTop(WorkerAlerts)
                                },
                                onGoMenu = {
                                    clientMenuExpanded = false
                                    workerMenuExpanded = !workerMenuExpanded
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                AppNavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
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
                    monthlyCalendarViewModel = monthlyCalendarViewModel
                )
            }

            AnimatedVisibility(
                visible = clientMenuExpanded,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(220)) + scaleIn(
                    initialScale = 0.98f,
                    animationSpec = tween(300)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(260)
                ) + fadeOut(animationSpec = tween(180)) + scaleOut(
                    targetScale = 0.98f,
                    animationSpec = tween(260)
                )
            ) {
                FullScreenMenu(
                    title = "Menú cliente",
                    options = clientMenuOptions(
                        navController = navController,
                        closeMenu = { clientMenuExpanded = false },
                        onLogout = {
                            clientMenuExpanded = false
                            workerMenuExpanded = false
                            sessionRole = SessionRole.GUEST
                            currentClientTab = ClientTab.SERVICES
                            requestAppointmentDraft = null
                            navController.navigateToLandingClearingStack()
                        }
                    ),
                    onDismiss = { clientMenuExpanded = false }
                )
            }

            AnimatedVisibility(
                visible = workerMenuExpanded,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(220)) + scaleIn(
                    initialScale = 0.98f,
                    animationSpec = tween(300)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(260)
                ) + fadeOut(animationSpec = tween(180)) + scaleOut(
                    targetScale = 0.98f,
                    animationSpec = tween(260)
                )
            ) {
                FullScreenMenu(
                    title = "Menú trabajador",
                    options = workerMenuOptions(
                        navController = navController,
                        currentWorkerId = currentWorkerId,
                        closeMenu = { workerMenuExpanded = false },
                        onLogout = {
                            workerMenuExpanded = false
                            clientMenuExpanded = false
                            sessionRole = SessionRole.GUEST
                            currentWorkerTab = WorkerTab.DASHBOARD
                            requestAppointmentDraft = null
                            navController.navigateToLandingClearingStack()
                        }
                    ),
                    onDismiss = { workerMenuExpanded = false }
                )
            }
        }
    }
}

private fun clientMenuOptions(
    navController: NavHostController,
    closeMenu: () -> Unit,
    onLogout: () -> Unit
): List<MenuOption> {
    return listOf(
        MenuOption(
            title = "Agenda",
            subtitle = "Citas, historial y seguimiento",
            icon = TablerIcons.CalendarEvent,
            iconColor = Color(0xFF5FA8D3),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientAgenda)
            }
        ),
        MenuOption(
            title = "Solicitudes",
            subtitle = "Seguimiento de solicitudes y comprobantes",
            icon = TablerIcons.Dashboard,
            iconColor = Color(0xFF4F8CFF),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientRequests)
            }
        ),
        MenuOption(
            title = "Favoritos",
            subtitle = "Trabajadores guardados por el cliente",
            icon = TablerIcons.Briefcase,
            iconColor = Color(0xFFE77E9B),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientFavorites)
            }
        ),
        MenuOption(
            title = "Mis Ubicaciones",
            subtitle = "Gestiona tus direcciones frecuentes",
            icon = TablerIcons.MapPin,
            iconColor = Color(0xFF4A9EC7),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientLocationCatalog)
            }
        ),
        MenuOption(
            title = "Perfil",
            subtitle = "Datos personales e información de la cuenta",
            icon = TablerIcons.User,
            iconColor = Color(0xFF8E7CC3),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientProfile)
            }
        ),
        MenuOption(
            title = "Mensajes",
            subtitle = "Chats y conversaciones con trabajadores",
            icon = TablerIcons.Message,
            iconColor = Color(0xFF67B99A),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientMessages)
            }
        ),
        MenuOption(
            title = "Dashboard",
            subtitle = "Resumen general y actividad reciente",
            icon = TablerIcons.Dashboard,
            iconColor = Color(0xFFE29C7A),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientDashboard)
            }
        ),
        MenuOption(
            title = "Configuración",
            subtitle = "Opciones principales de la aplicación",
            icon = TablerIcons.Settings,
            iconColor = Color(0xFF9BB85D),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientConfiguration)
            }
        ),
        MenuOption(
            title = "Ajustes",
            subtitle = "Preferencias y personalización",
            icon = TablerIcons.Adjustments,
            iconColor = Color(0xFFD7B85A),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(ClientSettings)
            }
        ),
        MenuOption(
            title = "Cerrar sesión",
            subtitle = "Salir de la cuenta y volver al inicio",
            icon = TablerIcons.Logout,
            iconColor = Color(0xFFEF4444),
            onClick = onLogout
        )
    )
}

private fun workerMenuOptions(
    navController: NavHostController,
    currentWorkerId: String,
    closeMenu: () -> Unit,
    onLogout: () -> Unit
): List<MenuOption> {
    return listOf(
        MenuOption(
            title = "Perfil",
            subtitle = "Datos personales e información pública",
            icon = TablerIcons.User,
            iconColor = Color(0xFF8E7CC3),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerProfile)
            }
        ),
        MenuOption(
            title = "Agenda Diaria",
            subtitle = "Vista diaria en lista o mapa de las citas",
            icon = TablerIcons.CalendarEvent,
            iconColor = Color(0xFF4F8CFF),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(
                    WorkerDailyAppointments(workerId = currentWorkerId)
                )
            }
        ),
        MenuOption(
            title = "Servicios",
            subtitle = "Registrar y modificar servicios ofrecidos",
            icon = TablerIcons.Briefcase,
            iconColor = Color(0xFF4CB5AE),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerServices)
            }
        ),
        MenuOption(
            title = "Mis Categorías",
            subtitle = "Categorías de servicios que ofrezco",
            icon = TablerIcons.Adjustments,
            iconColor = Color(0xFF3B82F6),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerCategories)
            }
        ),
        MenuOption(
            title = "Tiempo de Traslado",
            subtitle = "Tiempo de margen entre servicios",
            icon = TablerIcons.Clock,
            iconColor = Color(0xFFE2B100),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(TravelTimeConfig)
            }
        ),
        MenuOption(
            title = "Mensajes",
            subtitle = "Chats y conversaciones con clientes",
            icon = TablerIcons.Message,
            iconColor = Color(0xFF67B99A),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerMessages)
            }
        ),
        MenuOption(
            title = "Configuración",
            subtitle = "Opciones principales de la aplicación",
            icon = TablerIcons.Settings,
            iconColor = Color(0xFF9BB85D),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerConfiguration)
            }
        ),
        MenuOption(
            title = "Ajustes",
            subtitle = "Preferencias y personalización",
            icon = TablerIcons.Adjustments,
            iconColor = Color(0xFFD7B85A),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerSettings)
            }
        ),
        MenuOption(
            title = "Reportes",
            subtitle = "Indicadores, ingresos y desempeño",
            icon = TablerIcons.ChartBar,
            iconColor = Color(0xFF5F8EFA),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerReports)
            }
        ),
        MenuOption(
            title = "Portafolio",
            subtitle = "Trabajos realizados y evidencia visual",
            icon = TablerIcons.Photo,
            iconColor = Color(0xFFC96AE6),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerPortfolio)
            }
        ),
        MenuOption(
            title = "Horario",
            subtitle = "Días laborales, disponibilidad y zonas",
            icon = TablerIcons.Clock,
            iconColor = Color(0xFFE2B100),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerSchedule)
            }
        ),
        MenuOption(
            title = "Cerrar sesión",
            subtitle = "Salir de la cuenta y volver al inicio",
            icon = TablerIcons.Logout,
            iconColor = Color(0xFFEF4444),
            onClick = onLogout
        )
    )
}

@Composable
private fun ClientDashboardPlaceholder(
    onGoToProfessionalProfile: () -> Unit,
    onBackToLanding: () -> Unit,
    onGoToCategories: () -> Unit,
    onGoToServices: () -> Unit,
    onGoToClientAppointmentDetail: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dashboard Cliente",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Pantalla temporal para pruebas del flujo de ingreso.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
            )

            Button(
                onClick = onGoToCategories,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir a categorías")
            }

            Button(
                onClick = onGoToProfessionalProfile,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir al perfil profesional")
            }

            Button(
                onClick = onGoToServices,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir a servicios")
            }

            Button(
                onClick = onGoToClientAppointmentDetail,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir a detalle de cita cliente")
            }

            Button(onClick = onBackToLanding) {
                Text("Volver al landing")
            }
        }
    }
}

@Composable
private fun WorkerDashboardPlaceholder(
    onBackToLanding: () -> Unit,
    onGoToMonthlyCalendar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dashboard Trabajador",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Pantalla temporal para pruebas del flujo de ingreso.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
            )

            Button(
                onClick = onGoToMonthlyCalendar,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir a la agenda")
            }

            Button(onClick = onBackToLanding) {
                Text("Volver al landing")
            }
        }
    }
}

@Composable
internal fun FeaturePlaceholder(
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private inline fun <reified T : Any> NavDestination?.isRoute(): Boolean {
    return this?.hasRoute<T>() == true
}

internal fun NavHostController.navigateSingleTop(route: Any) {
    navigate(route) {
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToLandingClearingStack() {
    navigate(Landing) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = false
        }
        launchSingleTop = true
        restoreState = false
    }
}

//esto siguiente se tiene que borrar apenas todas las clases dejen de recibir booking
internal fun Booking.toAppointment(): Appointment {
    return Appointment(
        id = id,
        clientId = clientId,
        clientName = clientName,
        workerId = workerId,
        workerName = workerName,
        status = status,
        serviceStartAt = date,
        services = services.map { it.toAppointmentService() },
        totalCost = totalCost.toInt(),
        location = location.toAppointmentLocation(),
        cancellationBy = cancellationBy.ifBlank { null },
        cancellationReason = cancellationReason.ifBlank { null },
        clientToWorkerReviewDone = ratingToWorkerDone,
        workerToClientReviewDone = ratingToClientDone
    )
}

private fun Appointment.toBooking(): Booking {
    return Booking(
        id = id,
        clientId = clientId,
        clientName = clientName,
        workerId = workerId,
        workerName = workerName,
        date = serviceStartAt,
        status = status,
        totalCost = totalCost.toDouble(),
        services = services.map { it.toService() },
        location = location.toAddress(),
        cancellationReason = cancellationReason ?: "",
        cancellationBy = cancellationBy ?: "",
        ratingToClientDone = workerToClientReviewDone,
        ratingToWorkerDone = clientToWorkerReviewDone
    )
}

private fun Service.toAppointmentService(): AppointmentService {
    return AppointmentService(
        id = id,
        name = name,
        description = description,
        cost = cost.toInt(),
        durationMinutes = extractMinutesFromDuration(duration),
        subtotal = cost.toInt()
    )
}

private fun AppointmentService.toService(): Service {
    return Service(
        id = id,
        name = name,
        description = description,
        cost = cost.toDouble(),
        duration = "${durationMinutes} min"
    )
}

private fun Address.toAppointmentLocation(): AppointmentLocation {
    return AppointmentLocation(
        id = id,
        alias = alias,
        province = province,
        district = district,
        canton = canton,
        latitude = latitude,
        longitude = longitude,
        reference = reference
    )
}

private fun AppointmentLocation.toAddress(): Address {
    return Address(
        id = id,
        alias = alias,
        province = province,
        district = district,
        canton = canton,
        latitude = latitude,
        longitude = longitude,
        reference = reference
    )
}

private fun extractMinutesFromDuration(duration: String): Int {
    return duration
        .filter { it.isDigit() }
        .toIntOrNull() ?: 0
}

private val APP_TIME_ZONE = TimeZone.of("America/Costa_Rica")


internal fun buildCurrentTimeSnapshot(): CurrentTimeSnapshot {
    val isoNow = DateTimeUtils.nowIsoMinute()
    val parsed = parseIsoMinute(isoNow) ?: return CurrentTimeSnapshot()

    return CurrentTimeSnapshot(
        epochMillis = 0L,
        currentDayKey = appDayKeyFromDate(
            year = parsed.year,
            month = parsed.month,
            day = parsed.day
        ),
        currentMinutes = (parsed.hour * 60) + parsed.minute,
        todayYear = parsed.year,
        todayMonth = parsed.month,
        todayDay = parsed.day
    )
}

private data class ParsedIsoMinute(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
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

    return ParsedIsoMinute(
        year = year,
        month = month,
        day = day,
        hour = hour,
        minute = minute
    )
}

private fun appDayKeyFromDate(
    year: Int,
    month: Int,
    day: Int
): String {
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

/**
 * 0 = Sunday, 1 = Monday, ..., 6 = Saturday
 */
private fun dayOfWeekIndex(year: Int, month: Int, day: Int): Int {
    val monthOffsets = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    var y = year
    if (month < 3) y -= 1
    return (y + y / 4 - y / 100 + y / 400 + monthOffsets[month - 1] + day) % 7
}

private fun appDayKeyFromWeekday(dayOfWeek: DayOfWeek): String {
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