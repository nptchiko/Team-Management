package com.thehecotnha.myapplication.models

import android.os.Parcelable
import com.thehecotnha.myapplication.utils.enums.Role
import kotlinx.parcelize.Parcelize


// Parcelize lam cho object User
// co the duoc truyen giua cac Activity/Fragment
// neu khong se phai request db nhieu

@Parcelize
data class User(

    var uid: String = "",

    var username: String = "",

    var email: String = "",

    var password: String = "",

    var role: Role = Role.USER,

    var avatarLink: String = "",

    var phone: String = "",

    ) : Parcelable
