package com.thehecotnha.myapplication.utils

import android.app.Dialog
import android.content.Context
import com.thehecotnha.myapplication.databinding.DialogProgressBinding


fun showProgressDialog(context: Context, text: String) {
    val dialog = Dialog(context)
    val binding = DialogProgressBinding.inflate(dialog.layoutInflater)
    dialog.setContentView(binding.root)

    binding.tvProgressText.text = text
    dialog.show()
}


