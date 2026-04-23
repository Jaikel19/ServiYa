package com.example.seviya.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.*

@Composable
fun ClientBottomBar(
    currentTab: ClientTab,
    menuActive: Boolean,
    unreadAlertsCount: Int = 0,
    onGoServices: () -> Unit,
    onGoMap: () -> Unit,
    onGoDashboard: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoMenu: () -> Unit,
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        ClientBottomItem(
            label = "Dashboard",
            icon = TablerIcons.Apps,
            active = currentTab == ClientTab.DASHBOARD,
            onClick = onGoDashboard,
        )

        ClientBottomItem(
            label = "Mapa",
            icon = TablerIcons.Globe,
            active = currentTab == ClientTab.MAP,
            onClick = onGoMap,
        )

        ClientBottomItem(
            label = "Servicios",
            icon = TablerIcons.Search,
            active = currentTab == ClientTab.SERVICES,
            onClick = onGoServices,
        )

        ClientBottomItem(
            label = "Alertas",
            icon = TablerIcons.Bell,
            active = currentTab == ClientTab.ALERTS,
            badgeCount = unreadAlertsCount,
            onClick = onGoAlerts,
        )

        ClientBottomItem(
            label = "Menú",
            icon = TablerIcons.Menu2,
            active = menuActive,
            onClick = onGoMenu,
        )
    }
}

enum class ClientTab {
    SERVICES,
    MAP,
    DASHBOARD,
    ALERTS,
}

@Composable
private fun RowScope.ClientBottomItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    badgeCount: Int = 0,
) {
    val hasBadge = badgeCount > 0
    val color =
        when {
            active -> Color(0xFF004AAD)
            hasBadge -> Color(0xFF004AAD)
            else -> Color(0xFF98A2B3)
        }

    val bg = if (active) Color(0xFFEAF1FB) else Color.Transparent

    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier =
                        Modifier.background(bg, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = icon, contentDescription = label, tint = color)
                }

                if (hasBadge) {
                    AlertBadge(
                        count = badgeCount,
                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (0).dp),
                    )
                }
            }
        },
        label = {
            Text(
                text = label,
                color = color,
                style =
                    MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium
                    ),
            )
        },
        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
    )
}

@Composable
private fun AlertBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val text = if (count > 9) "9+" else count.toString()

    Box(
        modifier =
            modifier
                .size(24.dp)
                .background(Color(0xFFE5484D), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}