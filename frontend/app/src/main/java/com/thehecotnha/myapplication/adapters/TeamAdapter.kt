package com.thehecotnha.myapplication.activities.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.databinding.ItemUserTeamBinding

class TeamAdapter(private val teamMembers: List<String>) :
    RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(val binding: ItemUserTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemUserTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val member = teamMembers[position]
        // Here you would typically load a user's profile image.
        // For now, we'll use a placeholder.
        holder.binding.userName.text = member
    }

    override fun getItemCount() = teamMembers.size
}