package com.thehecotnha.myapplication.utils

import android.app.Dialog
import android.content.Context
import com.thehecotnha.myapplication.databinding.DialogFailedBinding
import com.thehecotnha.myapplication.databinding.DialogProgressBinding
import com.thehecotnha.myapplication.databinding.DialogSuccessBinding


fun showProgressDialog(context: Context, text: String): Dialog {
    val dialog = Dialog(context)
    val binding = DialogProgressBinding.inflate(dialog.layoutInflater)
    dialog.setContentView(binding.root)

    binding.tvProgressText.text = text

    return dialog
}
fun toast(context: Context, message: String) {
    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
}

fun showSuccessDialog(context: Context, title: String, message: String) {
    val dialog = Dialog(context)
    val binding = DialogSuccessBinding.inflate(dialog.layoutInflater)

    dialog.setContentView(binding.root)
    binding.tvSuccessTitle.text = title
    binding.tvSuccessMessage.text = message

    binding.btnSuccessAction.setOnClickListener  {
        dialog.dismiss()
    }

    dialog.show()
}

fun showAleartDialog(context: Context, title: String, message: String) {
    val dialog = Dialog(context)
    val binding = DialogFailedBinding.inflate(dialog.layoutInflater)

    dialog.setContentView(binding.root)
    binding.tvFailedTitle.text = title
    binding.tvFailedMessage.text = message
    binding.btnFailedAction.setOnClickListener  {
        dialog.dismiss()
    }
    dialog.show()
}