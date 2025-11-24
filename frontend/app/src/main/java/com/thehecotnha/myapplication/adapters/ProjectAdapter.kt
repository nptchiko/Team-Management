package com.thehecotnha.myapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.databinding.ItemProjectBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.ProjectItem
import java.util.*
import kotlin.collections.ArrayList


class ProjectAdapter(
    private var items: List<ProjectItem>, private val onItemClicked: (ProjectItem) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private val originalItems = ArrayList<ProjectItem>(items)

    // Kotlin tao ItemProjectBinding tu dong lien ket voi item_project.xml
    // cho phep truy cap truc tiep den cac view (button, textView) trong layout thong qua no
    inner class ProjectViewHolder(private val binding: ItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun updateUIWith(project: ProjectItem) {
            binding.tvTitleProjectCard.text = project.title
            binding.tvTasksLeft.text = project.taskLefts.toString()
            binding.tvDateProjectCard.text = project.dueDate
            binding.tvTasksProgress.text = project.projectPercent.toString() + "%"
            binding.progressBarProjectCard.progress = project.projectPercent
            binding.tvTeamCount.text = project.team.toString()

            // from the home => go the corresponding project
            binding.root.setOnClickListener {
                onItemClicked(project)
            }
        }
    }

    // Tao giao dien cho tung item trong recycler view
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ItemProjectBinding.inflate(view, parent, false)
        return ProjectViewHolder(binding)
    }

    // Khi user luot den dau thi ham nay duoc goi de gan du lieu vao tung item den do
    override fun onBindViewHolder(
        holder: ProjectViewHolder,
        position: Int
    ) {
        holder.updateUIWith(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String?) {
        items = if (query.isNullOrEmpty()) {
            originalItems
        } else {
            val filteredList = ArrayList<ProjectItem>()
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            for (item in originalItems) {
                if (item.title.lowercase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredList.add(item)
                }
            }
            filteredList
        }
        notifyDataSetChanged()
    }

}