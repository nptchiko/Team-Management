package com.thehecotnha.myapplication.activities

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.thehecotnha.myapplication.databinding.ActivityProfileBinding
import com.thehecotnha.myapplication.utils.Response
import com.thehecotnha.myapplication.utils.showAleartDialog
import com.thehecotnha.myapplication.utils.showProgressDialog
import com.thehecotnha.myapplication.utils.showSuccessDialog
import com.thehecotnha.myapplication.activities.viewmodels.AuthViewModel

class ProfileActivity : AppCompatActivity() {


    private val viewModel: AuthViewModel by lazy {
        ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    private var progressDialog: Dialog? = null

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = showProgressDialog(this, "Loading profile...")
        // Dat listener de update giao dien khi co thay doi ve user
        viewModel.userState.observe(this) { response ->

            // tao object hien thi man hinh loading

            when (response) {
                is Response.Success -> {

                    if (progressDialog?.isShowing == true) {
                        progressDialog?.dismiss()
                    }
                    val user = response.data!!
                    binding.usernameTextView.text = user.username
                    binding.emailValueTextView.text = user.email
                    binding.phoneValueTextView.text = user.phone
                    binding.roleValueTextView.text = user.role.toString()
                    // You can load avatar using an image loading library like Glide or Picasso

                    if (user.avatarLink.isNotEmpty()) {
                        // Example using Glide:
                        // Glide.with(this).load(user.avatarLink).into(binding.avatarImageView)
                    }

                    showSuccessDialog(this, "Succesfully!", "Welcome back, ${user.username}!")
                }

                is Response.Failure -> {
                    //tat loading

                    if (progressDialog?.isShowing == true) {
                        progressDialog?.dismiss()
                    }

                    showAleartDialog(
                        context = this,
                        "Oops!",
                        response.e?.message ?: "Failed to load profile."
                    )
                }

                is Response.Idle -> {}
                is Response.Loading -> {
                    // hien thi loading
                    progressDialog?.show()

                }
            }

        }

        // lay info user
        viewModel.getUserData()

        binding.closeProfileButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}