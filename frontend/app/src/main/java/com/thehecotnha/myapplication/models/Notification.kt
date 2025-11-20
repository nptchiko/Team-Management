package com.thehecotnha.myapplication.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Notification
 */
data class Notification (
    var id: String = "",
    var userId: String = "",
    var senderName: String = "",
    @get:PropertyName("isRead") @set:PropertyName("isRead")
    var isRead : Boolean = false,
    var text: String = "",
    var sendAt:  Timestamp? = null,
    var notificationType: Int = 0,
    var itemName: String = ""
)