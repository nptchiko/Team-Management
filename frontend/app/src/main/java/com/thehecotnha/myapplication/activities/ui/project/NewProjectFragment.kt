package com.thehecotnha.myapplication.activities.ui.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thehecotnha.myapplication.databinding.FragmentNewProjectBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.thehecotnha.myapplication.activities.ui.adapters.TeamAdapter
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import com.thehecotnha.myapplication.models.CalendarDate
import java.util.Date

class NewProjectFragment : Fragment() {

    private var _binding: FragmentNewProjectBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private  val projViewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }

    private lateinit var teamAdapter: TeamAdapter

    private val teamMember = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewProjectBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set adapter for team members RecyclerView
        teamAdapter = TeamAdapter(teamMember)
        binding.rvTeam.adapter = teamAdapter

        // handle khi nhan nut calendar
        binding.ivDueDate.setOnClickListener {
            // Use the isVisible KTX property to toggle visibility.
            // This is cleaner and toggles between VISIBLE and GONE.
            // If the calendar is now visible, scroll to it.
            if (binding.calendarView.visibility != View.VISIBLE ) {
                // We post the scroll action to make sure the view has been measured and laid out
                // before we try to scroll to it.
                binding.calendarView.setVisibility(View.VISIBLE)
                binding.scroll.post {
                    // Scrolling to the bottom of the calendar view ensures the whole view is visible.
                    binding.scroll.smoothScrollTo(0, binding.calendarView.bottom)
                }
            } else {
                binding.calendarView.visibility = View.GONE
            }
        }

        // Handle back button click
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Chon ngat trong calendar view`

        var calendar: Calendar = Calendar.getInstance()

        // Handle khi chon ngay trong calendar view
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Create a Calendar instance to format the date
            calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            // Format the date into a readable string (e.g., "25/12/2023")
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = sdf.format(calendar.time)

            // Update the TextView with the selected date
            binding.tvDueDate.text = selectedDate
        }

        // Nut tao project moi
        binding.btnCreateProject.setOnClickListener {

            val title = binding.edtProjectTitle.text.toString().trim().ifEmpty {
                binding.edtProjectTitle.error = "Title cannot be empty"
                return@setOnClickListener
            }
            val dueDate = binding.tvDueDate.text.toString().trim().ifEmpty {
                binding.edtDueDate.error = "Due date cannot be empty"
                return@setOnClickListener
            }
            val description = binding.edtProjectDescription.text.toString().trim().ifEmpty {
                binding.edtProjectDescription.error = "Description cannot be empty"
                return@setOnClickListener
            }


            val project = Project(
                null,
                title,
                description,
                Timestamp(calendar.time),
                teams = teamMember
            )
            projViewModel.createProject(project)
        }


        binding.addUser.setOnClickListener {
            teamMember.add("usernameHolder")
            teamAdapter.notifyItemInserted(teamMember.size-1)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}