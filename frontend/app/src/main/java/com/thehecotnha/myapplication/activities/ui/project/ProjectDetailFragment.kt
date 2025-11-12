package com.thehecotnha.myapplication.activities.ui.project

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.activities.DashboardActivity
import com.thehecotnha.myapplication.activities.ui.tasks.NewTaskFragment
import com.thehecotnha.myapplication.activities.ui.tasks.TaskDetailFragment
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import com.thehecotnha.myapplication.adapters.TaskDetailAdapter
import com.thehecotnha.myapplication.databinding.FragmentProjectDetailBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.Task


@Suppress("DEPRECATION")
class ProjectDetailFragment : Fragment() {

    private var _binding: FragmentProjectDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val b get() = _binding!!

    private var todoAdapter: TaskDetailAdapter = TaskDetailAdapter(mutableListOf()) {  }
    private var inProgressAdapter: TaskDetailAdapter = TaskDetailAdapter(mutableListOf()) {  }
    private var doneAdapter: TaskDetailAdapter = TaskDetailAdapter(mutableListOf()) {}

    private val viewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }
    private var project: Project? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Retrieve the project object from arguments
            project = it.getParcelable(ARG_PROJECT) } // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectDetailBinding.inflate(inflater, container, false)
        val root = b.root

        b.tvProjectTitle.text = project?.title
        b.tvProjectDescription.text = project?.description
        b.tvDueDate.text = CalendarDate(project?.dueDate!!.toDate()).calendar


        b.rvTodoTasks.layoutManager = LinearLayoutManager(requireContext())
        b.rvProgressTasks.layoutManager = LinearLayoutManager(requireContext())
        b.rvDoneTasks.layoutManager = LinearLayoutManager(requireContext())

        Toast.makeText(requireContext(), "Project ID: ${project?.id}", Toast.LENGTH_SHORT).show()


        viewModel._projectTask.observe(viewLifecycleOwner) { tasks ->
            todoAdapter = TaskDetailAdapter(tasks.filter { task -> task.state == "TODO" })  { selected ->
                loadTask(selected)
            }
            b.rvTodoTasks.adapter = todoAdapter

            inProgressAdapter = TaskDetailAdapter(tasks.filter { task -> task.state == "IN PROGRESS" }) { selected ->
                loadTask(selected)
            }
            b.rvProgressTasks.adapter = inProgressAdapter

            doneAdapter = TaskDetailAdapter(tasks.filter { task -> task.state == "DONE" }) { selected ->
                loadTask(selected)
            }
            b.rvDoneTasks.adapter = doneAdapter

        }
        viewModel.getTasksByFilter(requireContext(), project?.id!!, "all")



        // Handle toolbar navigation (back) icon click
        b.toolbarProjectDetail.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        b.fabAddTask.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_nav_activity_dashboard, NewTaskFragment.newInstance(project!!))
                .addToBackStack(null)
                .commit()
        }
        return root
    }

    companion object {
        private const val ARG_PROJECT = "project_arg"
        @JvmStatic
        fun newInstance(project: Project) =
            ProjectDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROJECT, project)
                }
            }
    }

    private fun loadTask(taskInfo: Task) {
        val taskDetailFragment = TaskDetailFragment.newInstance(taskInfo)
        (activity as? DashboardActivity)?.loadFragment(taskDetailFragment)
    }
}