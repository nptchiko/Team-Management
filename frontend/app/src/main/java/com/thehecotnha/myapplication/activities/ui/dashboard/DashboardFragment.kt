package com.thehecotnha.myapplication.activities.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.databinding.FragmentDashboardBinding
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import java.util.Date

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val projectViewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        
        observeData()
        loadData()

        return binding.root
    }

    private fun observeData() {
        projectViewModel._allTasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks != null) {
                updateSummaryCards(tasks)
                updateStatusOverview(tasks)
            }
        }
    }

    private fun loadData() {
        projectViewModel.getAllUserTasks()
    }

    private fun updateSummaryCards(tasks: List<Task>) {
        // Total tasks done
        val doneCount = tasks.count { it.state == "DONE" }
        binding.tvDoneCount.text = "$doneCount done"

        // Set updated count to 0 as updatedAt is not available
//        binding.tvUpdatedCount.text = "0 updated"

        // Total tasks created (which is all of them)
        val createdCount = tasks.size
        binding.tvCreatedCount.text = "$createdCount created"

        // Total tasks due (not done and has a due date in the future)
        val dueCount = tasks.count { 
            it.endDate != null &&
            it.endDate!!.toDate().after(Date()) &&
            it.state != "DONE"
        }
        binding.tvDueCount.text = "$dueCount due"
    }

    private fun updateStatusOverview(tasks: List<Task>) {
        val totalTasks = tasks.size
        binding.tvTotalItems.text = totalTasks.toString()

        val todoCount = tasks.count { it.state == "To do" }
        val inProgressCount = tasks.count { it.state == "In Progress" }
        val doneCount = tasks.count { it.state == "DONE" }

        binding.tvTodoListCount.text = todoCount.toString()
        binding.tvInprogressListCount.text = inProgressCount.toString()
        binding.tvDoneListCount.text = doneCount.toString()

        if (totalTasks == 0) {
            binding.progressEmpty.visibility = View.VISIBLE
            binding.progressTodo.visibility = View.GONE
            binding.progressInprogress.visibility = View.GONE
            binding.progressDone.visibility = View.GONE
        } else {
            binding.progressEmpty.visibility = View.GONE

            var currentProgressAngle = -90f // Start from the top
            val degreesPerPercent = 3.6f // 360 degrees / 100 percent

            // To Do Progress
            if (todoCount > 0) {
                binding.progressTodo.visibility = View.VISIBLE
                val todoPercent = (todoCount.toFloat() / totalTasks * 100).toInt()
                binding.progressTodo.progress = todoPercent
                binding.progressTodo.rotation = currentProgressAngle
                currentProgressAngle += (todoPercent * degreesPerPercent)
            } else {
                binding.progressTodo.visibility = View.GONE
            }

            // In Progress Progress
            if (inProgressCount > 0) {
                binding.progressInprogress.visibility = View.VISIBLE
                val inProgressPercent = (inProgressCount.toFloat() / totalTasks * 100).toInt()
                binding.progressInprogress.progress = inProgressPercent
                binding.progressInprogress.rotation = currentProgressAngle
                currentProgressAngle += (inProgressPercent * degreesPerPercent)
            } else {
                binding.progressInprogress.visibility = View.GONE
            }

            // Done Progress
            if (doneCount > 0) {
                binding.progressDone.visibility = View.VISIBLE
                val donePercent = (doneCount.toFloat() / totalTasks * 100).toInt()
                binding.progressDone.progress = donePercent
                binding.progressDone.rotation = currentProgressAngle
            } else {
                binding.progressDone.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}