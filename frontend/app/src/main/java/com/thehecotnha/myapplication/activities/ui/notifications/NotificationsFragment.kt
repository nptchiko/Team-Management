package com.thehecotnha.myapplication.activities.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thehecotnha.myapplication.activities.viewmodels.NotificationViewModel
import com.thehecotnha.myapplication.adapters.NotificationAdapter
import com.thehecotnha.myapplication.databinding.FragmentNotificationsBinding
import com.thehecotnha.myapplication.models.Response

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this).get(NotificationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()

        // Load notifications
        viewModel.getUserNotifications()
        viewModel.getUnreadCount()

        // Mark all as read button
        binding.btnMarkAllRead.setOnClickListener {
            viewModel.markAllAsRead()
        }
    }

    private fun setupRecyclerView() {
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() {
        // Observe notifications
        viewModel._allNotifications.observe(viewLifecycleOwner) { notifications ->
            if (notifications.isNullOrEmpty()) {
                binding.rvNotifications.visibility = View.GONE
                binding.tvEmptyNotifications.visibility = View.VISIBLE
            } else {
                binding.rvNotifications.visibility = View.VISIBLE
                binding.tvEmptyNotifications.visibility = View.GONE

                binding.rvNotifications.adapter = NotificationAdapter(
                    notifications = notifications,
                    onItemClick = { notification ->
                        // Mark as read when clicked
                        if (!notification.isRead) {
                            viewModel.markAsRead(notification.id)
                        }
                    },
                    onDeleteClick = { notification ->
                        showDeleteConfirmationDialog(notification.id)
                    }
                )
            }
        }

        // Observe unread count
        viewModel._unreadCount.observe(viewLifecycleOwner) { count ->
            binding.tvUnreadCount.text = "$count unread notifications"
            binding.btnMarkAllRead.isEnabled = count > 0
        }

        // Observe notification operations state
        viewModel._notificationState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    // Refresh data after operation
                    viewModel.getUserNotifications()
                    viewModel.getUnreadCount()
                }
                is Response.Failure -> {
                    // Handle error
                }
                else -> {}
            }
        }
    }

    private fun showDeleteConfirmationDialog(notificationId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Notification")
            .setMessage("Are you sure you want to delete this notification?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteNotification(notificationId)
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}