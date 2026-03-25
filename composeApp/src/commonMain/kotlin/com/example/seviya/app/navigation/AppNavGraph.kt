package com.example.seviya.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
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
import com.example.seviya.feature.shared.ProfessionalProfileScreen
import com.example.seviya.feature.shared.RequestAppointmentScreen
import com.example.seviya.feature.landing.ServicesScreen
import com.example.seviya.feature.landing.RoleAdmissionCatalogScreen
import com.example.seviya.feature.landing.RoleCatalogScreen
import com.example.seviya.feature.client.navigation.clientNavGraph
import com.example.seviya.feature.worker.navigation.workerNavGraph
import com.example.seviya.feature.worker.TravelTimeConfigScreen
import com.example.seviya.feature.worker.WorkersListScreen
import com.example.seviya.app.FeaturePlaceholder
import com.example.seviya.app.SessionRole
import com.example.seviya.app.buildCurrentTimeSnapshot
import com.example.seviya.app.navigateSingleTop
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
import com.example.seviya.core.navigation.WorkerDashboard
import com.example.seviya.core.navigation.WorkersList
import com.example.seviya.feature.categories.CategoriesCatalogScreen
import com.example.seviya.feature.landing.LandingScreen
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.requestAppointment.RequestAppointmentDraft

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

    clientNavGraph(
        navController = navController,
        currentClientId = currentClientId,
        onCurrentClientTabChange = onCurrentClientTabChange,
        onClientMenuExpandedChange = onClientMenuExpandedChange,
        onWorkerMenuExpandedChange = onWorkerMenuExpandedChange,
        monthlyCalendarViewModel = monthlyCalendarViewModel
    )

    composable<Services> {
        ServicesScreen()
    }

    composable<TravelTimeConfig> {
        TravelTimeConfigScreen(
            workerId = currentWorkerId,
            onBack = { navController.popBackStack() }
        )
    }

    composable<RoleCatalog> {
        RoleCatalogScreen(
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
        var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
        var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }

        CategoriesCatalogScreen(
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

        val currentTimeSnapshot = remember {
            buildCurrentTimeSnapshot()
        }

        WorkersListScreen(
            clientId = currentClientId,
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

        ProfessionalProfileScreen(
            clientId = currentClientId,
            workerId = route.workerId,
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
        if (requestAppointmentDraft == null) {
            FeaturePlaceholder(
                title = "Solicitud de cita",
                subtitle = "No hay servicios seleccionados para procesar."
            )
        } else {
            RequestAppointmentScreen(
                draft = requestAppointmentDraft,
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

    workerNavGraph(
        navController = navController,
        currentWorkerId = currentWorkerId,
        onCurrentWorkerTabChange = onCurrentWorkerTabChange,
        onClientMenuExpandedChange = onClientMenuExpandedChange,
        onWorkerMenuExpandedChange = onWorkerMenuExpandedChange,
        monthlyCalendarViewModel = monthlyCalendarViewModel,
        selectedAppointment = selectedAppointment
    )
}