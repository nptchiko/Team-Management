package com.thehecotnha.myapplication.models

import android.health.connect.datatypes.units.Percentage


data class QuickAccessItem(
    val title: String,
    val subtitle: String,
    val icon: Int
)

data class ViewItem(
    val title: String,
    val subtitle: String,
    val icon: Int
)

data class ProjectItem(

    val title: String,
    val pos: Int,
    val state: String,
    val dueDate: String,
    val taskLefts: Int,
    val projectPercent: Int
)