package com.thehecotnha.myapplication.utils.enums

enum class Role {
    USER, ADMIN;

    open fun get(index: Int) : Role {
        return Role.entries[index]
    }
}