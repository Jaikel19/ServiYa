package com.example.seviya.feature.worker.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.seviya.app.FeaturePlaceholder
import com.example.seviya.app.navigateSingleTop
import com.example.seviya.app.toAppointment
import com.example.seviya.core.designsystem.components.WorkerTab
import com.example.seviya.core.navigation.WorkerAgenda
import com.example.seviya.core.navigation.WorkerAlerts
import com.example.seviya.core.navigation.WorkerAppointmentDetail
import com.example.seviya.core.navigation.WorkerCategories
import com.example.seviya.core.navigation.WorkerConfiguration
import com.example.seviya.core.navigation.WorkerDailyAgenda
import com.example.seviya.core.navigation.WorkerDailyAppointments
import com.example.seviya.core.navigation.WorkerDashboard
import com.example.seviya.core.navigation.WorkerPaymentDetail
import com.example.seviya.core.navigation.WorkerPortfolio
import com.example.seviya.core.navigation.WorkerRequestDetail
import com.example.seviya.core.navigation.WorkerRequests
import com.example.seviya.core.navigation.WorkerSchedule
import com.example.seviya.core.navigation.WorkerServices
import com.example.seviya.core.navigation.WorkerStartAppointmentOtp
import com.example.seviya.core.navigation.WorkerToClientReview
import com.example.seviya.core.navigation.WorkerWeeklyAppointments
import com.example.seviya.feature.shared.DailyAgendaScreen
import com.example.seviya.feature.shared.MonthlyCalendarScreen
import com.example.seviya.feature.shared.WeeklyAgendaScreen
import com.example.seviya.feature.worker.WorkerAppointmentDetailScreen
import com.example.seviya.feature.worker.WorkerCategoriesScreen
import com.example.seviya.feature.worker.WorkerDailyAppointmentsScreen
import com.example.seviya.feature.worker.WorkerDashboardScreen
import com.example.seviya.feature.worker.WorkerPaymentDetailScreen
import com.example.seviya.feature.worker.WorkerRequestDetailScreen
import com.example.seviya.feature.worker.WorkerRequestsScreen
import com.example.seviya.feature.worker.WorkerStartAppointmentOtpScreen
import com.example.seviya.feature.worker.WorkerToClientReviewScreen
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.calendar.CalendarUserRole
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.seviya.core.navigation.TravelTimeConfig
import com.example.seviya.feature.worker.WorkerAlertsScreen
import com.example.seviya.core.navigation.ProfessionalProfile

