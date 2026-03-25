package com.example.seviya.feature.worker.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.seviya.feature.worker.WorkerCategoriesRoute
import com.example.seviya.feature.worker.WorkerDailyAppointmentsScreen
import com.example.seviya.feature.worker.WorkerDashboardRoute
import com.example.seviya.feature.worker.WorkerPaymentDetailScreen
import com.example.seviya.feature.worker.WorkerRequestDetailScreen
import com.example.seviya.feature.worker.WorkerStartAppointmentOtpScreen
import com.example.seviya.feature.worker.WorkerToClientReviewScreen
import com.example.seviya.ui.WorkerRequestsScreen
import com.example.shared.domain.entity.Appointment
import com.example.shared.presentation.WorkerPaymentDetail.WorkerPaymentDetailViewModel
import com.example.shared.presentation.WorkerRequest.WorkerRequestsViewModel
import com.example.shared.presentation.WorkerRequestDetailViewModel.WorkerRequestDetailViewModel
import com.example.shared.presentation.calendar.CalendarUserRole
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.dailyAgenda.DailyAgendaViewModel
import com.example.shared.presentation.workerAppointmentDetail.WorkerAppointmentDetailViewModel
import com.example.shared.presentation.workerCategories.WorkerCategoriesViewModel
import com.example.shared.presentation.workerDailyAppointments.WorkerDailyAppointmentsViewModel
import com.example.shared.presentation.workerDashboard.WorkerDashboardViewModel
import com.example.shared.presentation.workerStartAppointmentOtp.WorkerStartAppointmentOtpViewModel
import com.example.shared.presentation.workerToClientReview.WorkerToClientReviewViewModel
import com.example.shared.utils.DateTimeUtils
import org.koin.compose.viewmodel.koinViewModel

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