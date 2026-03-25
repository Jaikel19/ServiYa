package com.example.seviya.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Home
import compose.icons.tablericons.Login
import compose.icons.tablericons.UserPlus

private object GuestNavColors {
    val Primary = Color(0xFF004AAD)
    val Secondary = Color(0xFFEF4444)
    val Border = Color(0xFFEAEAF2)
    val Inactive = Color(0xFF94A3B8)
}

enum class GuestTab {
    HOME, LOGIN, REGISTER
}

@Composable
fun GuestBottomBar(
    currentTab: GuestTab,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.92f),
        tonalElevation = 10f.dp,
        modifier = Modifier.border(1.dp, GuestNavColors.Border)
    ) {
        GuestBottomItem(
            label = "INICIO",
            icon = TablerIcons.Home,
            active = currentTab == GuestTab.HOME,
            activeColor = GuestNavColors.Primary,
            onClick = onHome
        )

        GuestBottomItem(
            label = "INGRESAR",
            icon = TablerIcons.Login,
            active = currentTab == GuestTab.LOGIN,
            activeColor = GuestNavColors.Primary,
            onClick = onLogin
        )

        GuestBottomItem(
            label = "REGISTRAR",
            icon = TablerIcons.UserPlus,
            active = currentTab == GuestTab.REGISTER,
            activeColor = GuestNavColors.Secondary,
            onClick = onRegister
        )
    }
}

@Composable
private fun RowScope.GuestBottomItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val color = if (active) activeColor else GuestNavColors.Inactive

    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
        },
        label = {
            Text(
                text = label,
                color = color,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}