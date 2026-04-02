package com.example.shared.data.repository.notifications

import com.example.shared.domain.entity.AppNotification
import kotlinx.coroutines.flow.Flow

interface INotificationsRepository {
    fun observeNotifications(userId: String, limit: Int = 80): Flow<List<AppNotification>>

    suspend fun markAsRead(userId: String, notificationId: String)

    suspend fun markAllAsRead(userId: String, notificationIds: List<String>)
    suspend fun markAsUnread(userId: String, notificationId: String)

    suspend fun createNotification(userId: String, notification: AppNotification): String
}