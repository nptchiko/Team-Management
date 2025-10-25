package com.thehecotnha.myapplication.layouts

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thehecotnha.myapplication.activities.MainActivity
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.viewmodels.auth.AuthViewModel
import com.thehecotnha.myapplication.viewmodels.auth.AuthViewModelFactory
import com.thehecotnha.myapplication.databinding.FragmentSignInBinding
import com.thehecotnha.myapplication.repository.UserRepository
import com.thehecotnha.myapplication.utils.Response
import com.thehecotnha.myapplication.utils.showProgressDialog
import kotlin.getValue
import kotlin.jvm.java

//import com.thehecotnha.myapplication.layouts.ui.login.LoginViewModel
//import com.thehecotnha.myapplication.layouts.ui.login.LoginViewModelFactory

class SignInFragment : Fragment() {

    private val viewModel: AuthViewModel by lazy {
        ViewModelProvider(this, AuthViewModelFactory(UserRepository()))[AuthViewModel::class.java]
    }
    private var _binding: FragmentSignInBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEditTextTransparency(binding.usernameEditText, binding.usernameTextView, false)
        setEditTextTransparency(binding.passwordEditText, binding.passwordTextView, false)

        binding.signInButton.setOnClickListener {
           signIn()
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

    private fun setEditTextTransparency(editText: EditText, label: TextView, hasFocus: Boolean) {
        val fieldValue = editText.getText().toString()
        if (!hasFocus && editText !== binding.passwordEditText) {
            validateField(editText, label)
        }
        if (!hasFocus && fieldValue.isEmpty()) {
            editText.setAlpha(0.5f)
        } else {
            editText.setAlpha(1.0f)
        }
    }

    private fun validateField(field: EditText, label: TextView) {
        val fieldValue = field.getText().toString()

        field.setBackgroundResource(R.drawable.rounded_border_shadow)
        field.setPadding(70, 0, 35, 10)
        field.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        label.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        if (!TextUtils.isEmpty(fieldValue)) {
            if (field === binding.usernameEditText && !field.isFocused()) {
                field.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.correct),
                    null
                )
            } else if (field === binding.passwordEditText && !field.isFocused()) {
                // Specific password validation if needed
            }
        }
    }

    private fun togglePasswordVisibility() {
        if (binding.passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.eyeopen),
                null
            )
            binding.passwordEditText.setTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.inter_black
                )
            )
        } else {
            binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.eyeclose),
                null
            )
            binding.passwordEditText.setTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.inter_black
                )
            )
        }
        binding.passwordEditText.setSelection(binding.passwordEditText.length())
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun signIn() {
        // email
        val email = binding.usernameEditText.getText().toString()
        val password = binding.passwordEditText.getText().toString()


        if ( email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validate email format
        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
            binding.usernameEditText.error = "Invalid email format" // Show error directly on the field
            return
        }


        viewModel.signInState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Failure -> {
                    Toast.makeText(requireContext(), response.e?.message ?: "Sign in failed", Toast.LENGTH_LONG).show()

                    binding.usernameEditText.setBackgroundResource(R.drawable.rounded_error_border_shadow)
                    binding.passwordEditText.setBackgroundResource(R.drawable.rounded_error_border_shadow)
                    binding.usernameEditText.setPadding(70, 0, 35, 10)
                    binding.passwordEditText.setPadding(70, 0, 35, 10)
                    binding.usernameTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.passwordTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.usernameEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(requireContext(), R.drawable.error),
                        null
                    )

                }
                is Response.Success -> {
                    val intent: Intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(requireContext(), "Welcome", Toast.LENGTH_SHORT).show()

                }
                is Response.Idle -> {}
                is Response.Loading -> {
                    showProgressDialog(requireContext(), "Loading")
                }
            }
        }

        viewModel.signIn(email, password)
    }
}