package com.thehecotnha.myapplication.utils.enums

enum class Role {
    USER, ADMIN, PROJECT_MEMBER, PROJECT_ADMIN;

    open fun get(index: Int) : Role {
        return Role.entries[index]
    }
}