package com.thehecotnha.myapplication.layouts


import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.activities.viewmodels.AuthViewModel
import com.thehecotnha.myapplication.databinding.FragmentSignUpBinding
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.utils.showProgressDialog


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val viewModel by lazy {
        ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener {
            signUp()
        }

        binding.passwordEditText.setOnTouchListener { v: View?, event: MotionEvent? ->
            // Check if drawableEnd is clicked
            if (event!!.action == MotionEvent.ACTION_UP && event.rawX >= (binding.passwordEditText.right - binding.passwordEditText.getCompoundDrawables()[2].getBounds()
                    .width())
            ) {
                togglePasswordVisibility()
                true
            }
            false
        }
    }

    private fun signUp() {
        val username = binding.usernameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val repeatPassword = binding.repeatPasswordEditText.text.toString()

        // 1. Validate for empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validate email format
        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
            binding.emailEditText.error = "Invalid email format" // Show error directly on the field
            return
        }

        // 3. Validate password length
        if (password.length < 6 || password.length > 20) {
            Toast.makeText(requireContext(), "Password must be between 6 and 20 characters", Toast.LENGTH_SHORT).show()
            binding.passwordEditText.error = "Password must be 6-20 characters long"
            return
        }

        // 4. Validate that passwords match
        if (password != repeatPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            binding.repeatPasswordEditText.error = "Passwords do not match"
            return
        }

        // Update UI to show loading state
        viewModel.signUpState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Failure -> {

                }
                is Response.Success -> {
                    Toast.makeText(requireContext(), "Sign up success", Toast.LENGTH_SHORT).show()
                }
                is Response.Idle -> {}
                is Response.Loading -> {
                    showProgressDialog(requireContext(), "Loading")
                }
            }
        }
        // Rememember to call observe before calling signUp to avoid missing updates
        // Call sign up function in ViewModel
        viewModel.signUp(user = User(username = username, email = email, password = password))

    }

    /**
     * Helper function to validate email format using Android's built-in pattern.
     */
    private fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * A helper function to show a visual error state on all fields.
     */
    private fun showErrorState() {
        val errorDrawable = R.drawable.rounded_error_border_shadow // Your error background
        val redColor = ContextCompat.getColor(requireContext(), R.color.red) // Your error color

        // Set error background for EditTexts
        binding.usernameEditText.setBackgroundResource(errorDrawable)
        binding.emailEditText.setBackgroundResource(errorDrawable)
        binding.passwordEditText.setBackgroundResource(errorDrawable)
        binding.repeatPasswordEditText.setBackgroundResource(errorDrawable)

        // Set text color for labels (assuming you get them from binding)
        binding.usernameTextView.setTextColor(redColor)
        binding.emailTextView.setTextColor(redColor)
        binding.passwordTextView.setTextColor(redColor)
        binding.repeatPasswordTextView.setTextColor(redColor)
    }
    private fun togglePasswordVisibility() {
        if (binding.passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // Change password input type to visible
            binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            // Change drawableEnd icon to hide_password_icon
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.eyeopen),
                null
            )
            // Set font to Inter-Black
            binding.passwordEditText.setTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.inter_black
                )
            )
        } else {
            // Change password input type to hidden
            binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
            // Change drawableEnd icon to show_password_icon
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.eyeclose),
                null
            )
            // Remove font
            binding.passwordEditText.setTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.inter_black
                )
            )
        }
        binding.passwordEditText.setSelection(binding.passwordEditText.length())
    }
}
