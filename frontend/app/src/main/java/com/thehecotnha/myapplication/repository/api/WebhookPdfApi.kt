package com.thehecotnha.myapplication.repository.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WebhookPdfApi {

    @Multipart
    @POST("webhook-test/ai_pdf_summariser")
    fun uploadPdf(
        @Part pdfFile: MultipartBody.Part,
        @Part("description") description: RequestBody? = null
    ): Call<ResponseBody>
}