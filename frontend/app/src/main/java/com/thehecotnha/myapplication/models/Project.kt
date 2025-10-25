package com.thehecotnha.myapplication.models


import com.google.firebase.Timestamp

/**
 * Project
 */

data class Project(
    var id: String? = null,
    var title: String = "",
    var description: String = "",
    var dueDate: Timestamp? = null,
    var state: String = "TODO",
    var searchTitle: String = "",
    var teams: MutableList<String> = mutableListOf(),
)