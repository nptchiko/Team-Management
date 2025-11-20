package com.thehecotnha.myapplication.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.thehecotnha.myapplication.models.Notification
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.utils.NotificationType
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NotificationRepository {

    private val TAG = "NOTIFICATION_REPOSITORY"

    private val notificationRef = FirebaseModule.notificationCollection
    private val userRef = FirebaseModule.userCollection
    private val auth = FirebaseModule.firebaseAuth

    /**
     * Create a notification for a user
     */
    suspend fun createNotification(
        userId: String,
        senderName: String,
        notificationType: NotificationType,
        itemName: String,
        text: String
    ): Response<Notification> {
        return try {
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                userId = userId,
                senderName = senderName,
                isRead = false,
                text = text,
                sendAt = Timestamp.now(),
                notificationType = notificationType.ordinal,
                itemName = itemName
            )

            notificationRef.document(notification.id)
                .set(notification)
                .addOnSuccessListener {
                    Log.d(TAG, "createNotification: created notification id=${notification.id}")
                }
                .await()

            Response.Success(notification)
        } catch (e: Exception) {
            Log.e(TAG, "createNotification: failed", e)
            Response.Failure(e)
        }
    }

    /**
     * Get all notifications for a user
     */
    fun getUserNotifications(userId: String): Query {
        return notificationRef
            .whereEqualTo("userId", userId)
            .orderBy("sendAt", Query.Direction.DESCENDING)
    }

    /**
     * Get unread notifications count
     */
    suspend fun getUnreadCount(userId: String): Response<Int> {
        return try {
            val snapshot = notificationRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            Response.Success(snapshot.size())
        } catch (e: Exception) {
            Log.e(TAG, "getUnreadCount: failed", e)
            Response.Failure(e)
        }
    }

    /**
     * Mark notification as read
     */
    suspend fun markAsRead(notificationId: String): Response<Void> {
        return try {
            notificationRef.document(notificationId)
                .update("isRead", true)
                .await()

            Response.Success(null)
        } catch (e: Exception) {
            Log.e(TAG, "markAsRead: failed", e)
            Response.Failure(e)
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    suspend fun markAllAsRead(userId: String): Response<Void> {
        return try {
            val snapshot = notificationRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            snapshot.documents.forEach { doc ->
                doc.reference.update("isRead", true).await()
            }

            Response.Success(null)
        } catch (e: Exception) {
            Log.e(TAG, "markAllAsRead: failed", e)
            Response.Failure(e)
        }
    }

    /**
     * Delete a notification
     */
    suspend fun deleteNotification(notificationId: String): Response<Void> {
        return try {
            notificationRef.document(notificationId)
                .delete()
                .await()

            Response.Success(null)
        } catch (e: Exception) {
            Log.e(TAG, "deleteNotification: failed", e)
            Response.Failure(e)
        }
    }

    /**
     * Helper function to send notification when user is added to project
     */
    suspend fun notifyUserAddedToProject(
        userId: String,
        projectName: String,
        senderName: String
    ): Response<Notification> {
        return createNotification(
            userId = userId,
            senderName = senderName,
            notificationType = NotificationType.ADDED_TO_PROJECT,
            itemName = projectName,
            text = "$senderName added you to project: $projectName"
        )
    }

    /**
     * Helper function to send notification when user is removed from project
     */
    suspend fun notifyUserRemovedFromProject(
        userId: String,
        projectName: String,
        senderName: String
    ): Response<Notification> {
        return createNotification(
            userId = userId,
            senderName = senderName,
            notificationType = NotificationType.REMOVED_FROM_PROJECT,
            itemName = projectName,
            text = "$senderName removed you from project: $projectName"
        )
    }

    /**
     * Helper function to send notification when user is assigned to task
     */
    suspend fun notifyUserAssignedToTask(
        userId: String,
        taskName: String,
        projectName: String,
        senderName: String
    ): Response<Notification> {
        return createNotification(
            userId = userId,
            senderName = senderName,
            notificationType = NotificationType.ASSIGNED_TO_TASK,
            itemName = taskName,
            text = "$senderName assigned you to task: $taskName in $projectName"
        )
    }

    /**
     * Helper function to send notification when user is removed from task
     */
    suspend fun notifyUserRemovedFromTask(
        userId: String,
        taskName: String,
        senderName: String
    ): Response<Notification> {
        return createNotification(
            userId = userId,
            senderName = senderName,
            notificationType = NotificationType.REMOVED_TO_TASK,
            itemName = taskName,
            text = "$senderName removed you from task: $taskName"
        )
    }

    suspend fun notifyUserUpdatedTask(
        userId: String,
        taskName: String,
        projectName: String,
        senderName: String
    ): Response<Notification> {
        return createNotification(
            userId = userId,
            senderName = senderName,
            notificationType = NotificationType.ASSIGNED_TO_TASK,
            itemName = taskName,
            text = "$senderName updated your task: $taskName in $projectName"
        )
    }
}