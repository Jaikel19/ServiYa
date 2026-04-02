package com.example.shared.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.notifications.INotificationsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val repository: INotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState(isLoading = true))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private var currentUserId: String? = null
    private var observeJob: Job? = null

    fun start(userId: String) {
        if (currentUserId == userId && observeJob != null) return

        currentUserId = userId
        observeJob?.cancel()

        _uiState.value = NotificationsUiState(isLoading = true)
        _unreadCount.value = 0

        observeJob =
            viewModelScope.launch {
                repository
                    .observeNotifications(userId = userId, limit = 30)
                    .catch { error ->
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "No se pudieron cargar las alertas.",
                            )
                        _unreadCount.value = 0
                    }
                    .collect { notifications ->
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                notifications = notifications,
                                errorMessage = null,
                            )

                        _unreadCount.value = notifications.count { !it.isRead }
                    }
            }
    }

    fun markAsRead(notificationId: String) {
        val userId = currentUserId ?: return

        val target = _uiState.value.notifications.firstOrNull { it.id == notificationId } ?: return
        if (target.isRead) return

        updateLocalNotificationReadState(notificationId = notificationId, isRead = true)

        viewModelScope.launch {
            runCatching { repository.markAsRead(userId, notificationId) }
                .onFailure { error ->
                    updateLocalNotificationReadState(notificationId = notificationId, isRead = false)
                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage = error.message ?: "No se pudo marcar la alerta como leída."
                        )
                }
        }
    }

    fun markAsUnread(notificationId: String) {
        val userId = currentUserId ?: return

        val target = _uiState.value.notifications.firstOrNull { it.id == notificationId } ?: return
        if (!target.isRead) return

        updateLocalNotificationReadState(notificationId = notificationId, isRead = false)

        viewModelScope.launch {
            runCatching { repository.markAsUnread(userId, notificationId) }
                .onFailure { error ->
                    updateLocalNotificationReadState(notificationId = notificationId, isRead = true)
                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage = error.message ?: "No se pudo marcar la alerta como no leída."
                        )
                }
        }
    }

    fun markAllAsRead() {
        val userId = currentUserId ?: return

        val unreadIds = _uiState.value.notifications.filter { !it.isRead }.map { it.id }
        if (unreadIds.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isMarkingAllAsRead = true)

            runCatching { repository.markAllAsRead(userId, unreadIds) }
                .onFailure { error ->
                    _uiState.value =
                        _uiState.value.copy(
                            isMarkingAllAsRead = false,
                            errorMessage = error.message ?: "No se pudieron marcar todas como leídas.",
                        )
                }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isMarkingAllAsRead = false)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun updateLocalNotificationReadState(
        notificationId: String,
        isRead: Boolean,
    ) {
        val updatedNotifications =
            _uiState.value.notifications.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = isRead)
                } else {
                    notification
                }
            }

        _uiState.value =
            _uiState.value.copy(
                notifications = updatedNotifications,
            )

        _unreadCount.value = updatedNotifications.count { !it.isRead }
    }
}