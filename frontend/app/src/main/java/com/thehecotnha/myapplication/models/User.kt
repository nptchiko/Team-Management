package com.thehecotnha.myapplication.models

import com.thehecotnha.myapplication.utils.enums.Role

data class User(

    var uid: String?,

    var username: String = "",

    var email: String = "",

    var password: String = "",

    var role: Role = Role.USER,

    var avatarLink: String = "",

    var phone: String = "",

    )