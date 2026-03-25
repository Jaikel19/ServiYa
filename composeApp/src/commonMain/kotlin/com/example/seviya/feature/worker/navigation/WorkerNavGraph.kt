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
import com.example.seviya.feature.shared.DailyAgendaScreen
import com.example.seviya.feature.shared.MonthlyCalendarScreen
import com.example.seviya.feature.shared.WeeklyAgendaScreen
import com.example.seviya.feature.worker.WorkerAppointmentDetailScreen
import com.example.seviya.feature.worker.WorkerCategoriesScreen
import com.example.seviya.feature.worker.WorkerDailyAppointmentsScreen
import com.example.seviya.feature.worker.WorkerDashboardScreen
import com.example.seviya.feature.worker.WorkerPaymentDetailScreen
import com.example.seviya.feature.worker.WorkerRequestDetailScreen
import com.example.seviya.feature.worker.WorkerStartAppointmentOtpScreen
import com.example.seviya.feature.worker.WorkerToClientReviewScreen
import com.example.seviya.feature.worker.WorkerRequestsScreen
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.calendar.CalendarUserRole
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel

fun NavGraphBuilder.workerNavGraph(
    navController: NavHostController,
    currentWorkerId: String,
    onCurrentWorkerTabChange: (WorkerTab) -> Unit,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onWorkerMenuExpandedChange: (Boolean) -> Unit,
    monthlyCalendarViewModel: MonthlyCalendarViewModel,
    selectedAppointment: Appointment?
) {
    composable<WorkerDashboard> {
        WorkerDashboardScreen(
            workerId = currentWorkerId,
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

        WorkerDailyAppointmentsScreen(
            workerId = route.workerId,
            onBack = { navController.popBackStack() },
            onOpenMaps = { _, _, _ -> }
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
            }
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
        WorkerStartAppointmentOtpScreen(
            appointmentId = route.appointmentId,
            onBack = { navController.popBackStack() },
            onStartSuccess = {
                navController.popBackStack()
                navController.popBackStack()
            }
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
            }
        )
    }

    composable<WorkerRequestDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerRequestDetail>()
        WorkerRequestDetailScreen(
            bookingId = route.bookingId,
            onBack = { navController.popBackStack() }
        )
    }

    composable<WorkerPaymentDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerPaymentDetail>()
        WorkerPaymentDetailScreen(
            bookingId = route.bookingId,
            onBack = { navController.popBackStack() }
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
        WorkerCategoriesScreen(
            workerId = currentWorkerId,
            onBack = { navController.popBackStack() }
        )
    }

    composable<WorkerToClientReview> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkerToClientReview>()

        WorkerToClientReviewScreen(
            appointmentId = route.appointmentId,
            onBack = { navController.popBackStack() },
            onSubmitSuccess = {
                navController.popBackStack()
            }
        )
    }
}