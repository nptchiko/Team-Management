package com.thehecotnha.myapplication.utils.enums

enum class TeamRole {
    ADMIN, MEMBER;

    fun hasAdminRole(role: TeamRole): Boolean {
        return role == ADMIN
    }
}