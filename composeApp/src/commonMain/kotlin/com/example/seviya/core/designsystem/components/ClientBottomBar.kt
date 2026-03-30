package com.example.seviya.core.designsystem.components

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
fun ClientBottomBar(
    currentTab: ClientTab,
    menuActive: Boolean,
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
    ClientBottomItem("Dashboard", TablerIcons.Apps, currentTab == ClientTab.DASHBOARD, onGoDashboard)
    ClientBottomItem("Mapa", TablerIcons.Globe, currentTab == ClientTab.MAP, onGoMap)
    ClientBottomItem("Servicios", TablerIcons.Search, currentTab == ClientTab.SERVICES, onGoServices)
    ClientBottomItem("Alertas", TablerIcons.Bell, currentTab == ClientTab.ALERTS, onGoAlerts)
    ClientBottomItem("Menú", TablerIcons.Menu2, menuActive, onGoMenu)
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
) {
  val color = if (active) Color(0xFF004AAD) else Color(0xFF98A2B3)
  val bg = if (active) Color(0xFFEAF1FB) else Color.Transparent

  NavigationBarItem(
      selected = active,
      onClick = onClick,
      icon = {
        Box(
            modifier =
                Modifier.background(bg, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
          Icon(imageVector = icon, contentDescription = label, tint = color)
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
