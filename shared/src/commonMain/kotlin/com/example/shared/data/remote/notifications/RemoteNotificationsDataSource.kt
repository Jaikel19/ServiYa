package com.example.shared.data.remote.notifications

import com.example.shared.domain.entity.AppNotification
import com.example.shared.utils.DateTimeUtils
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteNotificationsDataSource : IRemoteNotificationsDataSource {

    private val db = Firebase.firestore

    override fun observeNotifications(userId: String, limit: Int): Flow<List<AppNotification>> {
        return db.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("createdAt", Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    runCatching {
                        val notification = document.data<AppNotification>()
                        notification.copy(
                            id = if (notification.id.isBlank()) document.id else notification.id,
                            userId = if (notification.userId.isBlank()) userId else notification.userId,
                        )
                    }
                        .getOrNull()
                }
            }
    }

    override suspend fun markAsRead(userId: String, notificationId: String) {
        db.collection("users")
            .document(userId)
            .collection("notifications")
            .document(notificationId)
            .update(
                "isRead" to true,
                "readAt" to DateTimeUtils.nowIsoMinute(),
            )
    }

    override suspend fun markAsUnread(userId: String, notificationId: String) {
        db.collection("users")
            .document(userId)
            .collection("notifications")
            .document(notificationId)
            .update(
                "isRead" to false,
                "readAt" to "",
            )
    }

    override suspend fun markAllAsRead(userId: String, notificationIds: List<String>) {
        if (notificationIds.isEmpty()) return

        val now = DateTimeUtils.nowIsoMinute()

        notificationIds.forEach { notificationId ->
            db.collection("users")
                .document(userId)
                .collection("notifications")
                .document(notificationId)
                .update(
                    "isRead" to true,
                    "readAt" to now,
                )
        }
    }

    override suspend fun createNotification(userId: String, notification: AppNotification): String {
        val createdAt = notification.createdAt.ifBlank { DateTimeUtils.nowIsoMinute() }
        val id = notification.id.ifBlank { buildNotificationId(createdAt, notification) }

        val normalized =
            notification.copy(
                id = id,
                userId = userId,
                createdAt = createdAt,
            )

        db.collection("users")
            .document(userId)
            .collection("notifications")
            .document(id)
            .set(normalized)

        return id
    }

    private fun buildNotificationId(createdAt: String, notification: AppNotification): String {
        val timestamp =
            createdAt.replace("-", "")
                .replace(":", "")
                .replace("T", "_")
                .replace(" ", "_")

        val suffix =
            (notification.appointmentId ?: notification.type.ifBlank { "general" })
                .replace(Regex("[^A-Za-z0-9_]"), "")
                .takeLast(16)
                .ifBlank { "item" }

        return "notif_${timestamp}_$suffix"
    }
}