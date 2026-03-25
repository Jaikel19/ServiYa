package com.example.seviya.feature.categories.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import com.example.seviya.core.designsystem.theme.MenuDivider
import com.example.seviya.core.designsystem.theme.MenuItemSubtitle
import com.example.seviya.core.designsystem.theme.MenuItemTitle
import com.example.seviya.core.designsystem.theme.MenuSurface
import com.example.seviya.core.designsystem.theme.MenuTitle
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.White
import compose.icons.TablerIcons
import compose.icons.tablericons.Adjustments
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.ChevronLeft
import compose.icons.tablericons.Dashboard
import compose.icons.tablericons.Message
import compose.icons.tablericons.Settings
import compose.icons.tablericons.User

@Composable
internal fun CategoriesMenuOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onGoAgenda: () -> Unit,
    onGoProfile: () -> Unit,
    onGoConfiguration: () -> Unit,
    onGoMessages: () -> Unit,
    onGoDashboard: () -> Unit,
    onGoSettings: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(220)) +
            slideInVertically(
                animationSpec = tween(280),
                initialOffsetY = { it / 10 }
            ),
        exit = fadeOut(animationSpec = tween(180)) +
            slideOutVertically(
                animationSpec = tween(220),
                targetOffsetY = { it / 12 }
            )
    ) {
        CategoriesFullScreenMenu(
            onDismiss = onDismiss,
            onGoAgenda = onGoAgenda,
            onGoProfile = onGoProfile,
            onGoConfiguration = onGoConfiguration,
            onGoMessages = onGoMessages,
            onGoDashboard = onGoDashboard,
            onGoSettings = onGoSettings
        )
    }
}

@Composable
private fun CategoriesFullScreenMenu(
    onDismiss: () -> Unit,
    onGoAgenda: () -> Unit,
    onGoProfile: () -> Unit,
    onGoConfiguration: () -> Unit,
    onGoMessages: () -> Unit,
    onGoDashboard: () -> Unit,
    onGoSettings: () -> Unit
) {
    val menuItems = listOf(
        CategoriesMenuItem(
            icon = TablerIcons.CalendarEvent,
            iconColor = Color(0xFF5FA8D3),
            title = "Agenda",
            subtitle = "Citas, disponibilidad y calendario",
            onClick = onGoAgenda
        ),
        CategoriesMenuItem(
            icon = TablerIcons.User,
            iconColor = Color(0xFF8E7CC3),
            title = "Perfil",
            subtitle = "Datos personales e información pública",
            onClick = onGoProfile
        ),
        CategoriesMenuItem(
            icon = TablerIcons.Message,
            iconColor = Color(0xFF67B99A),
            title = "Mensajes",
            subtitle = "Chats y conversaciones con clientes",
            onClick = onGoMessages
        ),
        CategoriesMenuItem(
            icon = TablerIcons.Dashboard,
            iconColor = Color(0xFFE29C7A),
            title = "Dashboard",
            subtitle = "Resumen general, métricas y actividad",
            onClick = onGoDashboard
        ),
        CategoriesMenuItem(
            icon = TablerIcons.Settings,
            iconColor = Color(0xFF9BB85D),
            title = "Configuración",
            subtitle = "Opciones principales de la aplicación",
            onClick = onGoConfiguration
        ),
        CategoriesMenuItem(
            icon = TablerIcons.Adjustments,
            iconColor = Color(0xFFD7B85A),
            title = "Ajustes",
            subtitle = "Preferencias, personalización y control",
            onClick = onGoSettings
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MenuSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MenuSurface)
                    .padding(start = 10.dp, end = 18.dp, top = 10.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.ChevronLeft,
                        contentDescription = null,
                        tint = TextBluePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Menú",
                    color = MenuTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        letterSpacing = 0.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(top = 6.dp, bottom = 20.dp)
            ) {
                menuItems.forEachIndexed { index, item ->
                    SettingsStyleMenuItem(
                        icon = item.icon,
                        iconColor = item.iconColor,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = item.onClick
                    )

                    if (index < menuItems.lastIndex) {
                        SettingsListDivider()
                        MenuSectionSpacer()
                    }
                }
            }
        }
    }
}

private data class CategoriesMenuItem(
    val icon: ImageVector,
    val iconColor: Color,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Composable
private fun SettingsStyleMenuItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(54.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = MenuItemTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    letterSpacing = 0.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = MenuItemSubtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
            )
        }
    }
}

@Composable
private fun SettingsListDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 72.dp, end = 18.dp)
            .height(1.dp)
            .background(MenuDivider)
    )
}

@Composable
private fun MenuSectionSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
    )
}