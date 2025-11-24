package com.thehecotnha.myapplication.activities.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jiradashboard.HomeTaskAdapter
import com.thehecotnha.myapplication.activities.DashboardActivity
import com.thehecotnha.myapplication.activities.ui.project.ProjectDetailFragment
import com.thehecotnha.myapplication.activities.ui.tasks.TaskDetailFragment
import com.thehecotnha.myapplication.adapters.ProjectAdapter
import com.thehecotnha.myapplication.databinding.FragmentSearchBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.HomeTaskItem
import com.thehecotnha.myapplication.models.ProjectItem
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val projViewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }
    private var tasksByProject: Map<String, List<Task>> = emptyMap()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvProjects.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())

        setupObservers()
        setupSearchView()

        projViewModel.getAllUserTasks()
        projViewModel.getUserProjects()


        return root
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProjectsAndTasks(newText)
                return true
            }
        })
    }

    private fun setupObservers() {
        projViewModel._allProjects.observe(viewLifecycleOwner) { projects ->
            val projectItems = projects?.mapIndexed { index, project ->
                val tasks = tasksByProject[project.id] ?: emptyList()
                val total = tasks.size
                val done = tasks.count { it.state == "DONE" }
                val tasksLeft = total - done
                val percent = if (total > 0) (done * 100) / total else 0

                ProjectItem(
                    project.title,
                    index,
                    project.state,
                    CalendarDate(project.dueDate!!.toDate()).calendar,
                    tasksLeft,
                    percent,
                    project.teams.size
                )
            } ?: emptyList()

            binding.rvProjects.adapter = ProjectAdapter(projectItems) { project ->
                val projectDetailFragment = ProjectDetailFragment.newInstance(projects!![project.pos])
                (activity as? DashboardActivity)?.loadFragment(projectDetailFragment)
            }
        }

        projViewModel._allTasks.observe(viewLifecycleOwner) { tasks ->
            tasksByProject = tasks.groupBy { it.projectId }
            val taskItems = tasks.mapIndexed { index, task ->
                HomeTaskItem(
                    task.title,
                    task.projectName,
                    task.endDate!!.toDate(),
                    task.assignedTo[0],
                    index,
                    priority = task.priority
                )
            }
            binding.rvTasks.adapter = HomeTaskAdapter(taskItems) { selected ->
                val taskDetailFragment = TaskDetailFragment.newInstance(tasks[selected.idx])
                (activity as? DashboardActivity)?.loadFragment(taskDetailFragment)
            }
        }
    }

    private fun filterProjectsAndTasks(query: String?) {
        val projectAdapter = binding.rvProjects.adapter as? ProjectAdapter
        val taskAdapter = binding.rvTasks.adapter as? HomeTaskAdapter

        projectAdapter?.filter(query)
        taskAdapter?.filter(query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
