package com.example.seviya

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.seviya.UI.CategoriesCatalogRoute
import com.example.seviya.UI.LandingScreen
import com.example.seviya.UI.MonthlyCalendarScreen
import com.example.seviya.UI.ProfessionalProfileRoute
import com.example.seviya.UI.RoleAdmissionCatalogScreen
import com.example.seviya.UI.RoleCatalogScreen
import com.example.seviya.UI.TravelTimeConfigScreen
import com.example.seviya.UI.WorkerAppointmentDetailScreen
import com.example.seviya.UI.WorkerDashboardRoute
import com.example.seviya.UI.WorkersListRoute
import com.example.seviya.components.ClientBottomBar
import com.example.seviya.components.ClientTab
import com.example.seviya.components.FullScreenMenu
import com.example.seviya.components.GuestBottomBar
import com.example.seviya.components.GuestTab
import com.example.seviya.components.MenuOption
import com.example.seviya.components.WorkerBottomBar
import com.example.seviya.components.WorkerTab
import com.example.seviya.navigation.CategoriesCatalog
import com.example.seviya.navigation.ClientAgenda
import com.example.seviya.navigation.ClientAlerts
import com.example.seviya.navigation.ClientConfiguration
import com.example.seviya.navigation.ClientDashboard
import com.example.seviya.navigation.ClientMap
import com.example.seviya.navigation.ClientMessages
import com.example.seviya.navigation.ClientProfile
import com.example.seviya.navigation.ClientSearch
import com.example.seviya.navigation.ClientSettings
import com.example.seviya.navigation.Landing
import com.example.seviya.navigation.ProfessionalProfile
import com.example.seviya.navigation.RoleAdmissionCatalog
import com.example.seviya.navigation.RoleCatalog
import com.example.seviya.navigation.Services
import com.example.seviya.navigation.TravelTimeConfig
import com.example.seviya.navigation.WorkerAgenda
import com.example.seviya.navigation.WorkerAlerts
import com.example.seviya.navigation.WorkerAppointmentDetail
import com.example.seviya.navigation.WorkerConfiguration
import com.example.seviya.navigation.WorkerDashboard
import com.example.seviya.navigation.WorkerMessages
import com.example.seviya.navigation.WorkerPortfolio
import com.example.seviya.navigation.WorkerProfile
import com.example.seviya.navigation.WorkerReports
import com.example.seviya.navigation.WorkerRequests
import com.example.seviya.navigation.WorkerSchedule
import com.example.seviya.navigation.WorkerServices
import com.example.seviya.navigation.WorkerSettings
import com.example.seviya.navigation.WorkersList
import com.example.seviya.theme.AppTheme
import com.example.seviya.ui.ServicesScreen
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.categories.CategoriesViewModel
import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel
import com.example.shared.presentation.services.ServicesViewModel
import com.example.shared.presentation.workerDashboard.WorkerDashboardViewModel
import com.example.shared.presentation.workersList.WorkersListViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Adjustments
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.ChartBar
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Dashboard
import compose.icons.tablericons.Logout
import compose.icons.tablericons.Message
import compose.icons.tablericons.Photo
import compose.icons.tablericons.Settings
import compose.icons.tablericons.User
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

    var clientMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var workerMenuExpanded by rememberSaveable { mutableStateOf(false) }

    var currentClientTab by rememberSaveable { mutableStateOf(ClientTab.SERVICES) }
    var currentWorkerTab by rememberSaveable { mutableStateOf(WorkerTab.DASHBOARD) }

    val monthlyCalendarViewModel: MonthlyCalendarViewModel = koinViewModel()
    val calendarState by monthlyCalendarViewModel.uiState.collectAsState()

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
                                currentDestination.isRoute<ClientSettings>()
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
                                currentDestination.isRoute<WorkerAppointmentDetail>()
                        )

    val guestCurrentTab =
        when {
            currentDestination.isRoute<Landing>() -> GuestTab.HOME
            currentDestination.isRoute<RoleAdmissionCatalog>() -> GuestTab.LOGIN
            currentDestination.isRoute<RoleCatalog>() -> GuestTab.REGISTER
            else -> GuestTab.HOME
        }

    AppTheme {
        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
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
                                    navController.navigateSingleTop(CategoriesCatalog)
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
                NavHost(
                    navController = navController,
                    startDestination = Landing,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding),
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(320)
                        ) + fadeIn(animationSpec = tween(260))
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(280)
                        ) + fadeOut(animationSpec = tween(220))
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(320)
                        ) + fadeIn(animationSpec = tween(260))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(280)
                        ) + fadeOut(animationSpec = tween(220))
                    }
                ) {
                    composable<Landing> {
                        LandingScreen(
                            onGoToServices = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(Services)
                            },
                            onLogin = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(RoleAdmissionCatalog)
                            },
                            onRegister = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(RoleCatalog)
                            }
                        )
                    }

                    composable<Services> {
                        val viewModel: ServicesViewModel = koinViewModel()
                        ServicesScreen(viewModel)
                    }

                    composable<TravelTimeConfig> {
                        TravelTimeConfigScreen(
                            initialMinutes = 30,
                            onBack = { navController.popBackStack() },
                            onSave = { navController.popBackStack() },
                            onGoHome = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(Landing)
                            },
                            onGoServices = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(Services)
                            },
                            onGoRegister = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(RoleCatalog)
                            }
                        )
                    }

                    composable<RoleCatalog> {
                        RoleCatalogScreen(
                            onGoHome = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(Landing)
                            },
                            onGoLogin = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(RoleAdmissionCatalog)
                            },
                            onGoRegister = { },
                            onPickClient = {
                                sessionRole = SessionRole.CLIENT
                                currentClientTab = ClientTab.SERVICES
                                navController.navigateSingleTop(CategoriesCatalog)
                            },
                            onPickWorker = {
                                sessionRole = SessionRole.WORKER
                                currentWorkerTab = WorkerTab.DASHBOARD
                                currentWorkerId = "worker_demo_001"
                                navController.navigateSingleTop(WorkerDashboard)
                            }
                        )
                    }

                    composable<RoleAdmissionCatalog> {
                        RoleAdmissionCatalogScreen(
                            onGoHome = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(Landing)
                            },
                            onGoLogin = { },
                            onGoRegister = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(RoleCatalog)
                            },
                            onPickClient = {
                                sessionRole = SessionRole.CLIENT
                                currentClientTab = ClientTab.SERVICES
                                navController.navigateSingleTop(ClientDashboard)
                            },
                            onPickWorker = {
                                sessionRole = SessionRole.WORKER
                                currentWorkerTab = WorkerTab.DASHBOARD
                                currentWorkerId = "worker_demo_001"
                                navController.navigateSingleTop(WorkerDashboard)
                            }
                        )
                    }

                    composable<CategoriesCatalog> {
                        val viewModel: CategoriesViewModel = koinViewModel()

                        var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
                        var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }

                        CategoriesCatalogRoute(
                            viewModel = viewModel,
                            selectedCategoryId = selectedCategoryId,
                            onGoServices = {
                                currentClientTab = ClientTab.SERVICES
                                navController.navigateSingleTop(CategoriesCatalog)
                            },
                            onGoMap = {
                                currentClientTab = ClientTab.MAP
                                navController.navigateSingleTop(ClientMap)
                            },
                            onGoSearch = {
                                currentClientTab = ClientTab.SEARCH
                                navController.navigateSingleTop(ClientSearch)
                            },
                            onGoAlerts = {
                                currentClientTab = ClientTab.ALERTS
                                navController.navigateSingleTop(ClientAlerts)
                            },
                            onGoAgenda = { navController.navigateSingleTop(ClientAgenda) },
                            onGoProfile = { navController.navigateSingleTop(ClientProfile) },
                            onGoConfiguration = { navController.navigateSingleTop(ClientConfiguration) },
                            onGoMessages = { navController.navigateSingleTop(ClientMessages) },
                            onGoDashboard = { navController.navigateSingleTop(ClientDashboard) },
                            onGoSettings = { navController.navigateSingleTop(ClientSettings) },
                            onCategoryClick = { category ->
                                selectedCategoryId = category.id
                                selectedCategoryName = category.name
                            },
                            onContinueWithSelectedCategory = {
                                if (!selectedCategoryId.isNullOrBlank()) {
                                    navController.navigate(
                                        WorkersList(
                                            categoryId = selectedCategoryId,
                                            categoryName = selectedCategoryName
                                        )
                                    )
                                }
                            }
                        )
                    }

                    composable<WorkersList> { backStackEntry ->
                        val route = backStackEntry.toRoute<WorkersList>()
                        val viewModel: WorkersListViewModel = koinViewModel()

                        WorkersListRoute(
                            viewModel = viewModel,
                            selectedCategoryId = route.categoryId,
                            selectedCategoryName = route.categoryName,
                            onWorkerClick = { workerId ->
                                navController.navigate(
                                    ProfessionalProfile(workerId = workerId)
                                )
                            },
                            onThemeClick = { },
                            onBottomServices = {
                                currentClientTab = ClientTab.SERVICES
                                navController.navigateSingleTop(CategoriesCatalog)
                            },
                            onBottomMap = {
                                currentClientTab = ClientTab.MAP
                                navController.navigateSingleTop(ClientMap)
                            },
                            onBottomSearch = {
                                currentClientTab = ClientTab.SEARCH
                                navController.navigateSingleTop(ClientSearch)
                            },
                            onBottomNotifications = {
                                currentClientTab = ClientTab.ALERTS
                                navController.navigateSingleTop(ClientAlerts)
                            },
                            onBottomMenu = {
                                clientMenuExpanded = true
                                workerMenuExpanded = false
                            }
                        )
                    }

                    composable<ProfessionalProfile> { backStackEntry ->
                        val route = backStackEntry.toRoute<ProfessionalProfile>()
                        val viewModel: ProfessionalProfileViewModel = koinViewModel()

                        ProfessionalProfileRoute(
                            workerId = route.workerId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onBottomServices = {
                                currentClientTab = ClientTab.SERVICES
                                navController.navigateSingleTop(CategoriesCatalog)
                            },
                            onBottomMap = {
                                currentClientTab = ClientTab.MAP
                                navController.navigateSingleTop(ClientMap)
                            },
                            onBottomSearch = {
                                currentClientTab = ClientTab.SEARCH
                                navController.navigateSingleTop(ClientSearch)
                            },
                            onBottomNotifications = {
                                currentClientTab = ClientTab.ALERTS
                                navController.navigateSingleTop(ClientAlerts)
                            },
                            onBottomMenu = {
                                clientMenuExpanded = true
                                workerMenuExpanded = false
                            }
                        )
                    }

                    composable<ClientDashboard> {
                        ClientDashboardPlaceholder(
                            onBackToLanding = {
                                sessionRole = SessionRole.GUEST
                                clientMenuExpanded = false
                                workerMenuExpanded = false
                                navController.navigateSingleTop(Landing)
                            },
                            onGoToProfessionalProfile = {
                                navController.navigate(
                                    ProfessionalProfile(workerId = "worker_demo_001")
                                )
                            },
                            onGoToCategories = {
                                currentClientTab = ClientTab.SERVICES
                                navController.navigateSingleTop(CategoriesCatalog)
                            },
                            onGoToServices = {
                                sessionRole = SessionRole.GUEST
                                navController.navigateSingleTop(Services)
                            }
                        )
                    }

                    composable<ClientMap> {
                        FeaturePlaceholder(
                            title = "Mapa del cliente",
                            subtitle = "Aquí irá el mapa interactivo con trabajadores por categoría y zona."
                        )
                    }

                    composable<ClientSearch> {
                        FeaturePlaceholder(
                            title = "Búsqueda",
                            subtitle = "Aquí irá la búsqueda de trabajadores, categorías y servicios."
                        )
                    }

                    composable<ClientAlerts> {
                        FeaturePlaceholder(
                            title = "Alertas del cliente",
                            subtitle = "Aquí irán las notificaciones y recordatorios del cliente."
                        )
                    }

                    composable<ClientAgenda> {
                        FeaturePlaceholder(
                            title = "Agenda del cliente",
                            subtitle = "Aquí irán las citas, historial y seguimiento del cliente."
                        )
                    }

                    composable<ClientProfile> {
                        FeaturePlaceholder(
                            title = "Perfil del cliente",
                            subtitle = "Aquí irá la información del perfil del cliente."
                        )
                    }

                    composable<ClientMessages> {
                        FeaturePlaceholder(
                            title = "Mensajes",
                            subtitle = "Aquí irán los chats del cliente con los trabajadores."
                        )
                    }

                    composable<ClientConfiguration> {
                        FeaturePlaceholder(
                            title = "Configuración",
                            subtitle = "Aquí irán las opciones principales de configuración del cliente."
                        )
                    }

                    composable<ClientSettings> {
                        FeaturePlaceholder(
                            title = "Ajustes",
                            subtitle = "Aquí irán las preferencias y personalización del cliente."
                        )
                    }

                    composable<WorkerDashboard> {
                        val viewModel: WorkerDashboardViewModel = koinViewModel()

                        WorkerDashboardRoute(
                            workerId = currentWorkerId,
                            viewModel = viewModel,
                            onOpenMessages = {
                                navController.navigateSingleTop(WorkerMessages)
                            },
                            onOpenReports = {
                                navController.navigateSingleTop(WorkerReports)
                            },
                            onOpenSchedule = {
                                navController.navigateSingleTop(WorkerSchedule)
                            },
                            onOpenPortfolio = {
                                navController.navigateSingleTop(WorkerPortfolio)
                            },
                            onOpenSettings = {
                                navController.navigateSingleTop(WorkerSettings)
                            },
                            onOpenAppointmentDetail = { booking ->
                                monthlyCalendarViewModel.selectBooking(booking)
                                navController.navigateSingleTop(WorkerAppointmentDetail)
                            },
                            onStartAppointment = { booking ->
                                monthlyCalendarViewModel.startAppointment(booking.id)
                            },
                            onCompleteAppointment = { booking ->
                                monthlyCalendarViewModel.completeAppointment(booking.id)
                            },
                            onOpenReview = { booking ->
                                monthlyCalendarViewModel.selectBooking(booking)
                                navController.navigateSingleTop(WorkerAppointmentDetail)
                            }
                        )
                    }

                    composable<WorkerAgenda> {
                        MonthlyCalendarScreen(
                            viewModel = monthlyCalendarViewModel,
                            onBack = { navController.popBackStack() },
                            onOpenBookingDetail = { booking ->
                                monthlyCalendarViewModel.selectBooking(booking)
                                navController.navigateSingleTop(WorkerAppointmentDetail)
                            }
                        )
                    }

                    composable<WorkerAppointmentDetail> {
                        calendarState.selectedBooking?.let { booking ->
                            WorkerAppointmentDetailScreen(
                                booking = booking,
                                onBack = {
                                    monthlyCalendarViewModel.clearSelectedBooking()
                                    navController.popBackStack()
                                },
                                onOpenGoogleMaps = {},
                                onOpenWaze = {},
                                onVerifyPayment = {
                                    monthlyCalendarViewModel.confirmPayment(booking.id)
                                },
                                onStartAppointment = {
                                    monthlyCalendarViewModel.startAppointment(booking.id)
                                },
                                onFinishAppointment = {
                                    monthlyCalendarViewModel.completeAppointment(booking.id)
                                },
                                onRateClient = { },
                                onCancelAppointment = {
                                    monthlyCalendarViewModel.cancelAppointmentByWorker(booking.id)
                                },
                                onGoServices = {
                                    currentWorkerTab = WorkerTab.DASHBOARD
                                    navController.navigateSingleTop(WorkerDashboard)
                                },
                                onGoMap = { },
                                onGoSearch = {
                                    currentWorkerTab = WorkerTab.REQUESTS
                                    navController.navigateSingleTop(WorkerRequests)
                                },
                                onGoAlerts = {
                                    currentWorkerTab = WorkerTab.ALERTS
                                    navController.navigateSingleTop(WorkerAlerts)
                                },
                                onGoMenu = {
                                    workerMenuExpanded = true
                                    clientMenuExpanded = false
                                }
                            )
                        } ?: FeaturePlaceholder(
                            title = "Detalle de cita",
                            subtitle = "No hay una cita seleccionada en este momento."
                        )
                    }

                    composable<WorkerRequests> {
                        FeaturePlaceholder(
                            title = "Solicitudes",
                            subtitle = "Aquí irán las solicitudes de citas y validación de pagos del trabajador."
                        )
                    }

                    composable<WorkerAlerts> {
                        FeaturePlaceholder(
                            title = "Alertas del trabajador",
                            subtitle = "Aquí irán las alertas y recordatorios del trabajador."
                        )
                    }

                    composable<WorkerProfile> {
                        FeaturePlaceholder(
                            title = "Perfil del trabajador",
                            subtitle = "Aquí irá la información pública y privada del trabajador."
                        )
                    }

                    composable<WorkerMessages> {
                        FeaturePlaceholder(
                            title = "Mensajes del trabajador",
                            subtitle = "Aquí irán los chats del trabajador con los clientes."
                        )
                    }

                    composable<WorkerConfiguration> {
                        FeaturePlaceholder(
                            title = "Configuración del trabajador",
                            subtitle = "Aquí irán las opciones principales de configuración del trabajador."
                        )
                    }

                    composable<WorkerSettings> {
                        FeaturePlaceholder(
                            title = "Ajustes del trabajador",
                            subtitle = "Aquí irán las preferencias y personalización del trabajador."
                        )
                    }

                    composable<WorkerReports> {
                        FeaturePlaceholder(
                            title = "Reportes",
                            subtitle = "Aquí irán métricas, ingresos y reportes del trabajador."
                        )
                    }

                    composable<WorkerPortfolio> {
                        FeaturePlaceholder(
                            title = "Portafolio",
                            subtitle = "Aquí irá el portafolio de trabajos del trabajador."
                        )
                    }

                    composable<WorkerServices> {
                        FeaturePlaceholder(
                            title = "Servicios del trabajador",
                            subtitle = "Aquí irá el registro y edición de servicios del trabajador."
                        )
                    }

                    composable<WorkerSchedule> {
                        FeaturePlaceholder(
                            title = "Horario del trabajador",
                            subtitle = "Aquí irá la configuración de días laborales, zonas y disponibilidad."
                        )
                    }
                }
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
                        closeMenu = { workerMenuExpanded = false },
                        onLogout = {
                            workerMenuExpanded = false
                            clientMenuExpanded = false
                            sessionRole = SessionRole.GUEST
                            currentWorkerTab = WorkerTab.DASHBOARD
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
    onGoToServices: () -> Unit
) {
    Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier
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
                modifier = androidx.compose.ui.Modifier.padding(top = 12.dp, bottom = 24.dp)
            )

            Button(
                onClick = onGoToCategories,
                modifier = androidx.compose.ui.Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir a categorías")
            }

            Button(
                onClick = onGoToProfessionalProfile,
                modifier = androidx.compose.ui.Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir al perfil profesional")
            }

            Button(
                onClick = onGoToServices,
                modifier = androidx.compose.ui.Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir a servicios")
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
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier
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
                modifier = androidx.compose.ui.Modifier.padding(top = 12.dp, bottom = 24.dp)
            )

            Button(
                onClick = onGoToMonthlyCalendar,
                modifier = androidx.compose.ui.Modifier.padding(bottom = 12.dp)
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
private fun FeaturePlaceholder(
    title: String,
    subtitle: String
) {
    Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier
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
                modifier = androidx.compose.ui.Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private inline fun <reified T : Any> NavDestination?.isRoute(): Boolean {
    return this?.hasRoute<T>() == true
}

private fun NavHostController.navigateSingleTop(route: Any) {
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