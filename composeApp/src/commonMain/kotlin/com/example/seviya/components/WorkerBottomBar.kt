package com.example.seviya.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.*

@Composable
fun WorkerBottomBar(
    currentTab: WorkerTab,
    menuActive: Boolean,
    onGoDashboard: () -> Unit,
    onGoAgenda: () -> Unit,
    onGoRequests: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoMenu: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        WorkerBottomItem("Dashboard", TablerIcons.Dashboard, currentTab == WorkerTab.DASHBOARD, onGoDashboard)
        WorkerBottomItem("Agenda", TablerIcons.CalendarEvent, currentTab == WorkerTab.AGENDA, onGoAgenda)
        WorkerBottomItem("Solicitudes", TablerIcons.FileText, currentTab == WorkerTab.REQUESTS, onGoRequests)
        WorkerBottomItem("Alertas", TablerIcons.Bell, currentTab == WorkerTab.ALERTS, onGoAlerts)
        WorkerBottomItem("Menú", TablerIcons.Menu2, menuActive, onGoMenu)
    }
}

enum class WorkerTab {
    DASHBOARD, AGENDA, REQUESTS, ALERTS
}

@Composable
private fun RowScope.WorkerBottomItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val color = if (active) Color(0xFF004AAD) else Color(0xFF98A2B3)
    val bg = if (active) Color(0xFFEAF1FB) else Color.Transparent

    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier
                    .background(bg, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color
                )
            }
        },
        label = {
            Text(
                text = label,
                color = color,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium
                )
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}