fun NavGraphBuilder.workerNavGraph(
    navController: NavHostController,
    currentWorkerId: String,
    onCurrentWorkerTabChange: (WorkerTab) -> Unit,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onWorkerMenuExpandedChange: (Boolean) -> Unit,
    monthlyCalendarViewModel: MonthlyCalendarViewModel,
    selectedAppointment: Appointment?,
) {
    composable<WorkerDashboard> {
        WorkerDashboardScreen(
            workerId = currentWorkerId,
            onOpenCategories = {
                navController.navigateSingleTop(WorkerCategories)
            },
            onOpenTravelTime = {
                navController.navigateSingleTop(TravelTimeConfig)
            },
            onOpenDailyAgenda = {
                onCurrentWorkerTabChange(WorkerTab.AGENDA)
                navController.navigate(WorkerDailyAgenda(workerId = currentWorkerId))
            },
            onOpenSchedule = { navController.navigateSingleTop(WorkerSchedule) },
            onOpenPortfolio = { navController.navigateSingleTop(WorkerPortfolio) },
            onOpenAppointmentDetail = { appointment ->
                monthlyCalendarViewModel.selectAppointment(appointment)
                navController.navigateSingleTop(WorkerAppointmentDetail)
            },
            onStartAppointment = { appointment ->
                monthlyCalendarViewModel.startAppointment(appointment.id)
            },
            onCompleteAppointment = { appointment ->
                monthlyCalendarViewModel.completeAppointment(appointment.id)
            },
            onOpenReview = { appointment ->
                monthlyCalendarViewModel.selectAppointment(appointment)
                navController.navigateSingleTop(WorkerAppointmentDetail)
            },
        )
    }

  composable<WorkerDailyAppointments> { backStackEntry ->
    val route = backStackEntry.toRoute<WorkerDailyAppointments>()

    WorkerDailyAppointmentsScreen(
        workerId = route.workerId,
        onBack = { navController.popBackStack() },
        onOpenMaps = { _, _, _ -> },
        onOpenAppointmentDetail = { appointment ->
            monthlyCalendarViewModel.selectAppointment(appointment)
            navController.navigateSingleTop(WorkerAppointmentDetail)
        },
    )
  }

  composable<WorkerWeeklyAppointments> { backStackEntry ->
    val route = backStackEntry.toRoute<WorkerWeeklyAppointments>()

    WeeklyAgendaScreen(
        userId = route.workerId,
        role = CalendarUserRole.WORKER,
        onBack = { navController.popBackStack() },
        onOpenDetail = { appointment ->
          monthlyCalendarViewModel.selectAppointment(appointment)
          navController.navigateSingleTop(WorkerAppointmentDetail)
        },
    )
  }

  composable<WorkerDailyAgenda> { backStackEntry ->
    val route = backStackEntry.toRoute<WorkerDailyAgenda>()

    DailyAgendaScreen(
        userId = route.workerId,
        role = CalendarUserRole.WORKER,
        onBack = { navController.popBackStack() },
        onOpenDetail = { appointment ->
          monthlyCalendarViewModel.selectAppointment(appointment)
          navController.navigateSingleTop(WorkerAppointmentDetail)
        },
    )
  }

  composable<WorkerAgenda> {
    MonthlyCalendarScreen(
        viewModel = monthlyCalendarViewModel,
        userId = currentWorkerId,
        userRole = CalendarUserRole.WORKER,
        onBack = { navController.popBackStack() },
        onOpenMonthView = { navController.navigateSingleTop(WorkerAgenda) },
        onOpenWeekView = {
          navController.navigate(WorkerWeeklyAppointments(workerId = currentWorkerId))
        },
        onOpenDayView = { navController.navigate(WorkerDailyAgenda(workerId = currentWorkerId)) },
        onOpenAppointmentDetail = { appointment ->
          monthlyCalendarViewModel.selectAppointment(appointment)
          navController.navigateSingleTop(WorkerAppointmentDetail)
        },
    )
  }

  composable<WorkerAppointmentDetail> {
    selectedAppointment?.let { appointment ->
      WorkerAppointmentDetailScreen(
          appointment = appointment,
          monthlyCalendarViewModel = monthlyCalendarViewModel,
          onBack = { navController.popBackStack() },
          onStartAppointment = { appointmentId ->
            navController.navigate(WorkerStartAppointmentOtp(appointmentId = appointmentId))
          },
          onRateClient = { appointmentId ->
            navController.navigate(WorkerToClientReview(appointmentId = appointmentId))
          },
          onGoServices = {
            onCurrentWorkerTabChange(WorkerTab.DASHBOARD)
            navController.navigateSingleTop(WorkerDashboard)
          },
          onOpenPaymentDetail = { appointmentId ->
              navController.navigate(WorkerPaymentDetail(bookingId = appointmentId))
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
          },
      )
    }
        ?: FeaturePlaceholder(
            title = "Detalle de cita",
            subtitle = "No hay una cita seleccionada en este momento.",
        )
  }

  composable<WorkerStartAppointmentOtp> { backStackEntry ->
    val route = backStackEntry.toRoute<WorkerStartAppointmentOtp>()
    WorkerStartAppointmentOtpScreen(
        appointmentId = route.appointmentId,
        onBack = { navController.popBackStack() },
        onStartSuccess = {
          navController.popBackStack()
          navController.popBackStack()
        },
    )
  }

  composable<WorkerRequests> {
    WorkerRequestsScreen(
        workerId = currentWorkerId,
        onOpenRequestDetail = { appointmentId ->
          navController.navigate(WorkerRequestDetail(bookingId = appointmentId))
        },
        onOpenPaymentDetail = { appointmentId ->
          navController.navigate(WorkerPaymentDetail(bookingId = appointmentId))
        },
    )
  }

  composable<WorkerRequestDetail> { backStackEntry ->
    val route = backStackEntry.toRoute<WorkerRequestDetail>()
    WorkerRequestDetailScreen(
        bookingId = route.bookingId,
        onBack = { navController.popBackStack() },
    )
  }

  composable<WorkerPaymentDetail> { backStackEntry ->
    val route = backStackEntry.toRoute<WorkerPaymentDetail>()
    WorkerPaymentDetailScreen(
        bookingId = route.bookingId,
        onBack = { navController.popBackStack() },
    )
  }

    composable<WorkerAlerts> {
        WorkerAlertsScreen(
            workerId = currentWorkerId,
            onBack = { navController.popBackStack() },
            onOpenRequestDetail = { appointmentId ->
                navController.navigate(WorkerRequestDetail(bookingId = appointmentId))
            },
            onOpenPaymentDetail = { appointmentId ->
                navController.navigate(WorkerPaymentDetail(bookingId = appointmentId))
            },
            onOpenRequests = { navController.navigateSingleTop(WorkerRequests) },
            onOpenDailyAppointments = { workerId ->
                navController.navigateSingleTop(WorkerDailyAppointments(workerId = workerId))
            },
            onOpenAppointmentDetail = { appointmentId ->
                openWorkerAppointmentDetailFromAlert(
                    appointmentId = appointmentId,
                    workerId = currentWorkerId,
                    selectedAppointment = selectedAppointment,
                    monthlyCalendarViewModel = monthlyCalendarViewModel,
                    navController = navController,
                )
            },
            onOpenProfessionalProfile = { workerId ->
                navController.navigate(ProfessionalProfile(workerId = workerId))
            },
        )
    }

  composable<WorkerPortfolio> {
    FeaturePlaceholder(
        title = "Portafolio",
        subtitle = "Aquí irá el portafolio de trabajos del trabajador.",
    )
  }

  composable<WorkerServices> {
    FeaturePlaceholder(
        title = "Servicios del trabajador",
        subtitle = "Aquí irá el registro y edición de servicios del trabajador.",
    )
  }

  composable<WorkerSchedule> {
    FeaturePlaceholder(
        title = "Horario del trabajador",
        subtitle = "Aquí irá la configuración de días laborales, zonas y disponibilidad.",
    )
  }

  composable<WorkerCategories> {
    WorkerCategoriesScreen(workerId = currentWorkerId, onBack = { navController.popBackStack() })
  }

  composable<WorkerToClientReview> { backStackEntry ->
    val route = backStackEntry.toRoute<WorkerToClientReview>()

    WorkerToClientReviewScreen(
        appointmentId = route.appointmentId,
        onBack = { navController.popBackStack() },
        onSubmitSuccess = { navController.popBackStack() },
    )
  }
}

private fun openWorkerAppointmentDetailFromAlert(
    appointmentId: String,
    workerId: String,
    selectedAppointment: Appointment?,
    monthlyCalendarViewModel: MonthlyCalendarViewModel,
    navController: NavHostController,
) {
    val currentSelected = selectedAppointment?.takeIf { it.id == appointmentId }

    if (currentSelected != null) {
        monthlyCalendarViewModel.selectAppointment(currentSelected)
        navController.navigateSingleTop(WorkerAppointmentDetail)
    } else {
        navController.navigateSingleTop(WorkerDailyAppointments(workerId = workerId))
    }
}