package com.thehecotnha.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thehecotnha.myapplication.databinding.ItemTaskBinding
import com.thehecotnha.myapplication.models.Task


class TaskDetailAdapter(
    private val items: List<Task>,
    private val onItemClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskDetailAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val b: ItemTaskBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun updateUIWith(task: Task) {
            b.tvTaskTitle.text = task.title
            b.tvTaskDescription.text = task.description

            b.root.setOnClickListener {
                onItemClicked(task)
            }
        }
    }

    // Tao giao dien cho tung item trong recycler view
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(view, parent, false)
        return TaskViewHolder(binding)
    }

    // Khi user luot den dau thi ham nay duoc goi de gan du lieu vao tung item den do
    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        holder.updateUIWith(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateTasks(newTasks: List<Task>) {
        (items as MutableList).clear()
        items.addAll(newTasks)
        this.notifyDataSetChanged()
    }
}