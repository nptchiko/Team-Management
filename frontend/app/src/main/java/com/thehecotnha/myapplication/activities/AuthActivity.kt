package com.thehecotnha.myapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.databinding.ActivityAuthBinding
import com.thehecotnha.myapplication.layouts.SignInFragment
import com.thehecotnha.myapplication.layouts.SignUpFragment

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.signUpPageButton.setOnClickListener {
            loadFragment(SignUpFragment())
            binding.signUpPageButton.setBackgroundResource(R.drawable.rounded_border_shadow)
            binding.signInPageButton.setBackgroundResource(android.R.color.transparent)
        }

        binding.signInPageButton.setOnClickListener {
            loadFragment(SignInFragment())
            binding.signUpPageButton.setBackgroundResource(android.R.color.transparent)
            binding.signInPageButton.setBackgroundResource(R.drawable.rounded_border_shadow)
        }

        binding.uploadButton.setOnClickListener {
            val intent: Intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }
    fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()

    }

}