package com.example.seviya.feature.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BlueGrayText
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.CardSurface
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.TextPrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.AppNotification
import com.example.shared.presentation.notifications.NotificationsUiState
import com.example.shared.presentation.notifications.NotificationsViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import org.koin.compose.viewmodel.koinViewModel

private enum class NotificationFilter {
    ALL,
    UNREAD,
    READ,
}

@Composable
fun NotificationCenterScreen(
    userId: String,
    title: String,
    emptyTitle: String,
    emptySubtitle: String,
    onBack: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
) {
    val viewModel: NotificationsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) { viewModel.start(userId) }

    NotificationCenterContent(
        uiState = uiState,
        title = title,
        emptyTitle = emptyTitle,
        emptySubtitle = emptySubtitle,
        onBack = onBack,
        onNotificationClick = { notification ->
            if (!notification.isRead) {
                viewModel.markAsRead(notification.id)
            }
            onNotificationClick(notification)
        },
        onMarkOneAsRead = { notificationId -> viewModel.markAsRead(notificationId) },
        onMarkOneAsUnread = { notificationId -> viewModel.markAsUnread(notificationId) },
        onMarkAllAsRead = { viewModel.markAllAsRead() },
    )
}

@Composable
private fun NotificationCenterContent(
    uiState: NotificationsUiState,
    title: String,
    emptyTitle: String,
    emptySubtitle: String,
    onBack: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
    onMarkOneAsRead: (String) -> Unit,
    onMarkOneAsUnread: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
) {
    var selectedFilter by rememberSaveable { mutableStateOf(NotificationFilter.ALL.name) }

    val filter = NotificationFilter.valueOf(selectedFilter)

    val notifications =
        when (filter) {
            NotificationFilter.ALL -> uiState.notifications
            NotificationFilter.UNREAD -> uiState.notifications.filter { !it.isRead }
            NotificationFilter.READ -> uiState.notifications.filter { it.isRead }
        }

    Scaffold(
        containerColor = AppBackgroundAlt,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NotificationCenterHeader(title = title, onBack = onBack)

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Tus alertas recientes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                        )
                        Text(
                            text = "No leídas: ${uiState.unreadCount} • Se muestran las 30 más recientes",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }

                    TextButton(
                        onClick = onMarkAllAsRead,
                        enabled = uiState.unreadCount > 0 && !uiState.isMarkingAllAsRead,
                    ) {
                        if (uiState.isMarkingAllAsRead) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp),
                                color = BrandBlue,
                            )
                        } else {
                            Text("Marcar todas")
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NotificationFilterChip(
                        label = "Todas",
                        selected = filter == NotificationFilter.ALL,
                        onClick = { selectedFilter = NotificationFilter.ALL.name },
                    )
                    NotificationFilterChip(
                        label = "No leídas",
                        selected = filter == NotificationFilter.UNREAD,
                        onClick = { selectedFilter = NotificationFilter.UNREAD.name },
                    )
                    NotificationFilterChip(
                        label = "Leídas",
                        selected = filter == NotificationFilter.READ,
                        onClick = { selectedFilter = NotificationFilter.READ.name },
                    )
                }

                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BrandBlue)
                        }
                    }

                    notifications.isEmpty() -> {
                        EmptyNotificationsState(
                            title = emptyTitle,
                            subtitle = emptySubtitle,
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 18.dp),
                        ) {
                            items(notifications, key = { it.id }) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onOpen = { onNotificationClick(notification) },
                                    onMarkAsRead = { onMarkOneAsRead(notification.id) },
                                    onMarkAsUnread = { onMarkOneAsUnread(notification.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCenterHeader(
    title: String,
    onBack: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "notification_center_header")

    val leftBadgeScale by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "left_badge_scale",
    )

    val rightBadgeScale by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "right_badge_scale",
    )

    val bubbleOffsetLarge by
    infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bubble_offset_large",
    )

    val bubbleOffsetSmall by
    infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bubble_offset_small",
    )

    val bubbleScaleLarge by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bubble_scale_large",
    )

    val bubbleScaleSmall by
    infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bubble_scale_small",
    )

    val shimmerOffset by
    infiniteTransition.animateFloat(
        initialValue = -260f,
        targetValue = 620f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(3200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmer_offset",
    )

    val entranceVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { entranceVisible.value = true }

    AnimatedVisibility(
        visible = entranceVisible.value,
        enter =
            fadeIn(animationSpec = tween(500)) +
                    slideInVertically(
                        initialOffsetY = { -it / 3 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing),
                    ),
    ) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                    .background(BrandBlue)
        ) {
            Box(
                modifier =
                    Modifier.matchParentSize()
                        .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
            ) {
                Box(
                    modifier =
                        Modifier.fillMaxHeight()
                            .width(140.dp)
                            .offset(x = shimmerOffset.dp)
                            .graphicsLayer {
                                rotationZ = -18f
                                alpha = 0.16f
                            }
                            .background(
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            White.copy(alpha = 0.45f),
                                            Color.Transparent,
                                        )
                                )
                            )
                )
            }

            Row(
                modifier =
                    Modifier.align(Alignment.TopStart)
                        .padding(start = 20.dp, top = 42.dp)
                        .graphicsLayer {
                            scaleX = leftBadgeScale
                            scaleY = leftBadgeScale
                        }
                        .clip(RoundedCornerShape(999.dp))
                        .background(White.copy(alpha = 0.14f))
                        .border(
                            width = 1.dp,
                            color = White.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(999.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val arrowFloat by
                infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = -2f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "arrow_float",
                )

                Box(
                    modifier =
                        Modifier.size(28.dp)
                            .graphicsLayer { translationY = arrowFloat }
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.14f))
                            .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = TablerIcons.ArrowLeft,
                        contentDescription = "Volver",
                        tint = White,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Servi",
                        color = White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Ya",
                        color = BrandRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            Text(
                text = title.uppercase(),
                modifier =
                    Modifier.align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 42.dp)
                        .graphicsLayer {
                            scaleX = rightBadgeScale
                            scaleY = rightBadgeScale
                        }
                        .clip(RoundedCornerShape(999.dp))
                        .background(White.copy(alpha = 0.14f))
                        .border(
                            width = 1.dp,
                            color = White.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(999.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Box(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)
            ) {
                Box(
                    modifier =
                        Modifier.size(82.dp)
                            .graphicsLayer {
                                translationY = bubbleOffsetLarge
                                scaleX = bubbleScaleLarge
                                scaleY = bubbleScaleLarge
                            }
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.08f))
                )

                Box(
                    modifier =
                        Modifier.size(46.dp)
                            .align(Alignment.BottomStart)
                            .graphicsLayer {
                                translationY = bubbleOffsetSmall
                                scaleX = bubbleScaleSmall
                                scaleY = bubbleScaleSmall
                            }
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.10f))
                )
            }
        }
    }
}

