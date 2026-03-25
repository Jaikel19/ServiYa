package com.example.seviya.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.seviya.UI.ClientAppointmentDetailScreen
import com.example.seviya.UI.ClientDashboardRoute
import com.example.seviya.UI.ClientHomeRoute
import com.example.seviya.UI.ClientLocationCatalogScreen
import com.example.seviya.UI.ClientMapScreen
import com.example.seviya.UI.ClientPaymentUploadScreen
import com.example.seviya.UI.ClientToWorkerReviewScreen
import com.example.seviya.UI.DailyAgendaScreen
import com.example.seviya.UI.FavoriteWorkersRoute
import com.example.seviya.UI.MonthlyCalendarScreen
import com.example.seviya.UI.ProfessionalProfileRoute
import com.example.seviya.UI.RequestAppointmentRoute
import com.example.seviya.UI.RoleAdmissionCatalogScreen
import com.example.seviya.UI.RoleCatalogScreen
import com.example.seviya.UI.TravelTimeConfigRoute
import com.example.seviya.UI.WeeklyAgendaScreen
import com.example.seviya.UI.WorkerAppointmentDetailScreen
import com.example.seviya.UI.WorkerCategoriesRoute
import com.example.seviya.UI.WorkerDailyAppointmentsScreen
import com.example.seviya.UI.WorkerDashboardRoute
import com.example.seviya.UI.WorkerPaymentDetailScreen
import com.example.seviya.UI.WorkerRequestDetailScreen
import com.example.seviya.UI.WorkerStartAppointmentOtpScreen
import com.example.seviya.UI.WorkerToClientReviewScreen
import com.example.seviya.UI.WorkersListRoute
import com.example.seviya.app.FeaturePlaceholder
import com.example.seviya.app.SessionRole
import com.example.seviya.app.buildCurrentTimeSnapshot
import com.example.seviya.app.navigateSingleTop
import com.example.seviya.app.toAppointment
import com.example.seviya.core.designsystem.components.ClientTab
import com.example.seviya.core.designsystem.components.WorkerTab
import com.example.seviya.core.navigation.CategoriesCatalog
import com.example.seviya.core.navigation.ClientAgenda
import com.example.seviya.core.navigation.ClientAlerts
import com.example.seviya.core.navigation.ClientAppointmentDetail
import com.example.seviya.core.navigation.ClientConfiguration
import com.example.seviya.core.navigation.ClientDailyAppointments
import com.example.seviya.core.navigation.ClientDashboard
import com.example.seviya.core.navigation.ClientFavorites
import com.example.seviya.core.navigation.ClientHome
import com.example.seviya.core.navigation.ClientLocationCatalog
import com.example.seviya.core.navigation.ClientMap
import com.example.seviya.core.navigation.ClientMessages
import com.example.seviya.core.navigation.ClientPaymentUpload
import com.example.seviya.core.navigation.ClientProfile
import com.example.seviya.core.navigation.ClientRequests
import com.example.seviya.core.navigation.ClientSearch
import com.example.seviya.core.navigation.ClientSettings
import com.example.seviya.core.navigation.ClientToWorkerReview
import com.example.seviya.core.navigation.ClientWeeklyAppointments
import com.example.seviya.core.navigation.Landing
import com.example.seviya.core.navigation.ProfessionalProfile
import com.example.seviya.core.navigation.RequestAppointment
import com.example.seviya.core.navigation.RoleAdmissionCatalog
import com.example.seviya.core.navigation.RoleCatalog
import com.example.seviya.core.navigation.Services
import com.example.seviya.core.navigation.TravelTimeConfig
import com.example.seviya.core.navigation.WorkerAgenda
import com.example.seviya.core.navigation.WorkerAlerts
import com.example.seviya.core.navigation.WorkerAppointmentDetail
import com.example.seviya.core.navigation.WorkerCategories
import com.example.seviya.core.navigation.WorkerConfiguration
import com.example.seviya.core.navigation.WorkerDailyAgenda
import com.example.seviya.core.navigation.WorkerDailyAppointments
import com.example.seviya.core.navigation.WorkerDashboard
import com.example.seviya.core.navigation.WorkerMessages
import com.example.seviya.core.navigation.WorkerPaymentDetail
import com.example.seviya.core.navigation.WorkerPortfolio
import com.example.seviya.core.navigation.WorkerProfile
import com.example.seviya.core.navigation.WorkerReports
import com.example.seviya.core.navigation.WorkerRequestDetail
import com.example.seviya.core.navigation.WorkerRequests
import com.example.seviya.core.navigation.WorkerSchedule
import com.example.seviya.core.navigation.WorkerServices
import com.example.seviya.core.navigation.WorkerSettings
import com.example.seviya.core.navigation.WorkerStartAppointmentOtp
import com.example.seviya.core.navigation.WorkerToClientReview
import com.example.seviya.core.navigation.WorkerWeeklyAppointments
import com.example.seviya.core.navigation.WorkersList
import com.example.seviya.feature.categories.CategoriesCatalogRoute
import com.example.seviya.feature.landing.LandingScreen
import com.example.seviya.ui.ClientRequestsScreen
import com.example.seviya.ui.ServicesScreen
import com.example.seviya.ui.WorkerRequestsScreen
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.ClientPaymentUpload.ClientPaymentUploadViewModel
import com.example.shared.presentation.WorkerPaymentDetail.WorkerPaymentDetailViewModel
import com.example.shared.presentation.WorkerRequest.WorkerRequestsViewModel
import com.example.shared.presentation.WorkerRequestDetailViewModel.WorkerRequestDetailViewModel
import com.example.shared.presentation.calendar.CalendarUserRole
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.categories.CategoriesViewModel
import com.example.shared.presentation.clientAppointmentDetail.ClientAppointmentDetailViewModel
import com.example.shared.presentation.clientDashboard.ClientDashboardViewModel
import com.example.shared.presentation.clientLocationCatalog.ClientLocationCatalogViewModel
import com.example.shared.presentation.clientMap.ClientMapViewModel
import com.example.shared.presentation.clientRequests.ClientRequestsViewModel
import com.example.shared.presentation.dailyAgenda.DailyAgendaViewModel
import com.example.shared.presentation.favoriteWorkers.FavoriteWorkersViewModel
import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel
import com.example.shared.presentation.requestAppointment.RequestAppointmentDraft
import com.example.shared.presentation.requestAppointment.RequestAppointmentViewModel
import com.example.shared.presentation.services.ServicesViewModel
import com.example.shared.presentation.workerAppointmentDetail.WorkerAppointmentDetailViewModel
import com.example.shared.presentation.workerCategories.WorkerCategoriesViewModel
import com.example.shared.presentation.workerDailyAppointments.WorkerDailyAppointmentsViewModel
import com.example.shared.presentation.workerDashboard.WorkerDashboardViewModel
import com.example.shared.presentation.workerStartAppointmentOtp.WorkerStartAppointmentOtpViewModel
import com.example.shared.presentation.workerToClientReview.WorkerToClientReviewViewModel
import com.example.shared.presentation.workerTravelTime.WorkerTravelTimeViewModel
import com.example.shared.presentation.workersList.WorkersListViewModel
import com.example.shared.utils.DateTimeUtils
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
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
    monthlyCalendarViewModel: MonthlyCalendarViewModel
) {
    val calendarState by monthlyCalendarViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Landing,
        modifier = modifier,
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
        homeNavGraph(
            navController = navController,
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
            selectedAppointment = calendarState.selectedAppointment
        )
    }
}

