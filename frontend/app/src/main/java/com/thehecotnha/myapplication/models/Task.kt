package com.thehecotnha.myapplication.models

import com.google.firebase.Timestamp



/**
 * Task
 */
data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
 //   var startDate: Timestamp? = null,
    var endDate: Timestamp? = null,
    var state: String = "TODO",
    var projectId: String = "",
    var projectName: String = "",
    var assignedTo: MutableList<String> = mutableListOf(),
    var updatedBy: String = "",
    var searchTitle: String = "",
   // var availableDays: MutableList<Timestamp> = mutableListOf()
)
