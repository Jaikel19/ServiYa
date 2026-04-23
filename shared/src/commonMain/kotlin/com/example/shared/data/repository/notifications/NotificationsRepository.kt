package com.example.shared.data.repository.notifications

import com.example.shared.data.remote.notifications.IRemoteNotificationsDataSource
import com.example.shared.domain.entity.AppNotification
import kotlinx.coroutines.flow.Flow

class NotificationsRepository(
    private val remoteDataSource: IRemoteNotificationsDataSource
) : INotificationsRepository {

    override fun observeNotifications(userId: String, limit: Int): Flow<List<AppNotification>> {
        return remoteDataSource.observeNotifications(userId, limit)
    }

    override suspend fun markAsRead(userId: String, notificationId: String) {
        remoteDataSource.markAsRead(userId, notificationId)
    }

    override suspend fun markAllAsRead(userId: String, notificationIds: List<String>) {
        remoteDataSource.markAllAsRead(userId, notificationIds)
    }

    override suspend fun markAsUnread(userId: String, notificationId: String) {
        remoteDataSource.markAsUnread(userId, notificationId)
    }

    override suspend fun createNotification(userId: String, notification: AppNotification): String {
        return remoteDataSource.createNotification(userId, notification)
    }
}