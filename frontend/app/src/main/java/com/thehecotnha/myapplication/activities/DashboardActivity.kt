package com.thehecotnha.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.activities.ui.dashboard.DashboardFragment
import com.thehecotnha.myapplication.activities.ui.home.HomeFragment
import com.thehecotnha.myapplication.activities.ui.notifications.NotificationsFragment
import com.thehecotnha.myapplication.activities.ui.project.ProjectFragment
import com.thehecotnha.myapplication.databinding.ActivityDashboardBinding
import com.thehecotnha.myapplication.utils.toast

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView = binding.bottomNavigation
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    toast(this, "Home")
                    true
                }
                R.id.navigation_projects -> {
                    loadFragment(ProjectFragment())
                    toast(this, "Project")
                    true
                }
                R.id.navigation_dashboards -> {
                    toast(this, "Dashboard")
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.navigation_notifications -> {
                    toast(this, "Notifications")
                    loadFragment(NotificationsFragment())
                    true
                }
                else -> false
            }
        }

        // ensure default tab is shown on first launch
        if (savedInstanceState == null) {
            navView.selectedItemId = R.id.navigation_home
        }

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_nav_activity_dashboard, fragment)
            .commit()
    }
}