package com.example.seviya.feature.client.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.seviya.app.FeaturePlaceholder
import com.example.seviya.app.navigateSingleTop
import com.example.seviya.core.designsystem.components.ClientTab
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
import com.example.seviya.core.navigation.ProfessionalProfile
import com.example.seviya.core.navigation.WorkersList
import com.example.seviya.feature.client.ClientAppointmentDetailScreen
import com.example.seviya.feature.client.ClientDashboardRoute
import com.example.seviya.feature.client.ClientHomeRoute
import com.example.seviya.feature.client.ClientLocationCatalogScreen
import com.example.seviya.feature.client.ClientMapScreen
import com.example.seviya.feature.client.ClientPaymentUploadScreen
import com.example.seviya.feature.client.ClientToWorkerReviewScreen
import com.example.seviya.feature.shared.DailyAgendaScreen
import com.example.seviya.feature.shared.MonthlyCalendarScreen
import com.example.seviya.feature.shared.WeeklyAgendaScreen
import com.example.seviya.feature.worker.FavoriteWorkersRoute
import com.example.seviya.ui.ClientRequestsScreen
import com.example.shared.presentation.ClientPaymentUpload.ClientPaymentUploadViewModel
import com.example.shared.presentation.calendar.CalendarUserRole
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.clientAppointmentDetail.ClientAppointmentDetailViewModel
import com.example.shared.presentation.clientDashboard.ClientDashboardViewModel
import com.example.shared.presentation.clientLocationCatalog.ClientLocationCatalogViewModel
import com.example.shared.presentation.clientMap.ClientMapViewModel
import com.example.shared.presentation.clientRequests.ClientRequestsViewModel
import com.example.shared.presentation.dailyAgenda.DailyAgendaViewModel
import com.example.shared.presentation.favoriteWorkers.FavoriteWorkersViewModel
import com.example.shared.utils.DateTimeUtils
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.clientNavGraph(
    navController: NavHostController,
    currentClientId: String,
    onCurrentClientTabChange: (ClientTab) -> Unit,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onWorkerMenuExpandedChange: (Boolean) -> Unit,
    monthlyCalendarViewModel: MonthlyCalendarViewModel
) {
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
}