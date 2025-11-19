package com.thehecotnha.myapplication.utils

import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import com.thehecotnha.myapplication.R


object FBConstant{
    const val COLLECTION_PROJECT = "projects"
    const val COLLECTION_USER = "users"
    const val COLLECTION_TASK = "tasksAffected"
    const val COLLECTION_NOTIFICATION = "notifications"

}

object priorityName {
    const val HIGH = "High"
    const val MEDIUM = "Medium"
    const val LOW = "Low"

    fun mapToInt(name: String): Int {
        return when(name) {
            HIGH -> 0
            MEDIUM -> 1
            LOW -> 2
            else -> 1
        }
    }
}

enum class NotificationType {
    NONE, ADDED_TO_PROJECT, REMOVED_FROM_PROJECT,
    ASSIGNED_TO_TASK, REMOVED_TO_TASK;

    open fun get(index: Int): NotificationType {
        return NotificationType.entries[index]
    }
}
