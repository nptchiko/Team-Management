package com.example.jiradashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thehecotnha.myapplication.databinding.ItemHomeTaskBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.HomeTaskItem


class HomeTaskAdapter(
    private val items: List<HomeTaskItem>,
    private val onItemClick: (HomeTaskItem) -> Unit
) : RecyclerView.Adapter<HomeTaskAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHomeTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.itemTitle
        val projName = binding.projectName
        val dueDate = binding.tvDueDate
        val assignee = binding.assigneeName

        fun bind(item: HomeTaskItem) {
            binding.itemTitle.text  = item.title
            binding.projectName.text = item.projName
            binding.tvDueDate.text   = CalendarDate(item.dueDate).calendar
            binding.assigneeName.text = item.assigneeName
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size
}