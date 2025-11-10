package com.thehecotnha.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thehecotnha.myapplication.databinding.ActivityDashboardBinding
import com.thehecotnha.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

   private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}