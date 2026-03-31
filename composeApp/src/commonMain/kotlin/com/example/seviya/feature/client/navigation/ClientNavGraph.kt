package com.example.seviya.feature.client.navigation

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
import com.example.seviya.feature.client.ClientDashboardScreen
import com.example.seviya.feature.client.ClientHomeScreen
import com.example.seviya.feature.client.ClientLocationCatalogScreen
import com.example.seviya.feature.client.ClientMapScreen
import com.example.seviya.feature.client.ClientPaymentUploadScreen
import com.example.seviya.feature.client.ClientRequestsScreen
import com.example.seviya.feature.client.ClientToWorkerReviewScreen
import com.example.seviya.feature.shared.DailyAgendaScreen
import com.example.seviya.feature.shared.MonthlyCalendarScreen
import com.example.seviya.feature.shared.WeeklyAgendaScreen
import com.example.seviya.feature.worker.FavoriteWorkersScreen
import com.example.shared.presentation.calendar.CalendarUserRole
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel

fun NavGraphBuilder.clientNavGraph(
    navController: NavHostController,
    currentClientId: String,
    onCurrentClientTabChange: (ClientTab) -> Unit,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onWorkerMenuExpandedChange: (Boolean) -> Unit,
    monthlyCalendarViewModel: MonthlyCalendarViewModel,
) {
  composable<ClientHome> {
    ClientHomeScreen(
        clientId = currentClientId,
        onWorkerClick = { workerId -> navController.navigate(ProfessionalProfile(workerId)) },
        onFavoritesClick = { navController.navigate(ClientFavorites) },
        onOpenCategoriesCatalog = { navController.navigateSingleTop(CategoriesCatalog) },
        onCategoryClick = { category ->
          navController.navigate(
              WorkersList(categoryId = category.id, categoryName = category.name)
          )
        },
    )
  }

  composable<ClientDashboard> {
    ClientDashboardScreen(
        clientId = currentClientId,
        onOpenAppointmentDetail = { bookingId ->
          navController.navigate(ClientAppointmentDetail(bookingId = bookingId))
        },
        onOpenAgenda = { navController.navigateSingleTop(ClientAgenda) },
        onOpenMap = {
          onCurrentClientTabChange(ClientTab.MAP)
          navController.navigateSingleTop(ClientMap)
        },
        onOpenRequests = { navController.navigateSingleTop(ClientRequests) },
        onOpenFavorites = { navController.navigateSingleTop(ClientFavorites) },
        onOpenCategories = {
          onCurrentClientTabChange(ClientTab.SERVICES)
          navController.navigateSingleTop(ClientHome)
        },
        onOpenServices = {
            onCurrentClientTabChange(ClientTab.SERVICES)
            navController.navigateSingleTop(ClientHome)
        },
        onOpenMenu = {
          onClientMenuExpandedChange(true)
          onWorkerMenuExpandedChange(false)
        },
    )
  }

  composable<ClientFavorites> {
    FavoriteWorkersScreen(
        clientId = currentClientId,
        onWorkerClick = { workerId ->
          navController.navigate(ProfessionalProfile(workerId = workerId))
        },
        onCategoriesClick = {
          onCurrentClientTabChange(ClientTab.SERVICES)
          navController.navigateSingleTop(ClientHome)
        },
        onBottomDashboard = {
          onCurrentClientTabChange(ClientTab.DASHBOARD)
          navController.navigateSingleTop(ClientDashboard)
        },
        onBottomMap = {
          onCurrentClientTabChange(ClientTab.MAP)
          navController.navigateSingleTop(ClientMap)
        },
        onBottomServices = {
          onCurrentClientTabChange(ClientTab.SERVICES)
          navController.navigateSingleTop(ClientHome)
        },
        onBottomNotifications = {
          onCurrentClientTabChange(ClientTab.ALERTS)
          navController.navigateSingleTop(ClientAlerts)
        },
        onBottomMenu = {
          onClientMenuExpandedChange(true)
          onWorkerMenuExpandedChange(false)
        },
    )
  }

  composable<ClientRequests> {
    ClientRequestsScreen(
        clientId = currentClientId,
        onOpenRequestDetail = { appointmentId ->
          navController.navigate(ClientAppointmentDetail(bookingId = appointmentId))
        },
        onOpenPaymentUpload = { appointmentId ->
          navController.navigate(ClientPaymentUpload(appointmentId = appointmentId))
        },
    )
  }

  composable<ClientPaymentUpload> { backStackEntry ->
    val route = backStackEntry.toRoute<ClientPaymentUpload>()
    ClientPaymentUploadScreen(
        appointmentId = route.appointmentId,
        onBack = { navController.popBackStack() },
    )
  }

  composable<ClientLocationCatalog> {
    ClientLocationCatalogScreen(
        clientId = currentClientId,
        onBack = { navController.popBackStack() },
    )
  }

  composable<ClientAppointmentDetail> { backStackEntry ->
    val route = backStackEntry.toRoute<ClientAppointmentDetail>()
    ClientAppointmentDetailScreen(
        bookingId = route.bookingId,
        onBack = { navController.popBackStack() },
        onReviewClick = {
          navController.navigate(ClientToWorkerReview(appointmentId = route.bookingId))
        },
        onGoDashboard = {
          onCurrentClientTabChange(ClientTab.DASHBOARD)
          navController.navigateSingleTop(ClientDashboard)
        },
        onGoMap = {
          onCurrentClientTabChange(ClientTab.MAP)
          navController.navigateSingleTop(ClientMap)
        },
        onGoServices = {
          onCurrentClientTabChange(ClientTab.SERVICES)
          navController.navigateSingleTop(ClientHome)
        },
        onGoAlerts = {
          onCurrentClientTabChange(ClientTab.ALERTS)
          navController.navigateSingleTop(ClientAlerts)
        },
        onGoMenu = {
          onClientMenuExpandedChange(true)
          onWorkerMenuExpandedChange(false)
        },
    )
  }

  composable<ClientMap> {
    ClientMapScreen(
        clientId = currentClientId,
        onWorkerClick = { workerId ->
          navController.navigate(ProfessionalProfile(workerId = workerId))
        },
    )
  }

  composable<ClientSearch> {
    FeaturePlaceholder(
        title = "Búsqueda",
        subtitle = "Aquí irá la búsqueda de trabajadores, categorías y servicios.",
    )
  }

  composable<ClientAlerts> {
    FeaturePlaceholder(
        title = "Alertas del cliente",
        subtitle = "Aquí irán las notificaciones y recordatorios del cliente.",
    )
  }

  composable<ClientDailyAppointments> { backStackEntry ->
    val route = backStackEntry.toRoute<ClientDailyAppointments>()

    DailyAgendaScreen(
        userId = route.clientId,
        role = CalendarUserRole.CLIENT,
        onBack = { navController.popBackStack() },
        onOpenDetail = { appointment ->
          navController.navigate(ClientAppointmentDetail(bookingId = appointment.id))
        },
    )
  }

  composable<ClientWeeklyAppointments> { backStackEntry ->
    val route = backStackEntry.toRoute<ClientWeeklyAppointments>()

    WeeklyAgendaScreen(
        userId = route.clientId,
        role = CalendarUserRole.CLIENT,
        onBack = { navController.popBackStack() },
        onOpenDetail = { appointment ->
          navController.navigate(ClientAppointmentDetail(bookingId = appointment.id))
        },
    )
  }

  composable<ClientAgenda> {
    MonthlyCalendarScreen(
        viewModel = monthlyCalendarViewModel,
        userId = currentClientId,
        userRole = CalendarUserRole.CLIENT,
        onBack = { navController.popBackStack() },
        onOpenMonthView = { navController.navigateSingleTop(ClientAgenda) },
        onOpenWeekView = {
          navController.navigate(ClientWeeklyAppointments(clientId = currentClientId))
        },
        onOpenDayView = {
          navController.navigate(ClientDailyAppointments(clientId = currentClientId))
        },
        onOpenAppointmentDetail = { appointment ->
          monthlyCalendarViewModel.selectAppointment(appointment)
          navController.navigateSingleTop(ClientAppointmentDetail(appointment.id))
        },
    )
  }

  composable<ClientToWorkerReview> { backStackEntry ->
    val route = backStackEntry.toRoute<ClientToWorkerReview>()

    ClientToWorkerReviewScreen(
        appointmentId = route.appointmentId,
        onBack = { navController.popBackStack() },
    )
  }

  composable<ClientProfile> {
    FeaturePlaceholder(
        title = "Perfil del cliente",
        subtitle = "Aquí irá la información del perfil del cliente.",
    )
  }
}
