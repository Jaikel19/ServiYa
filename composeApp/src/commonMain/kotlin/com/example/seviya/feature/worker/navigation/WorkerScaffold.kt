package com.example.seviya.feature.worker.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.example.seviya.app.navigateSingleTop
import com.example.seviya.core.designsystem.components.FullScreenMenu
import com.example.seviya.core.designsystem.components.MenuOption
import com.example.seviya.core.designsystem.components.WorkerBottomBar
import com.example.seviya.core.designsystem.components.WorkerTab
import com.example.seviya.core.navigation.TravelTimeConfig
import com.example.seviya.core.navigation.WorkerAgenda
import com.example.seviya.core.navigation.WorkerAlerts
import com.example.seviya.core.navigation.WorkerAppointmentDetail
import com.example.seviya.core.navigation.WorkerCategories
import com.example.seviya.core.navigation.WorkerConfiguration
import com.example.seviya.core.navigation.WorkerDailyAppointments
import com.example.seviya.core.navigation.WorkerDashboard
import com.example.seviya.core.navigation.WorkerMessages
import com.example.seviya.core.navigation.WorkerPortfolio
import com.example.seviya.core.navigation.WorkerProfile
import com.example.seviya.core.navigation.WorkerReports
import com.example.seviya.core.navigation.WorkerRequests
import com.example.seviya.core.navigation.WorkerSchedule
import com.example.seviya.core.navigation.WorkerServices
import com.example.seviya.core.navigation.WorkerSettings
import compose.icons.TablerIcons
import compose.icons.tablericons.Adjustments
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.ChartBar
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Logout
import compose.icons.tablericons.Message
import compose.icons.tablericons.Photo
import compose.icons.tablericons.Settings
import compose.icons.tablericons.User

fun NavDestination?.isWorkerScaffoldDestination(): Boolean {
    val destination = this ?: return false

    return destination.hasRoute<WorkerDashboard>() ||
            destination.hasRoute<WorkerAgenda>() ||
            destination.hasRoute<WorkerRequests>() ||
            destination.hasRoute<WorkerAlerts>() ||
            destination.hasRoute<WorkerPortfolio>() ||
            destination.hasRoute<WorkerServices>() ||
            destination.hasRoute<WorkerSchedule>() ||
            destination.hasRoute<WorkerCategories>() ||
            destination.hasRoute<TravelTimeConfig>() ||
            destination.hasRoute<WorkerAppointmentDetail>() ||
            destination.hasRoute<WorkerDailyAppointments>()
}

@Composable
fun WorkerScaffold(
    navController: NavHostController,
    currentWorkerId: String,
    currentTab: WorkerTab,
    menuExpanded: Boolean,
    unreadAlertsCount: Int,
    onCurrentTabChange: (WorkerTab) -> Unit,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onWorkerMenuExpandedChange: (Boolean) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                WorkerFeatureBottomBar(
                    navController = navController,
                    currentTab = currentTab,
                    menuExpanded = menuExpanded,
                    unreadAlertsCount = unreadAlertsCount,
                    onCurrentTabChange = onCurrentTabChange,
                    onClientMenuExpandedChange = onClientMenuExpandedChange,
                    onWorkerMenuExpandedChange = onWorkerMenuExpandedChange,
                )
            },
        ) { innerPadding ->
            content(innerPadding)
        }

        WorkerFeatureMenu(
            navController = navController,
            currentWorkerId = currentWorkerId,
            menuExpanded = menuExpanded,
            onWorkerMenuExpandedChange = onWorkerMenuExpandedChange,
            onLogout = onLogout,
        )
    }
}

