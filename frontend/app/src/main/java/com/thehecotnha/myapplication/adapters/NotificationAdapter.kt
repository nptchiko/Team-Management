package com.thehecotnha.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.databinding.ItemNotificationBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.Notification
import com.thehecotnha.myapplication.utils.NotificationType

class NotificationAdapter(
    private val notifications: List<Notification>,
    private val onItemClick: (Notification) -> Unit,
    private val onDeleteClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.tvNotificationText.text = notification.text
            binding.tvNotificationTime.text = notification.sendAt?.toDate()?.let {
                CalendarDate(it).calendar
            } ?: "Unknown time"

            // Set icon based on notification type
            val iconRes = when (NotificationType.values().getOrNull(notification.notificationType)) {
                NotificationType.ADDED_TO_PROJECT -> R.drawable.outline_add_24
                NotificationType.REMOVED_FROM_PROJECT -> R.drawable.cross
                NotificationType.ASSIGNED_TO_TASK -> R.drawable.outline_edit_24
                NotificationType.REMOVED_TO_TASK -> R.drawable.cross
                else -> R.drawable.ic_launcher_foreground
            }
            binding.ivNotificationIcon.setImageResource(iconRes)

            // Set background based on read status
            if (notification.isRead) {
                binding.root.alpha = 0.6f
            } else {
                binding.root.alpha = 1.0f
            }

            // Set sender name
            binding.tvSenderName.text = notification.senderName

            binding.root.setOnClickListener {
                onItemClick(notification)
            }

            binding.btnDeleteNotification.setOnClickListener {
                onDeleteClick(notification)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size
}