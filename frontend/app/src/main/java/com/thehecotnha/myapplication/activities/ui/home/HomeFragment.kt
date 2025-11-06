package com.thehecotnha.myapplication.activities.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jiradashboard.QuickAccessAdapter
import com.example.jiradashboard.RecentItemsAdapter
import com.google.firebase.Timestamp
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.activities.ProfileActivity
import com.thehecotnha.myapplication.adapters.ProjectAdapter
import com.thehecotnha.myapplication.databinding.FragmentHomeBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.ProjectItem
import com.thehecotnha.myapplication.models.QuickAccessItem
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.models.ViewItem
import com.thehecotnha.myapplication.utils.Response
import com.thehecotnha.myapplication.utils.showAleartDialog
import com.thehecotnha.myapplication.activities.viewmodels.AuthViewModel
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val projViewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }
    private var userInfo: User = User()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(AuthViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.projectRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        setupQuickAccess()
        setupRecentItems()
//        setupProjects()


        // retrieve user data
        // tracking user data
        viewModel.userState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    userInfo = response.data!!
                    binding.userNameText.text = userInfo.username
                }
                is Response.Failure -> {
                    showAleartDialog(requireContext(), "Oops!", response.e?.message ?: "Failed to load user.")
                    binding.userNameText.text = "Guest"
                }
                Response.Idle -> {}
                Response.Loading -> {}
            }
        }

        // tracking project data
        projViewModel._project.observe(viewLifecycleOwner){
                val projectItems = it?.map { project ->
                    ProjectItem(
                        // try common field names, fall back to defaults
                        project.title,
                        project.state,
                        // convert nullable Date (or timestamp) to calendar string
                        CalendarDate(project.dueDate!!.toDate()).calendar,
                        0,
                        0
                    )
                } ?: emptyList()
                binding.projectRecyclerView.adapter = ProjectAdapter(projectItems)
        }


        viewModel.getUserData()

        // retrieve user's project
        projViewModel.getUserProjects()

        // Nhap vao profile image de den trang profile
        binding.profileImageBackground.setOnClickListener {
            val intent: Intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("userInfo", userInfo)
            startActivity(intent)
        }




        return root
    }


    private fun setupQuickAccess() {
        binding.quickAccessRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val quickAccessItems = listOf(
            QuickAccessItem("My work", "Filter", R.drawable.outline_check_circle_24),
            QuickAccessItem("My work", "Filter", R.drawable.outline_check_circle_24),
            QuickAccessItem("My work", "Filter", R.drawable.outline_check_circle_24),
        )
        binding.quickAccessRecyclerView.adapter = QuickAccessAdapter(quickAccessItems)
    }

    private fun setupRecentItems() {
        binding.recentItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.overdueRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.incomingRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val viewItems = listOf(
            ViewItem("Place Holder", "Filter • Viewed", R.drawable.outline_check_circle_24),
            ViewItem("Place Holder", "Filter • Viewed", R.drawable.outline_check_circle_24),
            ViewItem("Place Holder", "Filter • Viewed", R.drawable.outline_check_circle_24),
        )

        binding.recentItemsRecyclerView.adapter = RecentItemsAdapter(viewItems)
        binding.overdueRecyclerView.adapter = RecentItemsAdapter(viewItems)
        binding.incomingRecyclerView.adapter = RecentItemsAdapter(viewItems)
    }

    private fun setupProjects() {

        // You can replace this with actual project data
        val projectItems = listOf(
            ProjectItem(
            "Project Alpha", "TODO", CalendarDate(Timestamp.now().toDate()).calendar, 0, 23),
        ProjectItem(
            "Project Alpha", "TODO", CalendarDate(Timestamp.now().toDate()).calendar, 0, 23),
        ProjectItem(
            "Project Alpha", "TODO", CalendarDate(Timestamp.now().toDate()).calendar, 0, 23),
            ProjectItem(
                "Project Alpha", "TODO", CalendarDate(Timestamp.now().toDate()).calendar, 0, 23),
        )


        binding.projectRecyclerView.adapter = ProjectAdapter(projectItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}