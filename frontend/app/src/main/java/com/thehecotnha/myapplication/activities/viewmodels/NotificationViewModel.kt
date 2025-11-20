package com.thehecotnha.myapplication.activities.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehecotnha.myapplication.models.Notification
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.repository.NotificationRepository
import com.thehecotnha.myapplication.repository.UserRepository
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val notificationRepo = NotificationRepository()
    private val userRepo = UserRepository()

    val _allNotifications = MutableLiveData<List<Notification>?>()
    val _unreadCount = MutableLiveData<Int>()
    val _notificationState = MutableLiveData<Response<Void>>(Response.Idle)

    /**
     * Get all notifications for the current user
     */
    fun getUserNotifications() {
        val userId = userRepo.currentUser().uid

        notificationRepo.getUserNotifications(userId)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val notifications = value.toObjects(Notification::class.java)
                    _allNotifications.postValue(notifications)
                } else {
                    _allNotifications.postValue(emptyList())
                }
            }
    }

    /**
     * Get unread notifications count
     */
    fun getUnreadCount() = viewModelScope.launch {
        val userId = userRepo.currentUser().uid
        val response = notificationRepo.getUnreadCount(userId)

        if (response is Response.Success) {
            _unreadCount.postValue(response.data ?: 0)
        }
    }

    /**
     * Mark a notification as read
     */
    fun markAsRead(notificationId: String) = viewModelScope.launch {
        _notificationState.value = Response.Loading
        _notificationState.value = notificationRepo.markAsRead(notificationId)
    }

    /**
     * Mark all notifications as read
     */
    fun markAllAsRead() = viewModelScope.launch {
        val userId = userRepo.currentUser().uid
        _notificationState.value = Response.Loading
        _notificationState.value = notificationRepo.markAllAsRead(userId)
    }

    /**
     * Delete a notification
     */
    fun deleteNotification(notificationId: String) = viewModelScope.launch {
        _notificationState.value = Response.Loading
        _notificationState.value = notificationRepo.deleteNotification(notificationId)
    }


}