@Composable
private fun NotificationFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) BrandBlue else White,
        border = BorderStroke(1.dp, if (selected) BrandBlue else BorderSoft),
        modifier = Modifier.clickable { onClick() },
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = if (selected) White else TextSecondary,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

private data class NotificationVisualStyle(
    val icon: ImageVector,
    val color: Color,
    val iconColor: Color,
)

private fun notificationVisualStyle(type: String): NotificationVisualStyle =
    when (type) {
        "NEW_REQUEST_RECEIVED" -> NotificationVisualStyle(
            icon = TablerIcons.UserPlus,
            color = Color(0xFF8DBED7),
            iconColor = Color(0xFF1476AA),
        )

        "REQUEST_ACCEPTED" -> NotificationVisualStyle(
            icon = TablerIcons.UserCheck,
            color = Color(0xFFA8D27E),
            iconColor = Color(0xFF63AC1B),
        )

        "REQUEST_REJECTED" -> NotificationVisualStyle(
            icon = TablerIcons.X,
            color = Color(0xFFF4B4B4),
            iconColor = Color(0xFFD92D20),
        )

        "PAYMENT_PENDING" -> NotificationVisualStyle(
            icon = TablerIcons.CreditCard,
            color = Color(0xFFF8D58B),
            iconColor = Color(0xFFE39B00),
        )

        "PAYMENT_RECEIPT_UPLOADED" -> NotificationVisualStyle(
            icon = TablerIcons.Receipt,
            color = Color(0xFFA7D47B),
            iconColor = Color(0xFF63AC1B),
        )

        "PAYMENT_ISSUE" -> NotificationVisualStyle(
            icon = TablerIcons.AlertCircle,
            color = Color(0xFFF7B2B2),
            iconColor = Color(0xFFD92D20),
        )

        "PAYMENT_VERIFIED" -> NotificationVisualStyle(
            icon = TablerIcons.ReportMoney,
            color = Color(0xFFBEE7C8),
            iconColor = Color(0xFF1B8A4B),
        )

        "APPOINTMENT_CONFIRMED" -> NotificationVisualStyle(
            icon = TablerIcons.CalendarEvent,
            color = Color(0xFF92DFEC),
            iconColor = Color(0xFF20BFDC),
        )

        "APPOINTMENT_REMINDER_24H",
        "APPOINTMENT_REMINDER_2H" -> NotificationVisualStyle(
            icon = TablerIcons.Bell,
            color = Color(0xFFE4D4FF),
            iconColor = Color(0xFF8F52FA),
        )

        "APPOINTMENT_STARTED" -> NotificationVisualStyle(
            icon = TablerIcons.PlayerPlay,
            color = Color(0xFFCDECCF),
            iconColor = Color(0xFF239B56),
        )

        "APPOINTMENT_COMPLETED" -> NotificationVisualStyle(
            icon = TablerIcons.CircleCheck,
            color = Color(0xFF83AEDC),
            iconColor = Color(0xFF194E87),
        )

        "APPOINTMENT_CANCELLED" -> NotificationVisualStyle(
            icon = TablerIcons.CalendarStats,
            color = Color(0xFFE89999),
            iconColor = Color(0xFFB83232),
        )

        "REVIEW_PENDING_CLIENT",
        "REVIEW_PENDING_WORKER" -> NotificationVisualStyle(
            icon = TablerIcons.MessageCircle,
            color = Color(0xFFFAB3CD),
            iconColor = Color(0xFFD63384),
        )

        "REVIEW_RECEIVED" -> NotificationVisualStyle(
            icon = TablerIcons.Star,
            color = Color(0xFFFAB0CF),
            iconColor = Color(0xFFD63384),
        )

        "REVIEW_SUBMITTED_SUCCESS" -> NotificationVisualStyle(
            icon = TablerIcons.MessageCircle,
            color = Color(0xFFE2B198),
            iconColor = Color(0xFFC25418),
        )

        else -> NotificationVisualStyle(
            icon = TablerIcons.AlertCircle,
            color = Color(0xFFDCE7F7),
            iconColor = BrandBlue,
        )
    }

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onOpen: () -> Unit,
    onMarkAsRead: () -> Unit,
    onMarkAsUnread: () -> Unit,
) {
    val style = notificationVisualStyle(notification.type)
    val timeLabel = formatNotificationDate(notification.createdAt)

    val background =
        if (notification.isRead) {
            CardSurface
        } else {
            style.color.copy(alpha = 0.12f)
        }

    val borderColor =
        if (notification.isRead) {
            BorderSoft
        } else {
            style.color.copy(alpha = 0.45f)
        }

    val iconContainerColor =
        if (notification.isRead) {
            White
        } else {
            style.color.copy(alpha = 0.16f)
        }

    val iconTint =
        if (notification.isRead) {
            BrandBlue
        } else {
            style.iconColor
        }

    val dotColor =
        if (notification.isRead) {
            BrandBlue
        } else {
            style.color
        }

    val actionColor =
        if (notification.isRead) {
            BrandBlue
        } else {
            style.color
        }

    Surface(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { onOpen() },
        shape = RoundedCornerShape(20.dp),
        color = background,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier.size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(iconContainerColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = style.icon,
                            contentDescription = null,
                            tint = iconTint,
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (!notification.isRead) {
                                Box(
                                    modifier =
                                        Modifier.size(8.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                )
                            }

                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = notification.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = BlueGrayText,
                )

                if (!notification.isRead) {
                    TextButton(onClick = onMarkAsRead) {
                        Text(
                            text = "Marcar leída",
                            color = actionColor,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )
                    }
                } else {
                    TextButton(onClick = onMarkAsUnread) {
                        Text(
                            text = "Marcar no leída",
                            color = BrandBlue,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState(
    title: String,
    subtitle: String,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier.size(92.dp)
                        .clip(CircleShape)
                        .background(SoftBlueSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TablerIcons.Bell,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(42.dp),
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}

private fun formatNotificationDate(raw: String): String {
    if (raw.isBlank()) return "Fecha no disponible"

    return when {
        raw.contains("T") -> {
            val date = raw.substringBefore("T")
            val time = raw.substringAfter("T").take(5)
            "$date • $time"
        }
        else -> raw
    }
}