fun NavGraphBuilder.homeNavGraph(
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
    selectedAppointment: Appointment?
) {
    composable<Landing> {
        LandingScreen(
            onGoToServices = {
                onSessionRoleChange(SessionRole.GUEST)
                navController.navigateSingleTop(Services)
            },
            onLogin = {
                onSessionRoleChange(SessionRole.GUEST)
                navController.navigateSingleTop(RoleAdmissionCatalog)
            },
            onRegister = {
                onSessionRoleChange(SessionRole.GUEST)
                navController.navigateSingleTop(RoleCatalog)
            }
        )
    }

    composable<ClientHome> {
        ClientHomeRoute(
            clientId = currentClientId,
            categoriesViewModel = koinInject(),
            favoriteWorkersViewModel = koinInject(),
            workersListViewModel = koinInject(),
            onWorkerClick = { workerId ->
                navController.navigate(ProfessionalProfile(workerId))
            },
            onFavoritesClick = {
                navController.navigate(ClientFavorites)
            },
            onOpenCategoriesCatalog = {
                navController.navigateSingleTop(CategoriesCatalog)
            },
            onCategoryClick = { category ->
                navController.navigate(
                    WorkersList(
                        categoryId = category.id,
                        categoryName = category.name
                    )
                )
            }
        )
    }

    composable<Services> {
        val viewModel: ServicesViewModel = koinViewModel()
        ServicesScreen(viewModel)
    }

    composable<TravelTimeConfig> {
        val viewModel: WorkerTravelTimeViewModel = koinViewModel()
        TravelTimeConfigRoute(
            workerId = currentWorkerId,
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
        )
    }

    composable<RoleCatalog> {
        RoleCatalogScreen(
            onGoHome = {
                onSessionRoleChange(SessionRole.GUEST)
                onRequestAppointmentDraftChange(null)
                navController.navigateSingleTop(Landing)
            },
            onGoLogin = {
                onSessionRoleChange(SessionRole.GUEST)
                navController.navigateSingleTop(RoleAdmissionCatalog)
            },
            onGoRegister = { },
            onPickClient = {
                onSessionRoleChange(SessionRole.CLIENT)
                onCurrentClientIdChange("client_demo_001")
                onCurrentClientNameChange("Cliente Demo")
                onCurrentClientTabChange(ClientTab.SERVICES)
                onRequestAppointmentDraftChange(null)
                navController.navigateSingleTop(ClientHome)
            },
            onPickWorker = {
                onSessionRoleChange(SessionRole.WORKER)
                onCurrentWorkerTabChange(WorkerTab.DASHBOARD)
                onCurrentWorkerIdChange("worker_demo_001")
                onRequestAppointmentDraftChange(null)
                navController.navigateSingleTop(WorkerDashboard)
            }
        )
    }

    composable<RoleAdmissionCatalog> {
        RoleAdmissionCatalogScreen(
            onGoHome = {
                onSessionRoleChange(SessionRole.GUEST)
                onRequestAppointmentDraftChange(null)
                navController.navigateSingleTop(Landing)
            },
            onGoLogin = { },
            onGoRegister = {
                onSessionRoleChange(SessionRole.GUEST)
                navController.navigateSingleTop(RoleCatalog)
            },
            onPickClient = {
                onSessionRoleChange(SessionRole.CLIENT)
                onCurrentClientIdChange("client_demo_001")
                onCurrentClientNameChange("Cliente Demo")
                onCurrentClientTabChange(ClientTab.SERVICES)
                onRequestAppointmentDraftChange(null)
                navController.navigateSingleTop(ClientHome)
            },
            onPickWorker = {
                onSessionRoleChange(SessionRole.WORKER)
                onCurrentWorkerTabChange(WorkerTab.DASHBOARD)
                onCurrentWorkerIdChange("worker_demo_001")
                onRequestAppointmentDraftChange(null)
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
                onCurrentClientTabChange(ClientTab.SERVICES)
                navController.navigateSingleTop(ClientHome)
            },
            onGoMap = {
                onCurrentClientTabChange(ClientTab.MAP)
                navController.navigateSingleTop(ClientMap)
            },
            onGoSearch = {
                onCurrentClientTabChange(ClientTab.SEARCH)
                navController.navigateSingleTop(ClientSearch)
            },
            onGoAlerts = {
                onCurrentClientTabChange(ClientTab.ALERTS)
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

        val currentTimeSnapshot = remember {
            buildCurrentTimeSnapshot()
        }

        WorkersListRoute(
            clientId = currentClientId,
            viewModel = viewModel,
            currentTime = currentTimeSnapshot,
            selectedCategoryId = route.categoryId,
            selectedCategoryName = route.categoryName,
            onWorkerClick = { workerId ->
                navController.navigate(
                    ProfessionalProfile(workerId = workerId)
                )
            },
            onFavoritesClick = {
                navController.navigateSingleTop(ClientFavorites)
            },
            onBottomServices = {
                onCurrentClientTabChange(ClientTab.SERVICES)
                navController.navigateSingleTop(ClientHome)
            },
            onBottomMap = {
                onCurrentClientTabChange(ClientTab.MAP)
                navController.navigateSingleTop(ClientMap)
            },
            onBottomSearch = {
                onCurrentClientTabChange(ClientTab.SEARCH)
                navController.navigateSingleTop(ClientSearch)
            },
            onBottomNotifications = {
                onCurrentClientTabChange(ClientTab.ALERTS)
                navController.navigateSingleTop(ClientAlerts)
            },
            onBottomMenu = {
                onClientMenuExpandedChange(true)
                onWorkerMenuExpandedChange(false)
            }
        )
    }

    composable<ProfessionalProfile> { backStackEntry ->
        val route = backStackEntry.toRoute<ProfessionalProfile>()
        val viewModel: ProfessionalProfileViewModel = koinViewModel()

        ProfessionalProfileRoute(
            clientId = currentClientId,
            workerId = route.workerId,
            viewModel = viewModel,
            onBack = { navController.popBackStack() },
            onBottomServices = {
                onCurrentClientTabChange(ClientTab.SERVICES)
                navController.navigateSingleTop(ClientHome)
            },
            onBottomMap = {
                onCurrentClientTabChange(ClientTab.MAP)
                navController.navigateSingleTop(ClientMap)
            },
            onBottomSearch = {
                onCurrentClientTabChange(ClientTab.SEARCH)
                navController.navigateSingleTop(ClientSearch)
            },
            onBottomNotifications = {
                onCurrentClientTabChange(ClientTab.ALERTS)
                navController.navigateSingleTop(ClientAlerts)
            },
            onBottomMenu = {
                onClientMenuExpandedChange(true)
                onWorkerMenuExpandedChange(false)
            },
            onProcessAppointment = { profile, selectedServices, workerAppointments ->
                onRequestAppointmentDraftChange(
                    RequestAppointmentDraft(
                        clientId = currentClientId,
                        clientName = currentClientName,
                        workerId = profile.workerId.ifBlank { route.workerId },
                        workerName = profile.name,
                        workerImageUrl = profile.profilePictureLink,
                        workerProvince = profile.locationProvince,
                        selectedServices = selectedServices,
                        schedule = profile.schedule,
                        travelTimeMinutes = profile.travelTime,
                        workerAppointments = workerAppointments,
                        currentTime = buildCurrentTimeSnapshot()
                    )
                )

                navController.navigateSingleTop(RequestAppointment)
            }
        )
    }

    composable<RequestAppointment> {
        val viewModel: RequestAppointmentViewModel = koinViewModel()

        if (requestAppointmentDraft == null) {
            FeaturePlaceholder(
                title = "Solicitud de cita",
                subtitle = "No hay servicios seleccionados para procesar."
            )
        } else {
            RequestAppointmentRoute(
                draft = requestAppointmentDraft,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenRequests = {
                    navController.navigateSingleTop(ClientRequests)
                },
                onOpenHome = {
                    onCurrentClientTabChange(ClientTab.SERVICES)
                    navController.navigateSingleTop(ClientHome)
                }
            )
        }
    }

    composable<ClientDashboard> {
        val viewModel: ClientDashboardViewModel = koinViewModel()

        ClientDashboardRoute(
            clientId = currentClientId,
            viewModel = viewModel,
            onOpenAppointmentDetail = { bookingId ->
                navController.navigate(
                    ClientAppointmentDetail(bookingId = bookingId)
                )
            },
            onOpenAgenda = {
                navController.navigateSingleTop(ClientAgenda)
            },
            onOpenMessages = {
                navController.navigateSingleTop(ClientMessages)
            },
            onOpenProfile = {
                navController.navigateSingleTop(ClientProfile)
            },
            onOpenLocations = {
                onCurrentClientTabChange(ClientTab.MAP)
                navController.navigateSingleTop(ClientMap)
            },
            onOpenReports = {
                navController.navigateSingleTop(ClientConfiguration)
            },
            onOpenRequests = {
                navController.navigateSingleTop(ClientRequests)
            },
            onOpenCategories = {
                onCurrentClientTabChange(ClientTab.SERVICES)
                navController.navigateSingleTop(ClientHome)
            },
            onOpenMenu = {
                onClientMenuExpandedChange(true)
                onWorkerMenuExpandedChange(false)
            }
        )
    }

    composable<ClientFavorites> {
        val viewModel: FavoriteWorkersViewModel = koinViewModel()

        FavoriteWorkersRoute(
            clientId = currentClientId,
            viewModel = viewModel,
            onWorkerClick = { workerId ->
                navController.navigate(
                    ProfessionalProfile(workerId = workerId)
                )
            },
            onCategoriesClick = {
                onCurrentClientTabChange(ClientTab.SERVICES)
                navController.navigateSingleTop(ClientHome)
            },
            onBottomServices = {
                onCurrentClientTabChange(ClientTab.SERVICES)
                navController.navigateSingleTop(ClientHome)
            },
            onBottomMap = {
                onCurrentClientTabChange(ClientTab.MAP)
                navController.navigateSingleTop(ClientMap)
            },
            onBottomSearch = {
                onCurrentClientTabChange(ClientTab.SEARCH)
                navController.navigateSingleTop(ClientSearch)
            },
            onBottomNotifications = {
                onCurrentClientTabChange(ClientTab.ALERTS)
                navController.navigateSingleTop(ClientAlerts)
            },
            onBottomMenu = {
                onClientMenuExpandedChange(true)
                onWorkerMenuExpandedChange(false)
            }
        )
    }

    composable<ClientRequests> {
        val viewModel: ClientRequestsViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(currentClientId) {
            viewModel.loadRequests(currentClientId)
        }

        ClientRequestsScreen(
            uiState = uiState,
            onOpenRequestDetail = { appointmentId ->
                navController.navigate(
                    ClientAppointmentDetail(bookingId = appointmentId)
                )
            },
            onOpenPaymentUpload = { appointmentId ->
                navController.navigate(
                    ClientPaymentUpload(appointmentId = appointmentId)
                )
            }
        )
    }

    composable<ClientPaymentUpload> { backStackEntry ->
        val route = backStackEntry.toRoute<ClientPaymentUpload>()
        val viewModel: ClientPaymentUploadViewModel = koinViewModel()

        LaunchedEffect(route.appointmentId) {
            viewModel.loadData(route.appointmentId)
        }

        ClientPaymentUploadScreen(
            appointmentId = route.appointmentId,
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
        )
    }

    composable<ClientLocationCatalog> {
        val viewModel: ClientLocationCatalogViewModel = koinViewModel()
        ClientLocationCatalogScreen(
            clientId = currentClientId,
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
        )
    }

    composable<ClientAppointmentDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<ClientAppointmentDetail>()
        val viewModel: ClientAppointmentDetailViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(route.bookingId) {
            viewModel.loadAppointmentDetail(route.bookingId)
        }

        ClientAppointmentDetailScreen(
            uiState = uiState,
            onBack = { navController.popBackStack() },
            onRequestCancellationPreview = {
                viewModel.prepareCancellationPreview(
                    currentDateTime = DateTimeUtils.nowIsoMinute()
                )
            },
            onCancelAppointment = {
                viewModel.cancelAppointmentByClient(
                    currentDateTime = DateTimeUtils.nowIsoMinute()
                )
            },
            onDismissCancellationPreview = {
                viewModel.dismissCancellationPreview()
            },
            onChatClick = { },
            onReviewClick = {
                navController.navigate(
                    ClientToWorkerReview(appointmentId = route.bookingId)
                )
            },
            onGoServices = {
                onCurrentClientTabChange(ClientTab.SERVICES)
                navController.navigateSingleTop(ClientHome)
            },
            onGoMap = {
                onCurrentClientTabChange(ClientTab.MAP)
                navController.navigateSingleTop(ClientMap)
            },
            onGoSearch = {
                onCurrentClientTabChange(ClientTab.SEARCH)
                navController.navigateSingleTop(ClientSearch)
            },
            onGoAlerts = {
                onCurrentClientTabChange(ClientTab.ALERTS)
                navController.navigateSingleTop(ClientAlerts)
            },
            onGoMenu = {
                onClientMenuExpandedChange(true)
                onWorkerMenuExpandedChange(false)
            }
        )
    }

    composable<ClientMap> {
        val viewModel: ClientMapViewModel = koinViewModel()
        ClientMapScreen(
            clientId = currentClientId,
            viewModel = viewModel,
            onWorkerClick = { workerId ->
                navController.navigate(ProfessionalProfile(workerId = workerId))
            }
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

    composable<ClientDailyAppointments> { backStackEntry ->
        val route = backStackEntry.toRoute<ClientDailyAppointments>()
        val viewModel: DailyAgendaViewModel = koinViewModel()

        DailyAgendaScreen(
            viewModel = viewModel,
            userId = route.clientId,
            role = CalendarUserRole.CLIENT,
            onBack = { navController.popBackStack() },
            onOpenDetail = { appointment ->
                navController.navigate(
                    ClientAppointmentDetail(bookingId = appointment.id)
                )
            }
        )
    }

    composable<ClientWeeklyAppointments> { backStackEntry ->
        val route = backStackEntry.toRoute<ClientWeeklyAppointments>()
        val viewModel: DailyAgendaViewModel = koinViewModel()

        WeeklyAgendaScreen(
            viewModel = viewModel,
            userId = route.clientId,
            role = CalendarUserRole.CLIENT,
            onBack = { navController.popBackStack() },
            onOpenDetail = { appointment ->
                navController.navigate(
                    ClientAppointmentDetail(bookingId = appointment.id)
                )
            }
        )
    }

    composable<ClientAgenda> {
        MonthlyCalendarScreen(
            viewModel = monthlyCalendarViewModel,
            userId = currentClientId,
            userRole = CalendarUserRole.CLIENT,
            onBack = { navController.popBackStack() },
            onOpenMonthView = {
                navController.navigateSingleTop(ClientAgenda)
            },
            onOpenWeekView = {
                navController.navigate(ClientWeeklyAppointments(clientId = currentClientId))
            },
            onOpenDayView = {
                navController.navigate(ClientDailyAppointments(clientId = currentClientId))
            },
            onOpenAppointmentDetail = { appointment ->
                monthlyCalendarViewModel.selectAppointment(appointment)
                navController.navigateSingleTop(ClientAppointmentDetail(appointment.id))
            }
        )
    }

    composable<ClientToWorkerReview> { backStackEntry ->
        val route = backStackEntry.toRoute<ClientToWorkerReview>()

        ClientToWorkerReviewScreen(
            appointmentId = route.appointmentId,
            onBack = { navController.popBackStack() }
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
                monthlyCalendarViewModel.selectAppointment(booking.toAppointment())
                navController.navigateSingleTop(WorkerAppointmentDetail)
            },
            onStartAppointment = { booking ->
                monthlyCalendarViewModel.startAppointment(booking.id)
            },
            onCompleteAppointment = { booking ->
                monthlyCalendarViewModel.completeAppointment(booking.id)
            },
            onOpenReview = { booking ->
                monthlyCalendarViewModel.selectAppointment(booking.toAppointment())
                navController.navigateSingleTop(WorkerAppointmentDetail)
            }
        )
    }

    composable<WorkerDailyAppointments> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerDailyAppointments>()
        val viewModel: WorkerDailyAppointmentsViewModel = koinViewModel()

        LaunchedEffect(route.workerId) {
            viewModel.loadAppointments(route.workerId)
        }

        WorkerDailyAppointmentsScreen(
            workerId = route.workerId,
            viewModel = viewModel,
            onBack = { navController.popBackStack() },
            onOpenMaps = { _, _, _ -> }
        )
    }

    composable<WorkerWeeklyAppointments> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerWeeklyAppointments>()
        val viewModel: DailyAgendaViewModel = koinViewModel()

        WeeklyAgendaScreen(
            viewModel = viewModel,
            userId = route.workerId,
            role = CalendarUserRole.WORKER,
            onBack = { navController.popBackStack() },
            onOpenDetail = { appointment ->
                monthlyCalendarViewModel.selectAppointment(appointment)
                navController.navigateSingleTop(WorkerAppointmentDetail)
            }
        )
    }

    composable<WorkerDailyAgenda> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerDailyAgenda>()
        val viewModel: DailyAgendaViewModel = koinViewModel()

        DailyAgendaScreen(
            viewModel = viewModel,
            userId = route.workerId,
            role = CalendarUserRole.WORKER,
            onBack = { navController.popBackStack() },
            onOpenDetail = { appointment ->
                monthlyCalendarViewModel.selectAppointment(appointment)
                navController.navigateSingleTop(WorkerAppointmentDetail)
            }
        )
    }

    composable<WorkerAgenda> {
        MonthlyCalendarScreen(
            viewModel = monthlyCalendarViewModel,
            userId = currentWorkerId,
            userRole = CalendarUserRole.WORKER,
            onBack = { navController.popBackStack() },
            onOpenMonthView = {
                navController.navigateSingleTop(WorkerAgenda)
            },
            onOpenWeekView = {
                navController.navigate(WorkerWeeklyAppointments(workerId = currentWorkerId))
            },
            onOpenDayView = {
                navController.navigate(WorkerDailyAgenda(workerId = currentWorkerId))
            },
            onOpenAppointmentDetail = { appointment ->
                monthlyCalendarViewModel.selectAppointment(appointment)
                navController.navigateSingleTop(WorkerAppointmentDetail)
            }
        )
    }

    composable<WorkerAppointmentDetail> {
        val detailViewModel: WorkerAppointmentDetailViewModel = koinViewModel()
        val detailUiState by detailViewModel.uiState.collectAsState()

        selectedAppointment?.let { appointment ->
            LaunchedEffect(appointment.id) {
                detailViewModel.loadPaymentReceipt(appointment.id)
                detailViewModel.loadReviewMeta(appointment.id)
            }

            WorkerAppointmentDetailScreen(
                appointment = appointment,
                paymentReceipt = detailUiState.paymentReceipt,
                reviewMeta = detailUiState.reviewMeta,
                onBack = {
                    detailViewModel.clearState()
                    monthlyCalendarViewModel.clearSelectedAppointment()
                    navController.popBackStack()
                },
                onOpenGoogleMaps = {},
                onOpenWaze = {},
                onVerifyPayment = {
                    monthlyCalendarViewModel.confirmPayment(appointment.id)
                },
                onStartAppointment = {
                    navController.navigate(WorkerStartAppointmentOtp(appointmentId = appointment.id))
                },
                onFinishAppointment = {
                    monthlyCalendarViewModel.completeAppointment(appointment.id)
                },
                onRateClient = {
                    navController.navigate(WorkerToClientReview(appointmentId = appointment.id))
                },
                onRequestCancellationPreview = {
                    detailViewModel.prepareCancellationPreview(appointment)
                },
                onCancelAppointment = {
                    detailViewModel.cancelAppointmentByWorker(
                        appointment = appointment,
                        currentDateTime = DateTimeUtils.nowIsoMinute()
                    )
                },
                onDismissCancellationPreview = {
                    detailViewModel.dismissCancellationPreview()
                },
                onGoServices = {
                    onCurrentWorkerTabChange(WorkerTab.DASHBOARD)
                    navController.navigateSingleTop(WorkerDashboard)
                },
                onGoMap = {},
                onGoSearch = {
                    onCurrentWorkerTabChange(WorkerTab.REQUESTS)
                    navController.navigateSingleTop(WorkerRequests)
                },
                onGoAlerts = {
                    onCurrentWorkerTabChange(WorkerTab.ALERTS)
                    navController.navigateSingleTop(WorkerAlerts)
                },
                onGoMenu = {
                    onWorkerMenuExpandedChange(true)
                    onClientMenuExpandedChange(false)
                }
            )
        } ?: FeaturePlaceholder(
            title = "Detalle de cita",
            subtitle = "No hay una cita seleccionada en este momento."
        )
    }

    composable<WorkerStartAppointmentOtp> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerStartAppointmentOtp>()
        val viewModel: WorkerStartAppointmentOtpViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(route.appointmentId) {
            viewModel.loadData(route.appointmentId)
        }

        LaunchedEffect(uiState.startSuccess) {
            if (uiState.startSuccess) {
                navController.popBackStack()
                navController.popBackStack()
            }
        }

        WorkerStartAppointmentOtpScreen(
            uiState = uiState,
            onBack = { navController.popBackStack() },
            onOtpChange = viewModel::onOtpChanged,
            onStartAppointment = { viewModel.startAppointmentWithOtp() }
        )
    }

    composable<WorkerRequests> {
        val viewModel: WorkerRequestsViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(currentWorkerId) {
            viewModel.loadRequests(currentWorkerId)
        }

        WorkerRequestsScreen(
            uiState = uiState,
            onAccept = { appointment -> viewModel.acceptRequest(appointment) },
            onReject = { appointment -> viewModel.rejectRequest(appointment) },
            onConfirm = { appointment -> viewModel.confirmPayment(appointment) },
            onCancel = { appointment -> viewModel.cancelPayment(appointment) },
            onLoadPaymentPending = { viewModel.loadPaymentPending(currentWorkerId) },
            onOpenRequestDetail = { appointmentId ->
                navController.navigate(WorkerRequestDetail(bookingId = appointmentId))
            },
            onOpenPaymentDetail = { appointmentId ->
                navController.navigate(WorkerPaymentDetail(bookingId = appointmentId))
            }
        )
    }

    composable<WorkerRequestDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerRequestDetail>()
        val viewModel: WorkerRequestDetailViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(route.bookingId) {
            viewModel.loadBooking(route.bookingId)
        }

        uiState.appointment?.let { appointment ->
            WorkerRequestDetailScreen(
                booking = appointment,
                onBack = { navController.popBackStack() },
                onAccept = {
                    viewModel.acceptRequest()
                    navController.popBackStack()
                },
                onReject = {
                    viewModel.rejectRequest()
                    navController.popBackStack()
                }
            )
        } ?: FeaturePlaceholder(
            title = "Solicitud",
            subtitle = "No se encontró la solicitud."
        )
    }

    composable<WorkerPaymentDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerPaymentDetail>()
        val viewModel: WorkerPaymentDetailViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(route.bookingId) {
            viewModel.loadPaymentDetail(route.bookingId)
        }

        LaunchedEffect(uiState.paymentVerified) {
            if (uiState.paymentVerified) {
                navController.popBackStack()
            }
        }

        uiState.appointment?.let { appointment ->
            WorkerPaymentDetailScreen(
                appointment = appointment,
                paymentReceipt = uiState.paymentReceipt,
                onBack = { navController.popBackStack() },
                onVerifyPayment = {
                    viewModel.verifyPayment()
                    navController.popBackStack()
                },
                onReportProblem = {
                    viewModel.reportProblem()
                    navController.popBackStack()
                }
            )
        } ?: FeaturePlaceholder(
            title = "Comprobante",
            subtitle = "No se encontró el pago."
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
            title = "Mensajes",
            subtitle = "Aquí irán los chats y conversaciones con clientes."
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

    composable<WorkerCategories> {
        val viewModel: WorkerCategoriesViewModel = koinViewModel()
        WorkerCategoriesRoute(
            workerId = currentWorkerId,
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
        )
    }

    composable<WorkerToClientReview> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerToClientReview>()
        val viewModel: WorkerToClientReviewViewModel = koinViewModel()

        LaunchedEffect(route.appointmentId) {
            viewModel.loadAppointment(route.appointmentId)
        }

        WorkerToClientReviewScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() },
            onSubmitSuccess = {
                navController.popBackStack()
            }
        )
    }
}