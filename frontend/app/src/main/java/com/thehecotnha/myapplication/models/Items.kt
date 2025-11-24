package com.thehecotnha.myapplication.models

import com.thehecotnha.myapplication.utils.enums.TeamRole
import java.util.Date


data class QuickAccessItem(
    val title: String,
    val subtitle: String,
    val icon: Int
)

data class HomeTaskItem(
    val title: String,
    val projName: String,
    val dueDate: Date,
    val assigneeName: String,
    val idx:Int,
    val priority: String
)

data class ProjectItem(

    val title: String,
    val pos: Int,
    val state: String,
    val dueDate: String,
    val taskLefts: Int,
    val projectPercent: Int,
    val team: Int,
)


// Dung de bieu dien UI
data class TeamItem(
    val name: String,
    val userId: String,
    val role: String = TeamRole.MEMBER.name,
)