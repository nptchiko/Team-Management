package com.thehecotnha.myapplication.activities.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thehecotnha.myapplication.adapters.ProjectAdapter
import com.thehecotnha.myapplication.databinding.FragmentDashboardBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.ProjectItem
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var projectAdapter: ProjectAdapter
    
    private val projectViewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }
    private var tasksByProject: Map<String, List<Task>> = emptyMap()
    private var projectsList: List<com.thehecotnha.myapplication.models.Project>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        observeData()
        loadData()
        
        return root
    }

    private fun observeData() {
        // Observe tasks để group theo projectId
        projectViewModel._allTasks.observe(viewLifecycleOwner) { tasks ->
            tasksByProject = tasks.groupBy { it.projectId }
        }

        // Observe projects và map sang ProjectItem
        projectViewModel._project.observe(viewLifecycleOwner) { projects ->
            projectsList = projects
            var cnt = 0
            val projectItems = projects?.map { project ->
                val tasks = tasksByProject[project.id] ?: emptyList()
                val total = tasks.size
                val done = tasks.count { it.state == "DONE" }
                val tasksLeft = total - done
                val percent = if (total > 0) (done * 100) / total else 0
                
                ProjectItem(
                    title = project.title,
                    pos = cnt++,
                    state = project.state,
                    dueDate = CalendarDate(project.dueDate!!.toDate()).calendar,
                    taskLefts = tasksLeft,
                    projectPercent = percent,
                    team = project.teams.size
                )
            } ?: emptyList()

            projectAdapter = ProjectAdapter(projectItems) { projectItem ->
                showCardTask(projectItem)
            }
            binding.projectRecyclerView.adapter = projectAdapter
            
            // Hiển thị project đầu tiên nếu có
            if (projectItems.isNotEmpty()) {
                showCardTask(projectItems.first())
            }
        }
    }

    private fun loadData() {
        // Load dữ liệu từ Firebase
        projectViewModel.getAllUserTasks()
        projectViewModel.getUserProjects()
    }

    private fun setupRecyclerView() {
        // RecyclerView sẽ được setup trong observeData()
    }

    private fun showCardTask(projectItem: ProjectItem) {
        // Tìm project gốc từ pos để lấy projectId
        val project = projectsList?.getOrNull(projectItem.pos)
        
        if (project != null) {
            // Lấy tasks của project này
            val tasks = tasksByProject[project.id] ?: emptyList()
            
            // Tính số task completed (DONE)
            val completed = tasks.count { it.state == "DONE" }
            
            // Tính số task ongoing (TODO + IN_PROGRESS)
            val ongoing = tasks.count { it.state == "TODO" || it.state == "IN PROGRESS" || it.state == "IN_PROGRESS" }
            
            binding.taskCompletedCard.tvNumberCard.text = completed.toString()
            binding.taskOngoingCard.tvNumberCard.text = ongoing.toString()

            binding.taskCompletedCard.tvNameCard.text = "Completed Tasks"
            binding.taskOngoingCard.tvNameCard.text = "Ongoing Tasks"
        } else {
            // Fallback: dùng dữ liệu từ ProjectItem
            val completed = if (projectItem.projectPercent == 100 && projectItem.taskLefts == 0) {
                // Nếu 100% và không có task left, giả sử có ít nhất 1 task completed
                1
            } else if (projectItem.projectPercent > 0) {
                // Tính ngược lại từ percent và taskLefts
                val total = projectItem.taskLefts / (1 - projectItem.projectPercent / 100.0)
                (total - projectItem.taskLefts).toInt()
            } else {
                0
            }
            
            binding.taskCompletedCard.tvNumberCard.text = completed.toString()
            binding.taskOngoingCard.tvNumberCard.text = projectItem.taskLefts.toString()

            binding.taskCompletedCard.tvNameCard.text = "Completed Tasks"
            binding.taskOngoingCard.tvNameCard.text = "Ongoing Tasks"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}