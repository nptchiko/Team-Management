package com.thehecotnha.myapplication.activities.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thehecotnha.myapplication.adapters.ProjectAdapter
import com.thehecotnha.myapplication.databinding.FragmentDashboardBinding
import com.thehecotnha.myapplication.models.ProjectItem

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        setupRecyclerView()
        return root
    }

    private fun setupRecyclerView() {
        var projectList = listOf(
            ProjectItem(
                title = "Making Wireframe",
                pos = 0,
                state = "TODO",
                taskLefts = 10,
                dueDate = "12/12/2025",
                projectPercent = 25
            ),
            ProjectItem(
                title = "Setup Project",
                pos = 1,
                state = "IN_PROGRESS",
                taskLefts = 5,
                dueDate = "20/12/2025",
                projectPercent = 50
            ),
            ProjectItem(
                title = "Design Database",
                pos = 2,
                state = "DONE",
                taskLefts = 0,
                dueDate = "01/12/2025",
                projectPercent = 100
            )
        )

        projectAdapter = ProjectAdapter(projectList) { projectItem ->
            Toast.makeText(context, "Clicked: ${projectItem.title}", Toast.LENGTH_SHORT).show()
            showCardTask(projectItem)
        }


        binding.projectRecyclerView.adapter = projectAdapter
    }

    private fun showCardTask(project: ProjectItem){
        val completed = project.taskLefts
        val ongoing = 10 - completed
        binding.taskCompletedCard.tvNumberCard.text = completed.toString()
        binding.taskOngoingCard.tvNumberCard.text = ongoing.toString()

        binding.taskCompletedCard.tvNameCard.text = "Completed Task"
        binding.taskOngoingCard.tvNameCard.text = "Ongoing Task"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}