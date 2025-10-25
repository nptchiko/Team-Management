package com.thehecotnha.myapplication.models.api

import com.google.gson.annotations.SerializedName

data class PDFItem(
    @SerializedName("error")
    val error: String,

    @SerializedName("result")
    val result: String
)
