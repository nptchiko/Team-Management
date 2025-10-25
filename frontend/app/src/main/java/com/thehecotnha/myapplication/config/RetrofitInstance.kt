package com.thehecotnha.myapplication.config

import com.google.gson.GsonBuilder
import com.thehecotnha.myapplication.repository.api.WebhookPdfApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {
        val mainUrl = "http://10.0.2.2:8080/"

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()


        val webhookPdfApi : WebhookPdfApi by lazy {
            Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(mainUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
                .create(WebhookPdfApi::class.java)

        }



    }
}