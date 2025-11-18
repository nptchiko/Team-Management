package com.thehecotnha.myapplication.services

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.thehecotnha.myapplication.config.WebhookInstance
import com.thehecotnha.myapplication.models.api.PDFItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.io.copyTo
import kotlin.io.use

class PDFUploadingService {

    private val TAG = "UPLOADING PDF SERVICE"

    private var _pdfContentLiveData : MutableLiveData<String> = MutableLiveData()

    val pdfContentLiveData : LiveData<String> = _pdfContentLiveData

    fun uploadPdfFromUri(context: Context, pdfUri: Uri, description: String? = null) {

        val pdfFile = uriToFile(context, pdfUri)

        val requestFile = pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())

        val filePart  = MultipartBody.Part.createFormData(
            "data",
            pdfFile.name,
            requestFile
        )

        val description = description?.toRequestBody("text/plain".toMediaTypeOrNull())

        WebhookInstance.webhookPdfApi.uploadPdf(filePart, description)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        var result = response.body()
                        Log.i("PDF Uploading Service", "Upload successful")
                        val res = Gson().fromJson(result?.string(), PDFItem::class.java)

                        _pdfContentLiveData.value = res.result

                    } else {
                        Log.e("PDF Uploading Service", "Failed: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    t: Throwable
                ) {
                    _pdfContentLiveData.postValue("")
                    Log.e("PDF Uploading Service", "Upload error: ${t.message}")
                }
            })

    }




    private fun uriToFile(context: Context, pdfUri: Uri): File {
        val contextResolver = context.contentResolver
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.pdf")

        contextResolver.openInputStream(pdfUri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}