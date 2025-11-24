package com.thehecotnha.myapplication.models

import android.os.Parcelable
import com.thehecotnha.myapplication.utils.enums.TeamRole
import kotlinx.parcelize.Parcelize



// Dung de luu tru tren Firestore
@Parcelize
data class TeamMember (
    var id: String = "",
    val name: String = "",
    var userId: String ="",
    var projectId: String = "",
    val role: String = TeamRole.MEMBER.name
) : Parcelable