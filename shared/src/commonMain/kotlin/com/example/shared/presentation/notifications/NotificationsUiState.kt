package com.example.shared.presentation.notifications

import com.example.shared.domain.entity.AppNotification

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<AppNotification> = emptyList(),
    val errorMessage: String? = null,
    val isMarkingAllAsRead: Boolean = false,
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }
}