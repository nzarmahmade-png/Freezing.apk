package com.example.data.repository

import com.example.data.datasource.LocalMockDataSource
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

interface INotificationRepository {
    val notificationsFlow: Flow<List<NotificationItem>>
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun addNotification(title: String, message: String, type: NotificationType, targetId: String? = null)
}

class NotificationRepository : INotificationRepository {
    private val _notifications = MutableStateFlow<List<NotificationItem>>(LocalMockDataSource.notifications)
    override val notificationsFlow: Flow<List<NotificationItem>> = _notifications.asStateFlow()

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override suspend fun markAsRead(id: String) {
        val list = _notifications.value.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = list[index].copy(isRead = true)
            _notifications.value = list
        }
    }

    override suspend fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    override suspend fun addNotification(
        title: String,
        message: String,
        type: NotificationType,
        targetId: String?
    ) {
        val item = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            titleArabic = title,
            messageArabic = message,
            type = type,
            timestamp = "اليوم ${dateFormat.format(Date())}",
            isRead = false,
            targetId = targetId
        )
        _notifications.value = listOf(item) + _notifications.value
    }
}
