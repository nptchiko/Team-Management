package com.thehecotnha.myapplication.activities.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jiradashboard.QuickAccessAdapter
import com.example.jiradashboard.RecentItemsAdapter
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.activities.ProfileActivity
import com.thehecotnha.myapplication.databinding.FragmentHomeBinding
import com.thehecotnha.myapplication.models.QuickAccessItem
import com.thehecotnha.myapplication.models.ViewItem
import com.thehecotnha.myapplication.utils.Response
import com.thehecotnha.myapplication.utils.showAleartDialog
import com.thehecotnha.myapplication.viewmodels.auth.AuthViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(AuthViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupQuickAccess()
        setupRecentItems()


        viewModel.userState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    val user = response.data!!
                    binding.userNameText.text = user.username
                }
                is Response.Failure -> {
                    showAleartDialog(requireContext(), "Oops!", response.e?.message ?: "Failed to load user.")
                    binding.userNameText.text = "Guest"
                }
                Response.Idle -> {}
                Response.Loading -> {}
            }
        }

        viewModel.getUserData()

        binding.profileImageBackground.setOnClickListener {
            val intent: Intent = Intent(requireContext(), ProfileActivity::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}