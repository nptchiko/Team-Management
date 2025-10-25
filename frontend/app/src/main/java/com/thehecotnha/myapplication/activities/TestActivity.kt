package com.thehecotnha.myapplication.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.thehecotnha.myapplication.databinding.ActivityTestBinding
import com.thehecotnha.myapplication.services.PDFUploadingService

class TestActivity : AppCompatActivity() {


    private lateinit var pdfUri : Uri
    private lateinit var uploader : PDFUploadingService
    private lateinit var binding: ActivityTestBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfUri = Uri.EMPTY

        uploader = PDFUploadingService()

        binding.uploadButton.setOnClickListener {

            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            imagePickerLauncher.launch(intent)

        }
    }


    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK) {
            if (it.data  != null) {
                val selectedImageUrl = it.data?.data
                selectedImageUrl?.let{ uri ->
                    pdfUri = uri
                    Log.i("PDF Picker Launcher", "PDF Loaded with URI: $pdfUri")

                    uploader.pdfContentLiveData.observe(this, Observer { item ->
                        binding.tvAiResponse.text = item
                    })
                    uploader.uploadPdfFromUri(this, pdfUri,"test with pdf")
                }
            }
        }
    }
}