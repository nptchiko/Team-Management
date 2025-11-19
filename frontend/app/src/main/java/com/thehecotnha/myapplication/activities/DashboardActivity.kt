package com.thehecotnha.myapplication.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.activities.ui.dashboard.DashboardFragment
import com.thehecotnha.myapplication.activities.ui.home.HomeFragment
import com.thehecotnha.myapplication.activities.ui.notifications.NotificationsFragment
import com.thehecotnha.myapplication.activities.ui.other.AdvancedFeatureFragment
import com.thehecotnha.myapplication.activities.ui.project.EditProjectFragment
import com.thehecotnha.myapplication.activities.ui.project.NewProjectFragment
import com.thehecotnha.myapplication.activities.ui.project.ProjectDetailFragment
import com.thehecotnha.myapplication.activities.ui.tasks.NewTaskFragment
import com.thehecotnha.myapplication.activities.ui.tasks.TaskDetailFragment
import com.thehecotnha.myapplication.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_nav_activity_dashboard)


            if (currentFragment is NewProjectFragment ||
                currentFragment is ProjectDetailFragment ||
                currentFragment is NewTaskFragment ||
                currentFragment is TaskDetailFragment ||
                currentFragment is EditProjectFragment ||
                currentFragment is AdvancedFeatureFragment
            ) {
                binding.bottomNavigation.visibility = View.GONE
                binding.ivAdd.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
                binding.ivAdd.visibility = View.VISIBLE
            }
        }

        val navView = binding.bottomNavigation
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())

                    true
                }
                R.id.navigation_advanced_feat -> {
                    loadFragment(AdvancedFeatureFragment.newInstance())
                    true
                }
                R.id.navigation_dashboards -> {

                    loadFragment(DashboardFragment())
                    true
                }
                R.id.navigation_notifications -> {

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

        // them project moi

        binding.ivAdd.setOnClickListener {
            loadFragment(NewProjectFragment())
        }


    }
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_nav_activity_dashboard, fragment)
            .apply { addToBackStack(fragment.toString()) }
            .commit()
    }
}