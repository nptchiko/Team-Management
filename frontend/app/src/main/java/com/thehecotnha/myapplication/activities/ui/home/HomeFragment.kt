package com.thehecotnha.myapplication.activities.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jiradashboard.HomeTaskAdapter
import com.google.firebase.Timestamp


import com.thehecotnha.myapplication.activities.DashboardActivity
import com.thehecotnha.myapplication.activities.ProfileActivity
import com.thehecotnha.myapplication.activities.ui.project.ProjectDetailFragment
import com.thehecotnha.myapplication.activities.ui.tasks.TaskDetailFragment
import com.thehecotnha.myapplication.adapters.ProjectAdapter
import com.thehecotnha.myapplication.databinding.FragmentHomeBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.ProjectItem

import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.utils.showAleartDialog
import com.thehecotnha.myapplication.activities.viewmodels.AuthViewModel
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import com.thehecotnha.myapplication.models.HomeTaskItem
import com.thehecotnha.myapplication.utils.removeTime
import com.thehecotnha.myapplication.models.Task

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val projViewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }
    private var userInfo: User = User()
    private var tasksByProject: Map<String, List<Task>> = emptyMap()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(AuthViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvTodayTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIncoming.layoutManager = LinearLayoutManager(requireContext())

        // ✅ Đăng ký observer TRƯỚC khi gọi getUserData()
        // retrieve user data
        // tracking user data
        viewModel.userState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    userInfo = response.data!!
                    // ✅ Hiển thị thông tin user đã đăng nhập
                    binding.userNameText.text = userInfo.username
                }
                is Response.Failure -> {
                    showAleartDialog(requireContext(), "Oops!", response.e?.message ?: "Failed to load user.")
                    binding.userNameText.text = "Guest"
                }
                Response.Idle -> {}
                Response.Loading -> {
                    // Có thể hiển thị loading indicator nếu cần
                }
            }
        }
        
        // ✅ Gọi getUserData() ngay sau khi đăng ký observer
        viewModel.getUserData()

        // tracking project data
        projViewModel._allProjects.observe(viewLifecycleOwner){
                var cnt = 0
                val projectItems = it?.map { project ->
                    val tasks = tasksByProject[project.id] ?: emptyList()
                    val total = tasks.size
                    val done = tasks.count { it.state == "DONE" }
                    val tasksLeft = total - done
                    val percent = if (total > 0) (done * 100) / total else 0
                    ProjectItem(
                        project.title,
                        cnt++,
                        project.state,
                        CalendarDate(project.dueDate!!.toDate()).calendar,
                        tasksLeft,
                        percent,
                        project.teams.size
                    )
                } ?: emptyList()

                binding.projectRecyclerView.adapter = ProjectAdapter(projectItems) { project ->
                    // Handle project item click
                    val projectDetailFragment = ProjectDetailFragment.newInstance(it!![project.pos])
                    (activity as? DashboardActivity)?.loadFragment(projectDetailFragment)
                }
        }

        projViewModel._allTasks.observe(viewLifecycleOwner) { tasks ->
            tasksByProject = tasks.groupBy { it.projectId }
        }

        projViewModel._projectTask.observe(viewLifecycleOwner){
            var cnt = 0
            val todayTaskItems = it?.map { task ->
                HomeTaskItem(
                    task.title,
                    task.projectName,
                    task.endDate!!.toDate(),
                    task.assignedTo[0],
                    cnt++
                )
            } ?: emptyList()

            binding.rvTodayTasks.adapter = HomeTaskAdapter(todayTaskItems.filter {
                removeTime(it.dueDate) == removeTime(Timestamp.now().toDate())
            }) {selected ->
                val taskDetailFragment = TaskDetailFragment.newInstance(it!![selected.idx])
                (activity as? DashboardActivity)?.loadFragment(taskDetailFragment)

            }

            binding.rvIncoming.adapter = HomeTaskAdapter(todayTaskItems.filter {
                removeTime(it.dueDate) > removeTime(Timestamp.now().toDate())
            }) { selected ->
                val taskDetailFragment = TaskDetailFragment.newInstance(it!![selected.idx])
                (activity as? DashboardActivity)?.loadFragment(taskDetailFragment)
            }
        }


        projViewModel.getAllUserTasks()

        viewModel.getUserData()
        // retrieve user's project
        projViewModel.getUserProjects()

        projViewModel.getTaskForDay(Timestamp.now().toDate(), true)

        // Nhap vao profile image de den trang profile
        binding.profileImage.setOnClickListener {
            val intent: Intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("userInfo", userInfo)
            startActivity(intent)
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}