@Composable
fun WorkerFeatureBottomBar(
    navController: NavHostController,
    currentTab: WorkerTab,
    menuExpanded: Boolean,
    unreadAlertsCount: Int,
    onCurrentTabChange: (WorkerTab) -> Unit,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onWorkerMenuExpandedChange: (Boolean) -> Unit,
) {
    WorkerBottomBar(
        currentTab = currentTab,
        menuActive = menuExpanded,
        unreadAlertsCount = unreadAlertsCount,
        onGoDashboard = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(false)
            onCurrentTabChange(WorkerTab.DASHBOARD)
            navController.navigateSingleTop(WorkerDashboard)
        },
        onGoAgenda = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(false)
            onCurrentTabChange(WorkerTab.AGENDA)
            navController.navigateSingleTop(WorkerAgenda)
        },
        onGoRequests = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(false)
            onCurrentTabChange(WorkerTab.REQUESTS)
            navController.navigateSingleTop(WorkerRequests)
        },
        onGoAlerts = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(false)
            onCurrentTabChange(WorkerTab.ALERTS)
            navController.navigateSingleTop(WorkerAlerts)
        },
        onGoMenu = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(!menuExpanded)
        },
    )
}

@Composable
fun WorkerFeatureMenu(
    navController: NavHostController,
    currentWorkerId: String,
    menuExpanded: Boolean,
    onWorkerMenuExpandedChange: (Boolean) -> Unit,
    onLogout: () -> Unit,
) {
    AnimatedVisibility(
        visible = menuExpanded,
        enter =
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                    fadeIn(animationSpec = tween(220)) +
                    scaleIn(initialScale = 0.98f, animationSpec = tween(300)),
        exit =
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(260)) +
                    fadeOut(animationSpec = tween(180)) +
                    scaleOut(targetScale = 0.98f, animationSpec = tween(260)),
    ) {
        FullScreenMenu(
            title = "Menú trabajador",
            options =
                workerMenuOptions(
                    navController = navController,
                    currentWorkerId = currentWorkerId,
                    closeMenu = { onWorkerMenuExpandedChange(false) },
                    onLogout = onLogout,
                ),
            onDismiss = { onWorkerMenuExpandedChange(false) },
        )
    }
}

private fun workerMenuOptions(
    navController: NavHostController,
    currentWorkerId: String,
    closeMenu: () -> Unit,
    onLogout: () -> Unit,
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
            },
        ),
        MenuOption(
            title = "Agenda Diaria",
            subtitle = "Vista diaria en lista o mapa de las citas",
            icon = TablerIcons.CalendarEvent,
            iconColor = Color(0xFF4F8CFF),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerDailyAppointments(workerId = currentWorkerId))
            },
        ),
        MenuOption(
            title = "Mis Servicios",
            subtitle = "Registrar y modificar servicios ofrecidos",
            icon = TablerIcons.Briefcase,
            iconColor = Color(0xFF4CB5AE),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerServices)
            },
        ),
        MenuOption(
            title = "Mis Categorías",
            subtitle = "Categorías de servicios que ofrezco",
            icon = TablerIcons.Adjustments,
            iconColor = Color(0xFF3B82F6),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerCategories)
            },
        ),
        MenuOption(
            title = "Tiempo de Traslado",
            subtitle = "Tiempo de margen entre servicios",
            icon = TablerIcons.Clock,
            iconColor = Color(0xFFE2B100),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(TravelTimeConfig)
            },
        ),
        MenuOption(
            title = "Portafolio",
            subtitle = "Trabajos realizados y evidencia visual",
            icon = TablerIcons.Photo,
            iconColor = Color(0xFFC96AE6),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerPortfolio)
            },
        ),
        MenuOption(
            title = "Horario",
            subtitle = "Días laborales, disponibilidad y zonas",
            icon = TablerIcons.Clock,
            iconColor = Color(0xFFE2B100),
            onClick = {
                closeMenu()
                navController.navigateSingleTop(WorkerSchedule)
            },
        ),
        MenuOption(
            title = "Cerrar sesión",
            subtitle = "Salir de la cuenta y volver al inicio",
            icon = TablerIcons.Logout,
            iconColor = Color(0xFFEF4444),
            onClick = onLogout,
        ),
    )
}