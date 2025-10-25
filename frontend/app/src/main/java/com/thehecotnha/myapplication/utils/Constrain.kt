package com.thehecotnha.myapplication.utils


object FBConstant{
    const val COLLECTION_PROJECT = "projects"
    const val COLLECTION_USER = "users"
    const val COLLECTION_TASK = "tasks"
    const val COLLECTION_NOTIFICATION = "notifications"
}


enum class NotificationType {
    NONE, ADDED_TO_PROJECT, REMOVED_FROM_PROJECT,
    ASSIGNED_TO_TASK, REMOVED_TO_TASK;

    open fun get(index: Int): NotificationType {
        return NotificationType.entries[index]
    }
}