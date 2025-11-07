package com.thehecotnha.myapplication.models


import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

/**
 * Project
 */

@Parcelize
data class Project(
    var id: String? = null,
    var title: String = "",
    var description: String = "",
    var dueDate: Timestamp? = Timestamp.now(),
    var state: String = "TODO",
    var searchTitle: String = "",
    var ownerId: String = "",
    var teams: MutableList<String> = mutableListOf(),
) : Parcelable