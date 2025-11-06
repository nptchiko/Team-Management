package com.thehecotnha.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.jiradashboard.QuickAccessAdapter.ViewHolder
import com.example.jiradashboard.RecentItemsAdapter
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.databinding.ItemProjectBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.ProjectItem


class ProjectAdapter(
    private val items: List<ProjectItem>
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {



    // Kotlin tao ItemProjectBinding tu dong lien ket voi item_project.xml
    // cho phep truy cap truc tiep den cac view (button, textView) trong layout thong qua no
    inner class ProjectViewHolder(private val binding: ItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun updateUIWith(project: ProjectItem) {

            binding.tvTitleProjectCard.text = project.title
            binding.tvStateProjectCard.text = project.state
            binding.tvTasksProjectCard.text = project.taskLefts.toString()
            binding.tvDateProjectCard.text = project.dueDate
            binding.progressBarProjectCard.progress = project.projectPercent


/*            // from the home => go the corresponding project
            binding.root.setOnClickListener {
                val action = HomeFragmentDirections.homeToProject(project)
                it.findNavController().navigate(action)
            }
            binding.executePendingBindings()*/
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

}