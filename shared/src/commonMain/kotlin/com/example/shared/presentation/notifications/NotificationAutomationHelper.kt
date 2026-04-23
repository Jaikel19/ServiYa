package com.example.shared.presentation.notifications

import com.example.shared.data.repository.notifications.INotificationsRepository
import com.example.shared.domain.entity.AppNotification
import com.example.shared.utils.DateTimeUtils

suspend fun INotificationsRepository.pushNotification(
    userId: String,
    recipientRole: String,
    title: String,
    message: String,
    type: String,
    appointmentId: String? = null,
    deepLink: String,
    actorId: String? = null,
    id: String = "",
) {
    createNotification(
        userId = userId,
        notification =
            AppNotification(
                id = id,
                userId = userId,
                recipientRole = recipientRole,
                title = title,
                message = message,
                type = type,
                isRead = false,
                createdAt = DateTimeUtils.nowIsoMinute(),
                readAt = null,
                appointmentId = appointmentId,
                deepLink = deepLink,
                actorId = actorId,
            ),
    )
}