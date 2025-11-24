package com.example.jiradashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.databinding.ItemHomeTaskBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.HomeTaskItem
import com.thehecotnha.myapplication.utils.priorityName
import java.util.*
import kotlin.collections.ArrayList


class HomeTaskAdapter(
    private var items: List<HomeTaskItem>,
    private val onItemClick: (HomeTaskItem) -> Unit
) : RecyclerView.Adapter<HomeTaskAdapter.ViewHolder>() {

    private val originalItems = ArrayList<HomeTaskItem>(items)

    inner class ViewHolder(private val binding: ItemHomeTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeTaskItem) {
            binding.itemTitle.text  = item.title
            binding.projectName.text = item.projName
            binding.tvDueDate.text   = CalendarDate(item.dueDate).calendar
            binding.assigneeName.text = item.assigneeName
            binding.priorityIcon.setImageResource(when (item.priority) {
                priorityName.HIGH -> R.drawable.chevron_double_up
                priorityName.MEDIUM -> R.drawable.equals
                priorityName.LOW -> R.drawable.chevron_double_down
                else -> R.drawable.equals
            })
            binding.priorityName.text = item.priority
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

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String?) {
        items = if (query.isNullOrEmpty()) {
            originalItems
        } else {
            val filteredList = ArrayList<HomeTaskItem>()
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            for (item in originalItems) {
                if (item.title.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                    item.projName.lowercase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredList.add(item)
                }
            }
            filteredList
        }
        notifyDataSetChanged()
    }
}