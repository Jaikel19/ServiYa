package com.example.seviya.feature.client.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.example.seviya.app.navigateSingleTop
import com.example.seviya.core.designsystem.components.ClientBottomBar
import com.example.seviya.core.designsystem.components.ClientTab
import com.example.seviya.core.designsystem.components.FullScreenMenu
import com.example.seviya.core.designsystem.components.MenuOption
import com.example.seviya.core.navigation.CategoriesCatalog
import com.example.seviya.core.navigation.ClientAgenda
import com.example.seviya.core.navigation.ClientAlerts
import com.example.seviya.core.navigation.ClientConfiguration
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
import com.example.seviya.core.navigation.ProfessionalProfile
import com.example.seviya.core.navigation.RequestAppointment
import com.example.seviya.core.navigation.WorkersList
import compose.icons.TablerIcons
import compose.icons.tablericons.Adjustments
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.Dashboard
import compose.icons.tablericons.Logout
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Message
import compose.icons.tablericons.Settings
import compose.icons.tablericons.User

fun NavDestination?.isClientScaffoldDestination(): Boolean {
    val destination = this ?: return false

    return destination.hasRoute<ClientHome>() ||
        destination.hasRoute<CategoriesCatalog>() ||
        destination.hasRoute<WorkersList>() ||
        destination.hasRoute<ProfessionalProfile>() ||
        destination.hasRoute<ClientMap>() ||
        destination.hasRoute<ClientSearch>() ||
        destination.hasRoute<ClientAlerts>() ||
        destination.hasRoute<ClientDashboard>() ||
        destination.hasRoute<ClientAgenda>() ||
        destination.hasRoute<ClientProfile>() ||
        destination.hasRoute<ClientMessages>() ||
        destination.hasRoute<ClientConfiguration>() ||
        destination.hasRoute<ClientSettings>() ||
        destination.hasRoute<ClientFavorites>() ||
        destination.hasRoute<RequestAppointment>() ||
        destination.hasRoute<ClientRequests>() ||
        destination.hasRoute<ClientPaymentUpload>() ||
        destination.hasRoute<ClientLocationCatalog>()
}

@Composable
fun ClientFeatureBottomBar(
    navController: NavHostController,
    currentTab: ClientTab,
    menuExpanded: Boolean,
    onCurrentTabChange: (ClientTab) -> Unit,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onWorkerMenuExpandedChange: (Boolean) -> Unit
) {
    ClientBottomBar(
        currentTab = currentTab,
        menuActive = menuExpanded,
        onGoServices = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(false)
            onCurrentTabChange(ClientTab.SERVICES)
            navController.navigateSingleTop(ClientHome)
        },
        onGoMap = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(false)
            onCurrentTabChange(ClientTab.MAP)
            navController.navigateSingleTop(ClientMap)
        },
        onGoSearch = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(false)
            onCurrentTabChange(ClientTab.SEARCH)
            navController.navigateSingleTop(ClientSearch)
        },
        onGoAlerts = {
            onClientMenuExpandedChange(false)
            onWorkerMenuExpandedChange(false)
            onCurrentTabChange(ClientTab.ALERTS)
            navController.navigateSingleTop(ClientAlerts)
        },
        onGoMenu = {
            onWorkerMenuExpandedChange(false)
            onClientMenuExpandedChange(!menuExpanded)
        }
    )
}

@Composable
fun ClientFeatureMenu(
    navController: NavHostController,
    menuExpanded: Boolean,
    onClientMenuExpandedChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    AnimatedVisibility(
        visible = menuExpanded,
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
                closeMenu = { onClientMenuExpandedChange(false) },
                onLogout = onLogout
            ),
            onDismiss = { onClientMenuExpandedChange(false) }
        )
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