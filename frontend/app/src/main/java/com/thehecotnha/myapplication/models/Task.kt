package com.thehecotnha.myapplication.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.thehecotnha.myapplication.utils.priorityName
import kotlinx.parcelize.Parcelize
import java.sql.Time


/**
 * Task
 */
@Parcelize
data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var startDate: Timestamp? = Timestamp.now(),
    var endDate: Timestamp? = null,
    var state: String = "TODO",
    var projectId: String = "",
    var projectName: String = "",
    // User id
    var assignedTo: MutableList<String> = mutableListOf(),
    var updatedBy: String = "",
    var updatedAt: Timestamp? = null,
    var searchTitle: String = "",
   // var availableDays: MutableList<Timestamp> = mutableListOf()
    var priority: String = priorityName.MEDIUM
) : Parcelable
