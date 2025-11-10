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
import com.thehecotnha.myapplication.activities.ui.tasks.NewTaskFragment
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import com.thehecotnha.myapplication.adapters.TaskDetailAdapter
import com.thehecotnha.myapplication.databinding.FragmentProjectDetailBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.Project



@Suppress("DEPRECATION")
class ProjectDetailFragment : Fragment() {

    private var _binding: FragmentProjectDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val b get() = _binding!!

    private var taskAdapter: TaskDetailAdapter? = null

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


        b.kanbanTodoColumn.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        b.kanbanTodoColumn.tvColumnTitle.text = "Todo"
        b.kanbanInProgressColumn.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        b.kanbanDoneColumn.rvTasks.layoutManager = LinearLayoutManager(requireContext())

        Toast.makeText(requireContext(), "Project ID: ${project?.id}", Toast.LENGTH_SHORT).show()


        viewModel._projectTask.observe(viewLifecycleOwner) { tasks ->
            taskAdapter = TaskDetailAdapter(tasks!!)  { pos ->
                Toast.makeText(requireContext(), "Clicked task at position: $pos", Toast.LENGTH_SHORT).show()
         /*       val projectDetailFragment = ProjectDetailFragment.newInstance(it!![project.pos])
                (activity as? DashboardActivity)?.loadFragment(projectDetailFragment)*/
                }
            b.kanbanTodoColumn.rvTasks.adapter = taskAdapter
/*            taskAdapter?.updateTasks(tasks.filter {
                it.state == getString(R.string.state_todo)
            })
            b.kanbanTodoColumn.rvTasks.adapter = taskAdapter

            taskAdapter?.updateTasks(tasks.filter {
                it.state == getString(R.string.state_progress)
            })
            b.kanbanInProgressColumn.rvTasks.adapter = taskAdapter

            taskAdapter?.updateTasks(tasks.filter {
                it.state == getString(R.string.state_completed)
            })
            b.kanbanDoneColumn.rvTasks.adapter = taskAdapter*/
        }
        viewModel.getTasksByFilter(requireContext(), project?.id!!, "all")


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
}